package explore.privateapi.mappers;

import explore.dtos.CategoryDto;
import explore.models.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PrivateCategoryMapper {
    CategoryDto toCategoryDto(Category category);
}
