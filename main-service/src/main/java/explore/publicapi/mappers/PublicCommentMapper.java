package explore.publicapi.mappers;

import explore.dtos.CommentDto;
import explore.models.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PublicUserMapper.class, PublicEventMapper.class})
public interface PublicCommentMapper {
    CommentDto toCommentDto(Comment comment);
}
