package explore.services;

import dto.hits.HitDto;
import dto.views.ViewStats;
import static dto.DateTimeFormat.DATE_TIME_PATTERN;
import static exceptions.ErrorDetails.HIT_DUPLICATE_ERROR;

import dto.views.ViewStatsProjection;
import exceptions.ResourceAlreadyExistsError;
import lombok.extern.slf4j.Slf4j;
import explore.mappers.HitMapper;
import explore.models.Hit;
import org.springframework.stereotype.Service;
import explore.repositories.HitsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HitService {
    private final HitsRepository repository;
    private final HitMapper mapper;

    public HitService(HitsRepository repository, HitMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public void addHit(HitDto hitDto) {
        log.info("Сохранение hit = ", hitDto);

        Hit newHit = mapper.toHit(hitDto);

        if (repository.existsByAppAndUriAndIpAndTimestamp(newHit.getApp(), newHit.getUri(), newHit.getIp(), newHit.getTimestamp())) {
            throw new ResourceAlreadyExistsError(HIT_DUPLICATE_ERROR);
        }

        Hit savedHit = repository.save(newHit);

        log.info("Сохранен hit = ", savedHit);
    }

    public List<ViewStats> getViewStatsByDateAndUris(String start,
                                                     String end,
                                                     String[] uris,
                                                     boolean unique) {
        log.info("Запрос на получение статистики с " + start + " по " + end + "для событий: " + uris);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);
        List<ViewStatsProjection> projections;

        if (unique && uris != null) {
            log.info("Получение уникальных запросов по uri");

            List<String> uriList = List.of(uris);
            projections = repository.findUniqueHitsByDateAndUri(startTime, endTime, uriList);
        } else if (!unique && uris != null) {
            log.info("Получение всех запросов по uri");

            List<String> uriList = List.of(uris);
            projections =  repository.findAllHitsByDateAndUri(startTime, endTime, uriList);
        } else if (unique && uris == null) {
            log.info("Получение уникальных запросов по всем uri");

            projections = repository.findUniqueHitsByDate(startTime, endTime);
        } else {
            log.info("Получение всего списка запросов");

            projections = repository.findAllHitsByDate(startTime, endTime);
        }

        List<ViewStats> views = projections.stream()
                .map(projection -> new ViewStats(
                        projection.getApp(),
                        projection.getUri(),
                        projection.getHits() == null ? 0 : projection.getHits().intValue()
                ))
                .collect(Collectors.toList());

        return views;
    }
}
