package explore.privateapi.controllers;

import explore.dtos.EventFullDto;
import explore.dtos.EventShortDto;
import explore.privateapi.dto.*;
import explore.privateapi.services.PrivateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateController {
    private final PrivateService service;

    //СОБЫТИЯ
    @GetMapping("/{userId}/events")
    public ResponseEntity<List<EventShortDto>> getEventsByUser(@PathVariable int userId,
                                                               @RequestParam(name = "from", defaultValue = "0") int from,
                                                               @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getEventsByUser(userId, from, size));
    }

    @PostMapping("/{userId}/events")
    public ResponseEntity<EventFullDto> postEvent(@PathVariable int userId,
                                                  @RequestBody @Valid NewEventDto eventDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.addEvent(userId, eventDto));
    }

    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> getEventById(@PathVariable int userId,
                                                     @PathVariable int eventId) {
        return ResponseEntity.ok(service.getEventById(userId, eventId));
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable int userId,
                                                    @PathVariable int eventId,
                                                    @RequestBody UpdateEventUserRequest request) {
        return ResponseEntity.ok(service.updateEvent(userId, eventId, request));
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequestsByInitiator(@PathVariable int userId,
                                                                    @PathVariable int eventId) {
        return ResponseEntity.ok(service.getRequestsByInitiator(userId, eventId));
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateUserRequest(@PathVariable int userId,
                                                                            @PathVariable int eventId,
                                                                            @RequestBody EventRequestStatusUpdateRequest request) {
        return ResponseEntity.ok(service.updateUserRequest(userId, eventId, request));
    }

    //ЗАЯВКИ НА УЧАСТИЕ
    @GetMapping("/{userId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getAllRequestsByUser(@PathVariable int userId) {
        return ResponseEntity.ok(service.getAllRequestsByUser(userId));
    }

    @PostMapping("/{userId}/requests")
    public ResponseEntity<ParticipationRequestDto> sendRequest(@PathVariable int userId,
                                                               @RequestParam(name = "eventId") int eventId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.addRequest(userId, eventId));
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable int userId,
                                                                 @PathVariable int requestId) {
        return ResponseEntity.ok(service.cancelRequest(userId, requestId));
    }
}
