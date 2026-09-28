package explore.adminapi.mappers;

import explore.adminapi.dto.UserDto;
import explore.dtos.UserShortDto;
import explore.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminUserMapper {
    UserDto toUserDto(User user);

    UserShortDto toUserShortDto(User user);
}
