package explore.privateapi.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.views.ViewStats;
import explore.ViewStatsClient;
import explore.dtos.EventFullDto;
import explore.dtos.EventShortDto;
import explore.dtos.LocationDto;
import explore.exceptions.DateRequestException;
import explore.exceptions.IncorrectRequestError;
import explore.exceptions.NotFoundError;
import explore.models.*;
import explore.privateapi.dto.*;
import explore.privateapi.mappers.PrivateEventMapper;
import explore.privateapi.repositories.*;
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
public class PrivateService {
    private final PrivateEventRepository eventRepository;
    private final PrivateUserRepository userRepository;
    private final PrivateCategoryRepository categoryRepository;
    private final PrivateLocationRepository locationRepository;
    private final PrivateRequestRepository requestRepository;
    private final PrivateEventMapper eventMapper;

    private final ViewStatsClient viewStatsClient;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    public List<EventShortDto> getEventsByUser(int userId, int from, int size) {
        log.info("Поступил запрос на получение списка из " + size + " событий, добавленных" +
                "пользователем с id=" + userId + ", не считая первые " + from);

        List<Event> foundEvents = eventRepository.findAllByInitiator(userId);

        if (foundEvents.isEmpty()) {
            log.info("Событий не найдено");
            return new ArrayList<>();
        }
        log.info("Найдено событий:" + foundEvents.size());

        List<Event> filteredEvents = foundEvents.stream()
                .sorted(Comparator.comparing(Event::getCreatedOn))
                .collect(Collectors.toList());
        String rangeStart = filteredEvents.getFirst().getCreatedOn().format(formatter);
        String rangeEnd = LocalDateTime.now().format(formatter);
        List<EventShortDto> eventResults = filteredEvents.stream()
                .skip(from)
                .limit(size)
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());

        log.info("Составление списка EventShortDto завершено. Формирование views");

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

        for (EventShortDto event : eventResults) {
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
    public EventFullDto addEvent(int userId, NewEventDto dto) {
        log.info("Поступил запрос на создание события от пользователя id=" + userId);

        LocalDateTime now = LocalDateTime.now();
        Category eventCategory = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundError("Category with id=" + dto.getCategory() + " was not found."));
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundError("User with id=" + userId + " was not found."));
        LocalDateTime eventDate = LocalDateTime.parse(dto.getEventDate(), formatter);

        if (eventDate.isBefore(now.plusHours(2))) {
            log.info("Дата и время на которые намечено событие не может быть раньше, " +
                    "чем через два часа от текущего момента: " + now.format(formatter));
            throw new DateRequestException("Field: eventDate. Error: Дата и время на которые намечено событие " +
                    "не может быть раньше, чем через два часа от текущего момента");
        }

        LocationDto eventLocation = dto.getLocation();
        Location location = locationRepository.findByLatAndLon(eventLocation.getLat(), eventLocation.getLon())
                .orElse(locationRepository.save(new Location(
                        null,
                        eventLocation.getLat(),
                        eventLocation.getLon())));
        Event newEvent = new Event(
                null,
                dto.getAnnotation(),
                eventCategory,
                dto.getDescription(),
                eventDate,
                location,
                dto.getPaid() == null ? false : dto.getPaid(),
                dto.getParticipantLimit(),
                dto.getRequestModeration() == null ? true : dto.getRequestModeration(),
                dto.getTitle(),
                now,
                initiator,
                null,
                State.PENDING,
                0
        );
        Event savedEvent = eventRepository.save(newEvent);

        log.info("Добавлено новое событие с id =" + savedEvent.getId());

        EventFullDto eventDto = eventMapper.toEventFullDto(savedEvent);

        log.info("Маппинг сохраненного события прошел успешно");
        return eventDto;
    }

    public EventFullDto getEventById(int userId, int eventId) {
        log.info("Поступил запрос на получение информации о событии id=" + eventId + " От пользователя id=" + userId);

        LocalDateTime now = LocalDateTime.now();
        Event foundEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundError("Event with id=" + eventId + " was not found."));

        if (foundEvent.getInitiator().getId() != userId) {
            log.info("Пользователь не является инициатором найденного события");
            throw new IncorrectRequestError("User is not the initiator of this event.");
        }
        log.info("Событие найдено, проверка инициатора успешно пройдена. Формирование views...");

        String[] uris = new String[]{"/events/" + foundEvent.getId()};
        String rangeStart = foundEvent.getCreatedOn().format(formatter);
        String rangeEnd = now.format(formatter);
        Object body = viewStatsClient.getStats(rangeStart, rangeEnd, uris, false).getBody();

        log.info("Ответ модуля статистики получен");

        ObjectMapper mapper = new ObjectMapper();
        List<ViewStats> allViewStats = mapper.convertValue(body, new TypeReference<List<ViewStats>>() {});

        log.info("ViewStats загружены");

        EventFullDto foundEventDto = eventMapper.toEventFullDto(foundEvent);
        int views = allViewStats.isEmpty() ? 0 : allViewStats.getFirst().getHits();

        foundEventDto.setViews(views);
        log.info("Поле views у события обновлено");
        return foundEventDto;
    }

    @Transactional
    public EventFullDto updateEvent(int userId, int eventId, UpdateEventUserRequest request) {
        log.info("Поступил запрос на обновление события id =" + eventId + " от пользователя id=" + userId);

        LocalDateTime now = LocalDateTime.now();
        Event foundEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundError("Event with id=" + eventId + " was not found."));
        LocationDto eventLocation = request.getLocation();

        if (foundEvent.getInitiator().getId() != userId) {
            log.info("Пользователь не является инициатором найденного события");
            throw new IncorrectRequestError("User is not the initiator of this event.");
        }
        if (foundEvent.getState().equals(State.PUBLISHED)) {
            throw new IncorrectRequestError("Only pending or canceled events can be changed");
        }
        if (request.getEventDate() != null) {
            if (LocalDateTime.parse(request.getEventDate(), formatter).isBefore(now.plusHours(2))) {
                log.info("Дата и время на которые намечено событие не может быть раньше, " +
                        "чем через два часа от текущего момента: " + now.format(formatter));
                throw new DateRequestException("Field: eventDate. Error: Дата и время на которые намечено событие " +
                        "не может быть раньше, чем через два часа от текущего момента");
            }
        }
        log.info("Событие найдено, проверка инициатора и статуса публикации успешно пройдена.");
        eventMapper.updateEventFromUserRequest(request, foundEvent);
        log.info("Автообновление произведено. Проверка полей category, location, stateAction...");
        if (request.getCategory() != null) {
            Category newCategory = categoryRepository.findById(request.getCategory())
                    .orElseThrow(() -> new NotFoundError("Category with id=" + request.getCategory() + " was not found"));
            foundEvent.setCategory(newCategory);
            log.info("Категория обновлена");
        }
        if (eventLocation != null) {
            Location location = locationRepository.findByLatAndLon(eventLocation.getLat(), eventLocation.getLon())
                    .orElse(locationRepository.save(new Location(
                            null,
                            eventLocation.getLat(),
                            eventLocation.getLon())));

            foundEvent.setLocation(location);
            log.info("Локация обновлена");
        }
        if (request.getStateAction() != null) {
            if (request.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                foundEvent.setState(State.PENDING);
            } else if (request.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
                foundEvent.setState(State.CANCELED);
            }
        }

        EventFullDto foundEventDto = eventMapper.toEventFullDto(eventRepository.save(foundEvent));

        log.info("Обновление завершено. Формирование views...");

        String[] uris = new String[]{"/events/" + foundEvent.getId()};
        String rangeStart = foundEvent.getCreatedOn().format(formatter);
        String rangeEnd = now.format(formatter);
        Object body = viewStatsClient.getStats(rangeStart, rangeEnd, uris, false).getBody();

        log.info("Ответ модуля статистики получен");

        ObjectMapper mapper = new ObjectMapper();
        List<ViewStats> allViewStats = mapper.convertValue(body, new TypeReference<List<ViewStats>>() {});

        log.info("ViewStats загружены");

        int views = allViewStats.isEmpty() ? 0 : allViewStats.getFirst().getHits();

        foundEventDto.setViews(views);
        log.info("Поле views у события обновлено");
        return foundEventDto;
    }

    public List<ParticipationRequestDto> getRequestsByInitiator(int userId, int eventId) {
        log.info("Поступил запрос на получение запросов на участие в событии id=" + eventId + " от инициатора id=" + userId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundError("Event with id=" + eventId + " was not found."));

        if (event.getInitiator().getId() != userId) {
            throw new NotFoundError("Событий с таким id и инициатором не найдено");
        }

        List<ParticipationRequest> eventRequests = requestRepository.findAllByEventId(eventId);

        if (eventRequests.isEmpty()) {
            log.info("Ничего не найдено");
            return new ArrayList<>();
        }
        log.info("Найдено заявок: " + eventRequests.size());

        List<ParticipationRequestDto> requestsDto = eventRequests.stream()
                .map(request -> new ParticipationRequestDto(
                        request.getCreated().format(formatter),
                        eventId,
                        request.getId(),
                        request.getRequester().getId(),
                        request.getStatus().name()))
                .collect(Collectors.toList());

        log.info("Формирование списка DTO завершено");
        return requestsDto;
    }

    @Transactional
    public EventRequestStatusUpdateResult updateUserRequest(int userId, int eventId, EventRequestStatusUpdateRequest request) {
        log.info("От пользователя id=" + userId + " поступил запрос на обновление статуса заявок по событию id=" + eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundError("Event with id=" + eventId + " was not found."));
        Status newStatus = Status.of(request.getStatus());

        if (event.getInitiator().getId() != userId) {
            throw new NotFoundError("Событий с таким id и инициатором не найдено");
        }

        List<ParticipationRequest> eventRequests = requestRepository.findAllByIdIn(request.getRequestIds());

        log.info("По заданному списку requestIds найдено заявок: " + eventRequests.size());

        List<ParticipationRequest> wrongStatusRequests = eventRequests.stream()
                .filter(eventRequest -> !eventRequest.getStatus().equals(Status.PENDING))
                .collect(Collectors.toList());

        if (!wrongStatusRequests.isEmpty()) {
            throw new IncorrectRequestError("Wrong status for requestIds");
        }

        List<ParticipationRequestDto> requestsDto;
        EventRequestStatusUpdateResult updateResult;

        if (newStatus.equals(Status.CONFIRMED)) {
            log.info("Запрос на одобрение заявок");
            if (event.getParticipantLimit() == 0) {
                log.info("Производится одобрение всех заявок...");

                int confirmedRequests = event.getConfirmedRequests();

                event.setConfirmedRequests(confirmedRequests + eventRequests.size());
                eventRepository.save(event);
                log.info("Поле confirmedRequests у event обновлено");
                eventRequests.forEach(eventRequest -> eventRequest.setStatus(Status.CONFIRMED));

                List<ParticipationRequest> savedRequests = requestRepository.saveAll(eventRequests);

                log.info("Статус всех заявок успешно изменен на CONFIRMED");

                requestsDto = savedRequests.stream()
                        .map(savedRequest -> new ParticipationRequestDto(
                                savedRequest.getCreated().format(formatter),
                                eventId,
                                savedRequest.getId(),
                                savedRequest.getRequester().getId(),
                                savedRequest.getStatus().name()
                        ))
                        .collect(Collectors.toList());
                updateResult = new EventRequestStatusUpdateResult(requestsDto, new ArrayList<>());
            } else {
                log.info("Производится одобрение заявок в пределах лимита на участие...");

                int confirmedRequests = event.getConfirmedRequests();
                int participantLimit = event.getParticipantLimit();

               for (int i = 0; i < eventRequests.size(); i++) {
                   if (confirmedRequests < participantLimit) {
                       eventRequests.get(i).setStatus(Status.CONFIRMED);
                       confirmedRequests++;
                   } else {
                       throw new IncorrectRequestError("Лимит участия превышен");
                   }
               }
               event.setConfirmedRequests(confirmedRequests);
               eventRepository.save(event);

               List<ParticipationRequest> savedRequests = requestRepository.saveAll(eventRequests);

               log.info("Изменение статуса заявок успешно произведено");

               List<ParticipationRequestDto> confirmed = savedRequests.stream()
                       .filter(confirmedRequest -> confirmedRequest.getStatus().equals(Status.CONFIRMED))
                       .map(confirmedRequest -> new ParticipationRequestDto(
                               confirmedRequest.getCreated().format(formatter),
                               eventId,
                               confirmedRequest.getId(),
                               confirmedRequest.getRequester().getId(),
                               Status.CONFIRMED.name()
                       ))
                       .collect(Collectors.toList());
               updateResult = new EventRequestStatusUpdateResult(confirmed, new ArrayList<>());
            }
        } else {
            log.info("Запрос на отклонение заявок");

            int currentConfirmedRequests = event.getConfirmedRequests();
            int finalConfirmedRequests = currentConfirmedRequests  - eventRequests.size();

            if (finalConfirmedRequests <= 0) {
                event.setConfirmedRequests(0);
            } else {
                event.setConfirmedRequests(finalConfirmedRequests);
            }
            eventRepository.save(event);
            log.info("Поле confirmedRequests у event обновлено");
            eventRequests.forEach(eventRequest -> eventRequest.setStatus(Status.REJECTED));

            List<ParticipationRequest> savedRequests = requestRepository.saveAll(eventRequests);
            requestsDto = eventRequests.stream()
                    .map(foundRequest -> new ParticipationRequestDto(
                                foundRequest.getCreated().format(formatter),
                                eventId,
                                foundRequest.getId(),
                                foundRequest.getRequester().getId(),
                                Status.REJECTED.name())
                    )
                    .collect(Collectors.toList());

            log.info("Статус заявок успешно изменен на REJECTED");

            updateResult = new EventRequestStatusUpdateResult(new ArrayList<>(), requestsDto);
        }
        return updateResult;
    }

    public List<ParticipationRequestDto> getAllRequestsByUser(int userId) {
        log.info("Поступил запрос на получение всех заявок пользователя id=" + userId);

        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundError("User with id=" + userId + " was not found."));
        List<ParticipationRequest> eventRequests = requestRepository.findAllByRequester(userId);

        if (eventRequests.isEmpty()) {
            log.info("Ничего не найдено");
            return new ArrayList<>();
        }
        log.info("Найдено заявок: " + eventRequests.size());

        List<ParticipationRequestDto> userRequests = eventRequests.stream()
                .map(userRequest -> new ParticipationRequestDto(
                        userRequest.getCreated().format(formatter),
                        userRequest.getEvent().getId(),
                        userRequest.getId(),
                        userId,
                        userRequest.getStatus().name()))
                .collect(Collectors.toList());
        return userRequests;
    }

    @Transactional
    public ParticipationRequestDto addRequest(int userId, int eventId) {
        log.info("Поступил запрос от пользователя с id=" + userId + " на участие в событии id=" + eventId);

        LocalDateTime now = LocalDateTime.now();
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundError("User with id=" + userId + " was not found."));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundError("Event with id=" + eventId + " was not found."));
        List<ParticipationRequest> eventRequests = requestRepository.findAllByEventId(eventId);

        if (event.getInitiator().getId() == userId) {
            throw new IncorrectRequestError("Инициатор не может участвовать в своем событии");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new IncorrectRequestError("Нельзя участвовать в неопубликованном событии");
        }
        if (!eventRequests.isEmpty()) {
            for (ParticipationRequest request :  eventRequests) {
                if (request.getRequester().getId() == userId) {
                    throw new IncorrectRequestError("Нельзя добавить повторный запрос");
                }
            }
            if (event.getConfirmedRequests() == event.getParticipantLimit() && event.getParticipantLimit() > 0) {
                throw new IncorrectRequestError("Достигнуто максимальное количество участников");
            }
        }
        log.info("Проверка запроса завершена");

        Status newStatus = Status.PENDING;

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            newStatus = Status.CONFIRMED;
        }

        ParticipationRequest newRequest = new ParticipationRequest(
                null,
                event,
                requester,
                now,
                newStatus
        );
        ParticipationRequest savedRequest = requestRepository.save(newRequest);

        log.info("Заявка на участие отправлена: id=" + savedRequest.getId());

        if (savedRequest.getStatus().equals(Status.CONFIRMED)) {
            int confirmedRequests = event.getConfirmedRequests();

            confirmedRequests++;
            event.setConfirmedRequests(confirmedRequests);
            eventRepository.save(event);
            log.info("Поле confirmedRequests в БД у события обновлено");
        }
        return new ParticipationRequestDto(
                now.format(formatter),
                eventId,
                savedRequest.getId(),
                userId,
                savedRequest.getStatus().name()
        );
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(int userId, int requestId) {
        log.info("Поступил запрос на удаление заявки на участие id=" + requestId + " от пользователя id=" + userId);

        ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundError("Request with id=" + requestId + " was not found."));

        requestRepository.deleteById(requestId);
        log.info("Запрос успешно удален");
        if (request.getStatus().equals(Status.CONFIRMED)) {
            int confirmedRequests = request.getEvent().getConfirmedRequests();

            confirmedRequests--;
            request.getEvent().setConfirmedRequests(confirmedRequests);
            eventRepository.save(request.getEvent());
            log.info("Количество заявок у события обновлено");
        }
        return new ParticipationRequestDto(
                request.getCreated().format(formatter),
                request.getEvent().getId(),
                requestId,
                userId,
                Status.CANCELED.name()
        );
    }
}
