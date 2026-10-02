package explore.privateapi.repositories;

import explore.models.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrivateLocationRepository extends JpaRepository<Location, Integer> {
    Optional<Location> findByLatAndLon(float lat, float lon);
}
