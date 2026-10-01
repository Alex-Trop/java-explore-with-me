package explore.adminapi.repositories.event;

import explore.models.Category;
import explore.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminEventRepository extends JpaRepository<Event, Integer>, UtilAdminEventRepository {
    boolean existsByCategory(Category category);
}
