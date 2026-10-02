package explore.privateapi.repositories;

import explore.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PrivateEventRepository extends JpaRepository<Event, Integer> {
    @Query("SELECT e FROM Event AS e " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.location " +
            "JOIN FETCH e.initiator " +
            "WHERE e.initiator.id = :userId")
    List<Event> findAllByInitiator(@Param("userId") Integer userId);

    @Override
    Optional<Event> findById(Integer integer);
}
