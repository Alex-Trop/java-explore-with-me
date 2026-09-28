package explore.privateapi.mappers;

import explore.dtos.EventFullDto;
import explore.dtos.EventShortDto;
import explore.models.Event;
import explore.privateapi.dto.UpdateEventUserRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {PrivateUserMapper.class, PrivateCategoryMapper.class})
public interface PrivateEventMapper {
    @Mapping(target = "views", constant = "0")
    @Mapping(target = "categoryDto", source = "category")
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "views", constant = "0")
    @Mapping(target = "categoryDto", source = "category")
    EventShortDto toEventShortDto(Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEventFromUserRequest(UpdateEventUserRequest userRequest, @MappingTarget Event event);
}
