package explore.publicapi.mappers;

import explore.dtos.UserShortDto;
import explore.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PublicUserMapper {
    UserShortDto toUserShortDto(User user);
}
