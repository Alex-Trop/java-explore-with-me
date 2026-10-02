package explore.privateapi.mappers;

import explore.dtos.UserShortDto;
import explore.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PrivateUserMapper {
    UserShortDto toUserShortDto(User user);
}
