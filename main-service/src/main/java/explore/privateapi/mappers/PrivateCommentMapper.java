package explore.privateapi.mappers;

import explore.dtos.CommentDto;
import explore.models.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PrivateUserMapper.class, PrivateEventMapper.class})
public interface PrivateCommentMapper {
    CommentDto toCommentDto(Comment comment);
}
