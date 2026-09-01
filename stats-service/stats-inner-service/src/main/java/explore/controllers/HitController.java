package explore.controllers;

import dto.hits.HitDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import explore.services.HitService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hit")
@RequiredArgsConstructor
public class HitController {
    private final HitService service;

    @PostMapping
    public ResponseEntity<Object> postHit(@RequestBody @Valid HitDto hitDto) {
        service.addHit(hitDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
