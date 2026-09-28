package explore.adminapi.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.views.ViewStats;
import explore.ViewStatsClient;
import explore.adminapi.dto.*;
import explore.adminapi.mappers.*;
import explore.adminapi.repositories.*;
import explore.dtos.CategoryDto;
import explore.dtos.CompilationDto;
import explore.dtos.EventFullDto;
import explore.exceptions.IncorrectRequestError;
import explore.exceptions.NotFoundError;
import explore.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static explore.validation.DateTimeFormat.DATE_TIME_PATTERN;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {
    private final AdminCategoryRepository categoryRepository;
    private final AdminEventRepository eventRepository;
    private final AdminUserRepository userRepository;
    private final AdminCompilationRepository compilationRepository;
    private final AdminLocationRepository locationRepository;
    private final AdminCategoryMapper categoryMapper;
    private final AdminEventMapper eventMapper;
    private final AdminUserMapper userMapper;
    private final AdminCompilationMapper compilationMapper;

    private final ViewStatsClient viewStatsClient;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    @Transactional
    public CategoryDto addCategory(NewCategoryDto dto) {
        log.info("Поступил запрос на добавление категории: " + dto);

        Category newCategory = new Category(null, dto.getName());
        Category addedCategory = categoryRepository.save(newCategory);

        log.info("Добавлена новая категория: " + addedCategory);

        CategoryDto resultDto = categoryMapper.toCategoryDto(addedCategory);

        log.info("Маппинг успешно завершен: " + resultDto);
        return resultDto;
    }

    @Transactional
    public void deleteCategory(int catId) {
        log.info("Поступил запрос на удаление категории с id = " + catId);

        Category foundCategory = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundError("Category with id=" + catId + " was not found"));

        if (eventRepository.existsByCategory(foundCategory)) {
            log.info("Удаление невозможно: к категории привязаны мероприятия");
            throw new IncorrectRequestError("The category is not empty");
        }
        categoryRepository.deleteById(catId);
        log.info("Категория удалена");
    }

    @Transactional
    public CategoryDto updateCategory(int catId, CategoryDto newDto) {
        log.info("Получен запрос на обновление сведений о категории с id=" + catId +
                ", новые сведения: " + newDto);

        Category foundCategory = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundError("Category with id=" + catId + " was not found"));

        categoryMapper.updateCategoryFromCategoryDto(newDto, foundCategory);

        Category updatedCategory = categoryRepository.save(foundCategory);

        log.info("Сведения обновлены: " + updatedCategory);
        return categoryMapper.toCategoryDto(updatedCategory);
    }

    public List<EventFullDto> getEventsFiltered(int[] users,
                                                String[] statesString,
                                                int[] categories,
                                                String rangeStart,
                                                String rangeEnd,
                                                int from,
                                                int size) {
        log.info("Поступил запрос на получение списка из " + size + " событий пользователей " + users
                + "в статусах " + statesString + "в категориях с id=" + categories
                + "с " + rangeStart + " по " + rangeEnd
                + ", не считая первые " + from + " событий.");

        Integer[] usersId = Arrays.stream(users)
                .boxed()
                .toArray(Integer[]::new);
        Integer[] categoriesId = Arrays.stream(categories)
                .boxed()
                .toArray(Integer[]::new);
        Set<State> statesSet = new HashSet<>();

        if (statesString != null) {
            for (int i = 0; i < statesString.length; i++) {
                statesSet.add(State.of(statesString[i]));
            }
        } else {
            statesSet.addAll(Arrays.stream(State.values()).toList());
        }

        State[] states = statesSet.toArray(new State[statesSet.size()]);
        LocalDateTime start;
        LocalDateTime end;
        List<Event> foundEvents;

        if (rangeStart == null) {
            start = LocalDateTime.now();
        } else {
            start = LocalDateTime.parse(rangeStart, formatter);
        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(rangeEnd, formatter);
            foundEvents = eventRepository.findFilteredEvents(usersId, states, categoriesId, start, end);
        } else {
            foundEvents = eventRepository.findFilteredUpcomingEvents(usersId, states, categoriesId, start);
        }

        if (foundEvents.isEmpty()) {
            log.info("События не найдены");
            return new ArrayList<>();
        }

        List<EventFullDto> eventResults = foundEvents.stream()
                .sorted(Comparator.comparing(Event::getEventDate))
                .skip(from)
                .limit(size)
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());

        log.info("Составление списка EventFullDto завершено. Формирование views");

        String[] uris = new String[eventResults.size()];

        for (int i = 0; i < eventResults.size(); i++) {
            String uri = "/events/" + eventResults.get(i).getId();
            uris[i] = uri;
        }

        Object body = viewStatsClient.getStats(rangeStart, rangeEnd, uris, false).getBody();

        log.info("Ответ модуля статистики получен");

        ObjectMapper mapper = new ObjectMapper();
        List<ViewStats> allViewStats = mapper.convertValue(body, new TypeReference<List<ViewStats>>() {});

        log.info("Загружены ViewStats по всем событиям.");

        Map<String, Integer> viewsMap = allViewStats.stream()
                .collect(Collectors.toMap(ViewStats::getUri, ViewStats::getHits));

        for (EventFullDto event : eventResults) {
            String uri = "/events/" + event.getId();

            if (viewsMap.containsKey(uri)) {
                event.setViews(viewsMap.get(uri));
                log.info("Для события с id =" + event.getId() + " добавлено кол-во просмотров");
            }
        }
        log.info("Загрузка views для всех событий завершена");
        return eventResults;
    }

    @Transactional
    public EventFullDto updateEvent(int eventId, UpdateEventAdminRequest adminRequest) {
        log.info("Поступил запрос на обновление сведений о событии id=" + eventId);

        LocalDateTime now = LocalDateTime.now();
        Event foundEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundError("Event with id=" + eventId + " was not found"));

        log.info("Событие найдено");
        if (foundEvent.getPublishedOn() != null && foundEvent.getPublishedOn().plusHours(1).isAfter(foundEvent.getEventDate())) {
            log.info("publishedOn менее чем за час до eventDate, обновление невозможно");
            throw new IncorrectRequestError("Cannot update the event because it's published " +
                    "less than an hour before the event date");
        }

        StateAction requestAction = adminRequest.getStateAction();

        if (requestAction != null) {
            if (requestAction.equals(StateAction.PUBLISH_EVENT) && !foundEvent.getState().equals(State.PENDING)) {
                log.info("Событие в статусе " + foundEvent.getState() + ", публикация невозможна");
                throw new IncorrectRequestError("Cannot publish the event because its state " +
                        "isn't \"pending\"");
            }
            if (requestAction.equals(StateAction.REJECT_EVENT) && foundEvent.getState().equals(State.PUBLISHED)) {
                log.info("Событие опубликовано ранее, отклонить его невозможно");
                throw new IncorrectRequestError("Cannot reject the event because it's already " +
                        "published");
            }
        }
        eventMapper.updateEventFromAdminRequest(adminRequest, foundEvent);
        log.info("Автоматическое обновление полей произведено");
        if (adminRequest.getCategory() != null) {
            Category newCategory = categoryRepository.findById(adminRequest.getCategory())
                    .orElseThrow(() -> new NotFoundError("Category with id=" + adminRequest.getCategory() + " was not found"));

            foundEvent.setCategory(newCategory);
            log.info("Категория обновлена");
        }
        if (adminRequest.getLocation() != null) {
            Location newLocation = new Location(
                    null,
                    adminRequest.getLocation().getLat(),
                    adminRequest.getLocation().getLon());
            Location savedLocation = locationRepository.save(newLocation);

            foundEvent.setLocation(savedLocation);
            log.info("Локация обновлена");

        }
        if (requestAction != null) {
            if (requestAction.equals(StateAction.PUBLISH_EVENT)) {
                foundEvent.setState(State.PUBLISHED);
                foundEvent.setPublishedOn(now);
                log.info("Статус изменен на PUBLISHED");
            } else if (requestAction.equals(StateAction.REJECT_EVENT)) {
                foundEvent.setState(State.CANCELED);
                log.info("Статус изменен на CANCELED");
            }
        }

        EventFullDto updatedEventDto = eventMapper.toEventFullDto(eventRepository.save(foundEvent));

        log.info("Информация о событии полностью обновлена. Формирование views...");

        String[] uris = new String[]{"/events/" + foundEvent.getId()};
        String rangeStart = foundEvent.getCreatedOn().format(formatter);
        String rangeEnd = now.format(formatter);
        Object body = viewStatsClient.getStats(rangeStart, rangeEnd, uris, false).getBody();

        log.info("Ответ модуля статистики получен");

        ObjectMapper mapper = new ObjectMapper();
        List<ViewStats> allViewStats = mapper.convertValue(body, new TypeReference<List<ViewStats>>() {});

        log.info("ViewStats загружены");

        int views = allViewStats.isEmpty() ? 0 : allViewStats.getFirst().getHits();

        updatedEventDto.setViews(views);
        log.info("Поле views у события обновлено");
        return updatedEventDto;
    }

    public List<UserDto> getUsersFiltered(int[] ids, int from, int size) {
        log.info("Поступил запрос на получение списка из " + size + " пользователей с id=" + ids
                + ", не считая первые " + from + "пользователей");

        List<User> foundUsers = new ArrayList<>();

        if (ids != null) {
            Integer[] usersId = Arrays.stream(ids)
                    .boxed()
                    .toArray(Integer[]::new);
            foundUsers = userRepository.findFilteredUsers(usersId);
        } else {
            foundUsers = userRepository.findAll();
        }

        if (foundUsers.isEmpty()) {
            log.info("Пользователи не найдены");
            return new ArrayList<>();
        }
        log.info("Найдено " + foundUsers.size() + " пользователей.");

        List<UserDto> userResults = foundUsers.stream()
                .map(user -> userMapper.toUserDto(user))
                .collect(Collectors.toList());

        return userResults;
    }

    @Transactional
    public UserDto addUser(NewUserRequest newUserRequest) {
        log.info("Поступил запрос на сохранение пользователя: " + newUserRequest);

        User newUser = new User(null, newUserRequest.getEmail(), newUserRequest.getName());
        User addedUser = userRepository.save(newUser);

        log.info("Добавлен новый пользователь: " + addedUser);
        return userMapper.toUserDto(addedUser);
    }

    @Transactional
    public void deleteUser(int userId) {
        log.info("Поступил запрос на удаление пользователя с id=" + userId);

        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            log.info("Пользователь удален");
        } else {
            log.info("Пользователь не найден");
            throw new NotFoundError("User with id=" + userId + " was not found");
        }
    }

    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newDto) {
        log.info("Поступил запрос на добавление подборки: " + newDto);

        Compilation newCompilation = new Compilation(null, newDto.getPinned(), newDto.getTitle(), new HashSet<>());

        if (newDto.getEvents() != null) {
            Set<Integer> events = new HashSet<>(newDto.getEvents());

            if (!events.isEmpty()) {
                log.info("В подборке имеются события с id=" + events);

                List<Event> foundEvents = eventRepository.findAllById(events);

                log.info("Найдено " + foundEvents.size() + " событий с указанными id");

                if (foundEvents.size() > 0) {
                    newCompilation.setEvents(Set.copyOf(foundEvents));
                }
            }
        }

        Compilation addedCompilation = compilationRepository.save(newCompilation);

        log.info("Подборка добавлена, присвоен id=" + addedCompilation.getId());
        return compilationMapper.toCompilationDto(addedCompilation);
    }

    @Transactional
    public void deleteCompilation(int compId) {
        log.info("Поступил запрос на удаление подборки с id=" + compId);

        if (compilationRepository.existsById(compId)) {
            compilationRepository.deleteById(compId);
            log.info("Подборка удалена");
        } else {
            log.info("Подборка не найдена");
            throw new NotFoundError("Compilation with id=" + compId + " was not found");
        }
    }

    @Transactional
    public CompilationDto updateCompilation(int compId, UpdateCompilationRequest compilationRequest) {
        log.info("Поступил запрос на обновление подборки с id=" + compId);

        Compilation foundCompilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundError("Compilation with id=" + compId + " was not found"));

        log.info("Найдена подборка: " + foundCompilation);
        compilationMapper.updateCompilationByAdmin(compilationRequest, foundCompilation);
        log.info("Автоматическое обновление (без поля events) произведено");

        List<Integer> newEvents = compilationRequest.getEvents();

        if (newEvents != null) {
            foundCompilation.getEvents().clear();
            if (!newEvents.isEmpty()) {
                List<Event> foundEvents = eventRepository.findAllById(newEvents);

                log.info("Для обновления найдено " + foundEvents.size() + " новых событий");
                foundCompilation.getEvents().addAll(foundEvents);
                log.info("Список событий обновлен");
            }
        }

        Compilation updatedCompilation = compilationRepository.save(foundCompilation);

        log.info("Данные в БД обновлены: " + updatedCompilation);
        return compilationMapper.toCompilationDto(updatedCompilation);
    }
}
