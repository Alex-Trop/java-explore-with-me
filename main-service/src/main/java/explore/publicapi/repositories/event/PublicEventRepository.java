package explore.publicapi.repositories.event;

import explore.models.Event;
import explore.models.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PublicEventRepository extends JpaRepository<Event, Integer>, UtilPublicEventRepository {
    Optional<Event> findByIdAndState(Integer id, State state);
}
