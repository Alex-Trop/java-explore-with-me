package explore.adminapi.mappers;

import explore.dtos.LocationDto;
import explore.models.Location;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminLocationMapper {
    LocationDto toLocationDto(Location location);
}
