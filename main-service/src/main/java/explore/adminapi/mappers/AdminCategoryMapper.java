package explore.adminapi.mappers;

import explore.dtos.CategoryDto;
import explore.models.Category;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AdminCategoryMapper {
    CategoryDto toCategoryDto(Category category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateCategoryFromCategoryDto(CategoryDto newDto, @MappingTarget Category category);
}
