package explore.publicapi.mappers;

import explore.dtos.CategoryDto;
import explore.models.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PublicCategoryMapper {
    CategoryDto toCategoryDto(Category category);
}
