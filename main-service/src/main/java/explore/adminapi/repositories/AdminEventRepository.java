package explore.adminapi.repositories;

import explore.models.Category;
import explore.models.Event;
import explore.models.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventRepository extends JpaRepository<Event, Integer> {
    boolean existsByCategory(Category category);

    @Query("SELECT e FROM Event AS e " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.location " +
            "JOIN FETCH e.initiator " +
            "WHERE e.initiator.id IN :users " +
            "AND e.state IN :states " +
            "AND e.category.id IN :categories " +
            "AND e.eventDate BETWEEN :start AND :end")
    List<Event> findFilteredEvents(@Param("users") Integer[] users,
                                   @Param("states") State[] states,
                                   @Param("categories") Integer[] categories,
                                   @Param("start")LocalDateTime start,
                                   @Param("end")LocalDateTime end);

    @Query("SELECT e FROM Event AS e " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.location " +
            "JOIN FETCH e.initiator " +
            "WHERE e.eventDate BETWEEN :start AND :end")
    List<Event> findAllEventsInPeriod(@Param("start")LocalDateTime start,
                                   @Param("end")LocalDateTime end);

    @Query("SELECT e FROM Event AS e " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.location " +
            "JOIN FETCH e.initiator " +
            "WHERE e.initiator.id IN :users " +
            "AND e.state IN :states " +
            "AND e.category.id IN :categories " +
            "AND e.eventDate >= :start")
    List<Event> findFilteredUpcomingEvents(@Param("users") Integer[] users,
                                           @Param("states") State[] states,
                                           @Param("categories") Integer[] categories,
                                           @Param("start")LocalDateTime start);

    @Query("SELECT e FROM Event AS e " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.location " +
            "JOIN FETCH e.initiator " +
            "WHERE e.eventDate >= :start")
    List<Event> findAllUpcomingEvents(@Param("start")LocalDateTime start);
}
