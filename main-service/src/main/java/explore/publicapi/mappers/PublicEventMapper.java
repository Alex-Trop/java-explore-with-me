package explore.publicapi.mappers;

import explore.dtos.EventCommentDto;
import explore.dtos.EventFullDto;
import explore.dtos.EventShortDto;
import explore.models.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PublicUserMapper.class, PublicCategoryMapper.class})
public interface PublicEventMapper {
    @Mapping(target = "views", constant = "0")
    @Mapping(target = "categoryDto", source = "category")
    EventShortDto toEventShortDto(Event event);

    @Mapping(target = "views", constant = "0")
    @Mapping(target = "categoryDto", source = "category")
    EventFullDto toEventFullDto(Event event);

    EventCommentDto toEventCommentDto(Event event);
}
