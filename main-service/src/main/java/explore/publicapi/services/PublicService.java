package explore.publicapi.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.hits.HitDto;
import dto.views.ViewStats;
import explore.HitClient;
import explore.ViewStatsClient;
import explore.dtos.*;
import explore.exceptions.DateRequestException;
import explore.exceptions.NotFoundError;
import explore.models.*;
import explore.publicapi.mappers.PublicCategoryMapper;
import explore.publicapi.mappers.PublicCommentMapper;
import explore.publicapi.mappers.PublicCompilationMapper;
import explore.publicapi.mappers.PublicEventMapper;
import explore.publicapi.repositories.PublicCategoryRepository;
import explore.publicapi.repositories.PublicCommentRepository;
import explore.publicapi.repositories.PublicCompilationRepository;
import explore.publicapi.repositories.event.PublicEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static explore.validation.DateTimeFormat.DATE_TIME_PATTERN;

@Service
@Slf4j
@RequiredArgsConstructor
public class PublicService {
    private final PublicCompilationRepository compilationRepository;
    private final PublicCategoryRepository categoryRepository;
    private final PublicEventRepository eventRepository;
    private final PublicCommentRepository commentRepository;
    private final PublicCompilationMapper compilationMapper;
    private final PublicCategoryMapper categoryMapper;
    private final PublicEventMapper eventMapper;
    private final PublicCommentMapper commentMapper;

    private final ViewStatsClient viewStatsClient;
    private final HitClient hitClient;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    private final ObjectMapper mapper = new ObjectMapper();

    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        log.info("Поступил запрос на получение " + size + " подборок, не считая первые " + from + "со статусом " +
                "pinned=" + pinned);

        List<Compilation> foundCompilations = new ArrayList<>();

        if (pinned != null) {
            foundCompilations = compilationRepository.findAllByPinned(pinned);
        } else {
            foundCompilations = compilationRepository.findAll();
        }

        if (foundCompilations.isEmpty()) {
            log.info("Ничего не найдено");
            return new ArrayList<>();
        }
        List<CompilationDto> compResults = foundCompilations.stream()
                .sorted(Comparator.comparing(Compilation::getId))
                .skip(from)
                .limit(size)
                .map(compilationMapper::toCompilationDto)
                .collect(Collectors.toList());

        log.info("Составление списка подборок завершено. Обновление поля views во всех событиях...");

        List<String> urisList = new ArrayList<>();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();

        for (int i = 0; i < compResults.size(); i++) {
            List<EventShortDto> compEvents = compResults.get(i).getEvents();

            if (compEvents != null && !compEvents.isEmpty()) {
                for (int j = 0; j < compEvents.size(); j++) {
                    String uri = "/events/" + compEvents.get(j).getId();
                    urisList.add(uri);
                    if (compEvents.get(j).getEventDate().isBefore(start)) {
                        start = compEvents.get(j).getEventDate();
                    }
                    if (compEvents.get(j).getEventDate().isAfter(end)) {
                        end = compEvents.get(j).getEventDate();
                    }
                }
            }
        }

        String rangeStart = start.format(formatter);
        String rangeEnd = end.format(formatter);
        String[] uris = urisList.toArray(new String[0]);
        Object body = viewStatsClient.getStats(rangeStart, rangeEnd, uris, false).getBody();

        if (body != null) {
            log.info("Ответ модуля статистики получен. Поиск был с " + rangeStart + " по " + rangeEnd);

            List<ViewStats> allViewStats = mapper.convertValue(body, new TypeReference<List<ViewStats>>() {
            });

            log.info("ViewStats загружены");

            Map<String, Integer> viewsMap = allViewStats.stream()
                    .collect(Collectors.toMap(ViewStats::getUri, ViewStats::getHits));

            for (CompilationDto comp : compResults) {
                List<EventShortDto> compEvents = comp.getEvents();

                if (compEvents != null && !compEvents.isEmpty()) {
                    for (EventShortDto event : compEvents) {
                        String uri = "/events/" + event.getId();

                        if (viewsMap.containsKey(uri)) {
                            event.setViews(viewsMap.get(uri));
                            log.info("Для события с id =" + event.getId() + " добавлено кол-во просмотров");
                        }
                    }
                }
                comp.setEvents(compEvents);
                log.info("Для подборки с id=" + comp.getId() + "просмотры фильмов обновлены");
            }
        } else {
            log.info("Получить статистику не удалось");
        }

        log.info("Для всех подборок обновлено количество просмотров фильмов");
        return compResults;
    }

    public CompilationDto getCompilationById(int id) {
        log.info("Поступил запрос на поиск подборки id=" + id);

        Compilation foundComp = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Compilation with id=" + id + " was not found."));

        CompilationDto compDto = compilationMapper.toCompilationDto(foundComp);
        List<EventShortDto> compEvents = compDto.getEvents();

        if (compEvents != null && !compEvents.isEmpty()) {
            log.info("Формирование views для вложенных фильмов...");

            List<String> urisList = new ArrayList<>();
            LocalDateTime start = LocalDateTime.now();
            LocalDateTime end = LocalDateTime.now();

            for (EventShortDto event : compEvents) {
                String uri = "/events/" + event.getId();

                urisList.add(uri);
                if (event.getEventDate().isBefore(start)) {
                    start = event.getEventDate();
                }
                if (event.getEventDate().isAfter(end)) {
                    end = event.getEventDate();
                }
            }

            String rangeStart = start.format(formatter);
            String rangeEnd = end.format(formatter);
            String[] uris = urisList.toArray(new String[0]);
            Object body = viewStatsClient.getStats(rangeStart, rangeEnd, uris, false).getBody();

            log.info("Ответ модуля статистики получен. Поиск был с " + rangeStart + " по " + rangeEnd);

            List<ViewStats> allViewStats = mapper.convertValue(body, new TypeReference<List<ViewStats>>() {
            });

            log.info("ViewStats загружены");

            Map<String, Integer> viewsMap = allViewStats.stream()
                    .collect(Collectors.toMap(ViewStats::getUri, ViewStats::getHits));

            for (EventShortDto event : compEvents) {
                String uri = "/events/" + event.getId();

                if (viewsMap.containsKey(uri)) {
                    event.setViews(viewsMap.get(uri));
                    log.info("Для события с id =" + event.getId() + " добавлено кол-во просмотров");
                }
            }
            compDto.setEvents(compEvents);
            log.info("Поле views Для всех фильмов в подборке обновлено");
        }
        return compDto;
    }

    public List<CategoryDto> getCategories(int from, int size) {
        log.info("Поступил запрос на получение списка из " + size + " категорий, не считая первые " + from);

        List<Category> foundCategories = categoryRepository.findAll();

        if (foundCategories.isEmpty()) {
            log.info("Ничего не найдено");
            return new ArrayList<>();
        }
        log.info("Найдено категорий: " + foundCategories.size());

        List<CategoryDto> categoryDtos = foundCategories.stream()
                .sorted(Comparator.comparing(Category::getId))
                .skip(from)
                .limit(size)
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());

        return categoryDtos;
    }

    public CategoryDto getCategoryById(int id) {
        log.info("Поступил запрос на получение категории с id=" + id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Category with id=" + id + " was not found."));
        CategoryDto categoryDto = categoryMapper.toCategoryDto(category);

        log.info("Найдена категория: " + categoryDto);
        return categoryDto;
    }

    public List<EventShortDto> getEventsFiltered(String text,
                                                Integer[] categories,
                                                Boolean paid,
                                                String rangeStart,
                                                String rangeEnd,
                                                boolean onlyAvailable,
                                                String sort,
                                                int from,
                                                int size,
                                                String ip) {
        log.info("Поступил запрос на получение списка событий с фильтрами: text=" + text + "; categories="
                + categories + "; paid=" + paid + "; start=" + rangeStart + "; end=" + rangeEnd + "; onlyAvailable=" + onlyAvailable
                + "; sort=" + sort + "; from=" + from + "; size=" + size);

        List<Event> foundEvents;
        String viewsStart = rangeStart;
        LocalDateTime now = LocalDateTime.now();
        String viewsEnd = now.format(formatter);
        List<Integer> categoriesId;

        if (categories == null || categories.length == 0) {
            categoriesId = null;
        } else {
            categoriesId = Arrays.stream(categories).toList();
        }

        if (rangeStart == null || rangeEnd == null) {
            log.info("Одно или оба поля rangeStart-rangeEnd равно null");
            if (onlyAvailable) {
                log.info("Поиск доступных событий после now");

                foundEvents = eventRepository.findAvailableUpcompingEventsFiltered(text,
                        categoriesId,
                        paid,
                        now,
                        State.PUBLISHED,
                        from,
                        size);
            } else {
                log.info("Поиск всех событий после now");

                foundEvents = eventRepository.findUpcompingEventsFiltered(text,
                        categoriesId,
                        paid,
                        now,
                        State.PUBLISHED,
                        from,
                        size);
            }
        } else {
            LocalDateTime start = LocalDateTime.parse(rangeStart, formatter);
            LocalDateTime end = LocalDateTime.parse(rangeEnd, formatter);

            if (start.isBefore(end)) {
                log.info("Диапазон дат задан верно");
                if (onlyAvailable) {
                    log.info("Поиск доступных событий в заданном диапазоне");

                    foundEvents = eventRepository.findAvailableEventsFilteredDateInBetween(text,
                            categoriesId,
                            paid,
                            start,
                            end,
                            State.PUBLISHED,
                            from,
                            size);
                } else {
                    log.info("Поиск всех событий в заданном диапазоне");

                    foundEvents = eventRepository.findEventsFilteredDateInBetween(text,
                            categoriesId,
                            paid,
                            start,
                            end,
                            State.PUBLISHED,
                            from,
                            size);
                }
            } else {
                throw new DateRequestException("Некорректный диапазон дат: rangeStart is NOT before rangeEnd");
            }
        }
        if (foundEvents == null || foundEvents.isEmpty()) {
            log.info("Ничего не найдено");
            return new ArrayList<>();
        }

        List<EventShortDto> foundDtos = foundEvents.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());

        log.info("Найдено событий: " + foundDtos.size() + ". Формирование views...");

        if (viewsStart == null) {
            viewsStart = foundEvents.stream()
                    .findFirst()
                    .get()
                    .getCreatedOn()
                    .format(formatter);
        }

        String[] uris = new String[foundDtos.size()];

        for (int i = 0; i < foundDtos.size(); i++) {
            String uri = "/events/" + foundDtos.get(i).getId();
            uris[i] = uri;
            HitDto hitDto = new HitDto(
                    "ewm-main-service",
                    uri,
                    ip,
                    now.format(formatter)
            );

            hitClient.postHit(hitDto);
        }
        Object body = viewStatsClient.getStats(viewsStart, viewsEnd, uris, true).getBody();

        if (body != null) {
            List<ViewStats> allViewStats = mapper.convertValue(body, new TypeReference<List<ViewStats>>() {
            });

            log.info("ViewStats загружены");

            Map<String, Integer> viewsMap = allViewStats.stream()
                    .collect(Collectors.toMap(ViewStats::getUri, ViewStats::getHits));

            for (EventShortDto dto : foundDtos) {
                String uri = "/events/" + dto.getId();

                if (viewsMap.containsKey(uri)) {
                    dto.setViews(viewsMap.get(uri));
                    log.info("Для события с id=" + dto.getId() + " поле views обновлено");
                }
            }
        } else {
            log.info("Получить статистику не удалось");
        }

        log.info("Поле views во всех событиях обновлено");
        if (sort != null) {
            if (sort.equalsIgnoreCase("EVENT_DATE")) {
                foundDtos.sort(Comparator.comparing(EventShortDto::getEventDate));
            } else {
                foundDtos.sort(Comparator.comparing(EventShortDto::getViews));
            }
        } else {
            foundDtos.sort(Comparator.comparing(EventShortDto::getViews));
        }
        return foundDtos;
    }

    public EventFullDto getEventById(Integer id, String ip) {
        log.info("Поступил запрос на получение информации о событии id=" + id);

        Event event = eventRepository.findByIdAndState(id, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundError("Event with id=" + id + " was not found."));
        String rangeStart = event.getCreatedOn().format(formatter);
        LocalDateTime now = LocalDateTime.now();
        String rangeEnd = now.format(formatter);
        String uri = "/events/" + event.getId();
        HitDto hitDto = new HitDto(
                "ewm-main-service",
                uri,
                ip,
                rangeEnd
        );

        hitClient.postHit(hitDto);
        log.info("Информация о просмотре успешно отправлена в сервис статистики");

        String[] uris = new String[]{uri};
        Object body = viewStatsClient.getStats(rangeStart, rangeEnd, uris, true).getBody();
        EventFullDto eventDto = eventMapper.toEventFullDto(event);

        if (body != null) {
            List<ViewStats> allViewStats = mapper.convertValue(body, new TypeReference<List<ViewStats>>() {});
            int views = allViewStats.isEmpty() ? 0 : allViewStats.getFirst().getHits();

            log.info("ViewStats загружен");
            eventDto.setViews(views);
        }
        log.info("EventShortDto полностью загружен");
        return eventDto;
    }

    public List<CommentDto> getComments(int eventId, String text) {
        log.info("Запрос на получение комментариев к событию id={} с text={}", eventId, text);
        if (!eventRepository.existsByIdAndState(eventId, State.PUBLISHED)) {
            throw new NotFoundError("Событие не найдено");
        }

        List<Comment> foundComments = commentRepository.findAllByEventIdAndText(eventId, text);

        if (foundComments.isEmpty()) {
            log.info("Комментарии отсутствуют");
            return new ArrayList<>();
        }

        List<CommentDto> results = foundComments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());

        log.info("Найдено {} комментариев. Маппинг завершен", results.size());
        return results;
    }
}