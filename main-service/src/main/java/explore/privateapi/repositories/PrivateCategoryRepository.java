package explore.privateapi.repositories;

import explore.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivateCategoryRepository extends JpaRepository<Category, Integer> {
}
