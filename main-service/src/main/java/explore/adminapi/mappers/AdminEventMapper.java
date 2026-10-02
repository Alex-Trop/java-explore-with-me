package explore.adminapi.mappers;

import explore.dtos.EventFullDto;
import explore.dtos.EventShortDto;
import explore.adminapi.dto.UpdateEventAdminRequest;
import explore.models.Event;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AdminUserMapper.class, AdminCategoryMapper.class})
public interface AdminEventMapper {
    @Mapping(target = "views", constant = "0")
    @Mapping(target = "categoryDto", source = "category")
    EventShortDto toEventShortDto(Event event);

    @Mapping(target = "views", constant = "0")
    @Mapping(target = "categoryDto", source = "category")
    EventFullDto toEventFullDto(Event event);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "eventDate", ignore = true)
    void updateEventFromAdminRequest(UpdateEventAdminRequest adminRequest, @MappingTarget Event event);
}
