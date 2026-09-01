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

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class ViewStatsController {
    private final HitService service;

    @GetMapping
    public ResponseEntity<Object> getStats(@RequestParam(name = "start") @ValidDateTimeFormat String start,
                                           @RequestParam(name = "end") @ValidDateTimeFormat String end,
                                           @RequestParam(name = "uris", required = false) String[] uris,
                                           @RequestParam(name = "unique", defaultValue = "false") boolean unique) {
        String decodedStart = URLDecoder.decode(start, StandardCharsets.UTF_8);
        String decodedEnd = URLDecoder.decode(end, StandardCharsets.UTF_8);
        List<ViewStats> viewStats = service.getViewStatsByDateAndUris(decodedStart,decodedEnd, uris, unique);

        return ResponseEntity.ok(viewStats);
    }
}
