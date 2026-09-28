package explore.adminapi.mappers;

import explore.dtos.CompilationDto;
import explore.adminapi.dto.UpdateCompilationRequest;
import explore.models.Compilation;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = AdminEventMapper.class)
public interface AdminCompilationMapper {
    CompilationDto toCompilationDto(Compilation compilation);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "events", ignore = true)
    void updateCompilationByAdmin(UpdateCompilationRequest compilationRequest, @MappingTarget Compilation compilation);
}
