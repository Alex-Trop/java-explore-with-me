package explore.adminapi.controllers;

import explore.adminapi.dto.*;
import explore.adminapi.services.AdminService;
import explore.dtos.CategoryDto;
import explore.dtos.CompilationDto;
import explore.dtos.EventFullDto;
import explore.validation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static explore.validation.DateTimeFormat.DATE_TIME_PATTERN;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService service;

    //КАТЕГОРИИ
    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> postCategory(@RequestBody @Valid NewCategoryDto dto) {
       return ResponseEntity
               .status(HttpStatus.CREATED)
               .body(service.addCategory(dto));
    }

    @DeleteMapping("/categories/{catId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int catId) {
        service.deleteCategory(catId);
        return ResponseEntity
                .noContent()
                .build();
    }

    @PatchMapping("/categories/{catId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable int catId, @RequestBody @Valid CategoryDto newDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.updateCategory(catId, newDto));
    }

    //СОБЫТИЯ
    @GetMapping("/events")
    public ResponseEntity<List<EventFullDto>> getEventsFiltered(@RequestParam(name = "users", required = false) int[] users,
                                                @RequestParam(name = "states", required = false) String[] states,
                                                @RequestParam(name = "categories", required = false) int[] categories,
                                                @RequestParam(name = "rangeStart", required = false) @ValidDateTimeFormat(pattern = DATE_TIME_PATTERN) String rangeStart,
                                                @RequestParam(name = "rangeEnd", required = false) @ValidDateTimeFormat(pattern = DATE_TIME_PATTERN) String rangeEnd,
                                                @RequestParam(name = "from", defaultValue = "0") int from,
                                                @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.getEventsFiltered(users, states, categories, rangeStart, rangeEnd, from, size));
    }

    @PatchMapping("/events/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable int eventId, @RequestBody @Valid UpdateEventAdminRequest adminRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.updateEvent(eventId, adminRequest));
    }

    //ПОЛЬЗОВАТЕЛИ
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsersFiltered(@RequestParam(name = "ids", required = false) int[] ids,
                                          @RequestParam(name = "from", defaultValue = "0") int from,
                                          @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.getUsersFiltered(ids, from, size));
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> postUser(@RequestBody @Valid NewUserRequest newUser) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.addUser(newUser));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable int userId) {
        service.deleteUser(userId);
        return ResponseEntity
                .noContent()
                .build();
    }

    //ПОДБОРКИ
    @PostMapping("/compilations")
    public ResponseEntity<CompilationDto> postCompilation(@RequestBody @Valid NewCompilationDto dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.addCompilation(dto));
    }

    @DeleteMapping("/compilations/{compId}")
    public ResponseEntity<Void> deleteCompilation(@PathVariable int compId) {
        service.deleteCompilation(compId);
        return ResponseEntity
                .noContent()
                .build();
    }

    @PatchMapping("/compilations/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(@PathVariable int compId,
                                                            @RequestBody @Valid UpdateCompilationRequest compilationRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(service.updateCompilation(compId, compilationRequest));
    }
}
