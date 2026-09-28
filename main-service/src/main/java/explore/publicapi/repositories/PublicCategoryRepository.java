package explore.publicapi.repositories;

import explore.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicCategoryRepository extends JpaRepository<Category, Integer> {
}
