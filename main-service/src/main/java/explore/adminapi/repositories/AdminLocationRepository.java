package explore.adminapi.repositories;

import explore.models.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminLocationRepository extends JpaRepository<Location, Integer> {
}
