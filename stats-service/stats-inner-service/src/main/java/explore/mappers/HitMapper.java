package explore.mappers;

import dto.App;
import dto.hits.HitDto;
import explore.models.Hit;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static dto.DateTimeFormat.DATE_TIME_PATTERN;

@Mapper(componentModel = "spring")
public interface HitMapper {
    default Hit toHit(HitDto hitDto) {
        return new Hit(
                App.of(hitDto.getApp()),
                hitDto.getUri(),
                hitDto.getIp(),
                LocalDateTime.parse(hitDto.getTimestamp(), DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))
        );
    }

    default HitDto toHitDto(Hit hit) {
        return new HitDto(
                hit.getApp().toString(),
                hit.getUri(),
                hit.getIp(),
                hit.getTimestamp().format(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))
        );
    }
}
