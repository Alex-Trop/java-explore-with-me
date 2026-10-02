package explore.publicapi.mappers;

import explore.dtos.CompilationDto;
import explore.models.Compilation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = PublicEventMapper.class)
public interface PublicCompilationMapper {
    CompilationDto toCompilationDto(Compilation compilation);
}
