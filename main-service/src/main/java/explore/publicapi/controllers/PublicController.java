package explore.publicapi.controllers;

import explore.dtos.CategoryDto;
import explore.dtos.CompilationDto;
import explore.dtos.EventFullDto;
import explore.dtos.EventShortDto;
import explore.publicapi.services.PublicService;
import explore.validation.ValidDateTimeFormat;
import explore.validation.ValidSortFormat;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static explore.validation.DateTimeFormat.DATE_TIME_PATTERN;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class PublicController {
    private final PublicService service;

    @GetMapping("/compilations")
    public ResponseEntity<List<CompilationDto>> getCompilations(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                                                @RequestParam(name = "from", defaultValue = "0") int from,
                                                                @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getCompilations(pinned, from, size));
    }

    @GetMapping("/compilations/{compId}")
    public ResponseEntity<CompilationDto> getCompilation(@PathVariable int compId) {
        return ResponseEntity.ok(service.getCompilationById(compId));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getCategories(@RequestParam(name = "from", defaultValue = "0") int from,
                                                             @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getCategories(from, size));
    }

    @GetMapping("/categories/{catId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable int catId) {
        return ResponseEntity.ok(service.getCategoryById(catId));
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventShortDto>> getEvents(@RequestParam(name = "text", defaultValue = "") String text,
                                                         @RequestParam(name = "categories") Integer[] categories,
                                                         @RequestParam(name = "paid") Boolean paid,
                                                         @RequestParam(name = "rangeStart", required = false) @ValidDateTimeFormat(pattern = DATE_TIME_PATTERN) String rangeStart,
                                                         @RequestParam(name = "rangeEnd", required = false) @ValidDateTimeFormat(pattern = DATE_TIME_PATTERN) String rangeEnd,
                                                         @RequestParam(name = "onlyAvailable", defaultValue = "false") boolean onlyAvailable,
                                                         @RequestParam(name = "sort", required = false) @ValidSortFormat String sort,
                                                         @RequestParam(name = "from", defaultValue = "0") int from,
                                                         @RequestParam(name = "size", defaultValue = "10") int size,
                                                         HttpServletRequest request) {
        return ResponseEntity.ok(service.getEventsFiltered(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request.getRemoteAddr()));
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<EventFullDto> getEventById(@PathVariable int id, HttpServletRequest request) {
        return ResponseEntity.ok(service.getEventById(id, request.getRemoteAddr()));
    }
}
