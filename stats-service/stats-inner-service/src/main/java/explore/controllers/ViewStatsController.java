package explore.controllers;

import dto.views.ViewStats;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import explore.services.HitService;
import org.springframework.web.bind.annotation.RestController;
import validation.ValidDateTimeFormat;

import java.util.List;

import static dto.DateTimeFormat.DATE_TIME_PATTERN;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class ViewStatsController {
    private final HitService service;

    @GetMapping
    public ResponseEntity<Object> getStats(@RequestParam(name = "start") @ValidDateTimeFormat(pattern = DATE_TIME_PATTERN) String start,
                                           @RequestParam(name = "end") @ValidDateTimeFormat(pattern = DATE_TIME_PATTERN) String end,
                                           @RequestParam(name = "uris", required = false) String[] uris,
                                           @RequestParam(name = "unique", defaultValue = "false") boolean unique) {
        List<ViewStats> viewStats = service.getViewStatsByDateAndUris(start, end, uris, unique);

        return ResponseEntity.ok(viewStats);
    }
}
