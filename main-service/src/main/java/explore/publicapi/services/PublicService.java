package explore.publicapi.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.hits.HitDto;
import dto.views.ViewStats;
import explore.HitClient;
import explore.ViewStatsClient;
import explore.dtos.CategoryDto;
import explore.dtos.CompilationDto;
import explore.dtos.EventFullDto;
import explore.dtos.EventShortDto;
import explore.exceptions.DateRequestException;
import explore.exceptions.NotFoundError;
import explore.models.Category;
import explore.models.Compilation;
import explore.models.Event;
import explore.models.State;
import explore.publicapi.mappers.PublicCategoryMapper;
import explore.publicapi.mappers.PublicCompilationMapper;
import explore.publicapi.mappers.PublicEventMapper;
import explore.publicapi.repositories.PublicCategoryRepository;
import explore.publicapi.repositories.PublicCompilationRepository;
import explore.publicapi.repositories.PublicEventRepository;
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
    private final PublicCompilationMapper compilationMapper;
    private final PublicCategoryMapper categoryMapper;
    private final PublicEventMapper eventMapper;

    private final ViewStatsClient viewStatsClient;
    private final HitClient hitClient;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

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

        log.info("Ответ модуля статистики получен. Поиск был с " + rangeStart + " по " + rangeEnd);

        ObjectMapper mapper = new ObjectMapper();
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

            ObjectMapper mapper = new ObjectMapper();
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
        List<Integer> categoriesId = Arrays.stream(categories).toList();

        if (rangeStart == null || rangeEnd == null) {
            log.info("Одно или оба поля rangeStart-rangeEnd равно null");
            if (onlyAvailable) {
                log.info("Поиск доступных событий после now");
                if (paid != null) {
                    foundEvents = eventRepository.findAvailableUpcompingEventsFilteredAndPaid(text,
                            categoriesId,
                            paid,
                            now,
                            State.PUBLISHED);
                } else {
                    foundEvents = eventRepository.findAvailableUpcompingEventsFilteredWithoutPaid(text,
                            categoriesId,
                            now,
                            State.PUBLISHED);
                }
            } else {
                log.info("Поиск всех событий после now");
                if (paid != null) {
                    foundEvents = eventRepository.findUpcompingEventsFilteredAndPaid(text,
                            categoriesId,
                            paid,
                            now,
                            State.PUBLISHED);
                } else {
                    foundEvents = eventRepository.findUpcompingEventsFilteredWithoutPaid(text,
                            categoriesId,
                            now,
                            State.PUBLISHED);
                }
            }
        } else {
            LocalDateTime start = LocalDateTime.parse(rangeStart, formatter);
            LocalDateTime end = LocalDateTime.parse(rangeEnd, formatter);

            if (start.isBefore(end)) {
                log.info("Диапазон дат задан верно");
                if (onlyAvailable) {
                    log.info("Поиск доступных событий в заданном диапазоне");
                    if (paid != null) {
                        foundEvents = eventRepository.findAvailableEventsFilteredDateInBetweenAndPaid(text,
                                categoriesId,
                                paid,
                                start,
                                end,
                                State.PUBLISHED);
                    } else {
                        foundEvents = eventRepository.findAvailableEventsFilteredDateInBetweenWithoutPaid(text,
                                categoriesId,
                                start,
                                end,
                                State.PUBLISHED);
                    }
                } else {
                    log.info("Поиск всех событий в заданном диапазоне");
                    if (paid != null) {
                        foundEvents = eventRepository.findEventsFilteredDateInBetweenAndPaid(text,
                                categoriesId,
                                paid,
                                start,
                                end,
                                State.PUBLISHED);
                    } else {
                        foundEvents = eventRepository.findEventsFilteredDateInBetweenWithoutPaid(text,
                                categoriesId,
                                start,
                                end,
                                State.PUBLISHED);
                    }
                }
            } else {
                throw new DateRequestException("Некорректный диапазон дат: rangeStart is NOT before rangeEnd");
            }
        }
        if (foundEvents.isEmpty()) {
            log.info("Ничего не найдено");
            return new ArrayList<>();
        }

        List<EventShortDto> foundDtos = foundEvents.stream()
                .skip(from)
                .limit(size)
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());

        log.info("Найдено событий: " + foundDtos.size() + ". Формирование views...");

        if (viewsStart == null) {
            viewsStart = foundEvents.stream()
                    .sorted(Comparator.comparing(Event::getCreatedOn))
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
        ObjectMapper mapper = new ObjectMapper();
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
        log.info("Поле views во всех событиях обновлено");
        if (sort.equalsIgnoreCase("EVENT_DATE")) {
            foundDtos.stream().sorted(Comparator.comparing(EventShortDto::getEventDate)).collect(Collectors.toList());
        } else {
            foundDtos.stream().sorted(Comparator.comparing(EventShortDto::getViews)).collect(Collectors.toList());
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
                now.format(formatter)
        );

        hitClient.postHit(hitDto);
        log.info("Информация о просмотре успешно отправлена в сервис статистики");

        String[] uris = new String[]{uri};
        Object body = viewStatsClient.getStats(rangeStart, rangeEnd, uris, true).getBody();
        ObjectMapper mapper = new ObjectMapper();
        List<ViewStats> allViewStats = mapper.convertValue(body, new TypeReference<List<ViewStats>>() {});
        int views = allViewStats.isEmpty() ? 0 : allViewStats.getFirst().getHits();

        log.info("ViewStats загружен");

        EventFullDto eventDto = eventMapper.toEventFullDto(event);

        eventDto.setViews(views);
        log.info("EventShortDto полностью загружен");
        return eventDto;
    }
}