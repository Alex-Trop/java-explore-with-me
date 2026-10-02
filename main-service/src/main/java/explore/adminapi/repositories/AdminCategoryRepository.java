package explore.adminapi.repositories;

import explore.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminCategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findById(Integer id);
}
