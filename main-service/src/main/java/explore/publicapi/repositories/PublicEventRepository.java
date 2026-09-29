package explore.publicapi.repositories;

import explore.models.Event;
import explore.models.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface PublicEventRepository extends JpaRepository<Event, Integer> {
    @Query("SELECT e FROM Event AS e " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.location " +
            "JOIN FETCH e.initiator " +
            "WHERE (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND e.category.id IN :categories " +
            "AND e.paid = :paid " +
            "AND e.eventDate BETWEEN :start AND :end " +
            "AND e.participantLimit > e.confirmedRequests " +
            "AND e.state = :state")
    List<Event> findAvailableEventsFilteredDateInBetweenAndPaid(@Param("text") String text,
                                                                @Param("categories") List<Integer> categories,
                                                                @Param("paid") Boolean paid,
                                                                @Param("start") LocalDateTime start,
                                                                @Param("end") LocalDateTime end,
                                                                @Param("state") State state);

    @Query("SELECT e FROM Event AS e " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.location " +
            "JOIN FETCH e.initiator " +
            "WHERE (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND e.category.id IN :categories " +
            "AND e.eventDate BETWEEN :start AND :end " +
            "AND e.participantLimit > e.confirmedRequests " +
            "AND e.state = :state")
    List<Event> findAvailableEventsFilteredDateInBetweenWithoutPaid(@Param("text") String text,
                                                                @Param("categories") List<Integer> categories,
                                                                @Param("start") LocalDateTime start,
                                                                @Param("end") LocalDateTime end,
                                                                @Param("state") State state);

    @Query("SELECT e FROM Event AS e " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.location " +
            "JOIN FETCH e.initiator " +
            "WHERE (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND e.category.id IN :categories " +
            "AND e.paid = :paid " +
            "AND e.eventDate BETWEEN :start AND :end " +
            "AND e.state = :state")
    List<Event> findEventsFilteredDateInBetweenAndPaid(@Param("text") String text,
                                                       @Param("categories") List<Integer> categories,
                                                       @Param("paid") Boolean paid,
                                                       @Param("start") LocalDateTime start,
                                                       @Param("end") LocalDateTime end,
                                                       @Param("state") State state);

    @Query("SELECT e FROM Event AS e " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.location " +
            "JOIN FETCH e.initiator " +
            "WHERE (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND e.category.id IN :categories " +
            "AND e.eventDate BETWEEN :start AND :end " +
            "AND e.state = :state")
    List<Event> findEventsFilteredDateInBetweenWithoutPaid(@Param("text") String text,
                                                       @Param("categories") List<Integer> categories,
                                                       @Param("start") LocalDateTime start,
                                                       @Param("end") LocalDateTime end,
                                                       @Param("state") State state);

    @Query("SELECT e FROM Event AS e " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.location " +
            "JOIN FETCH e.initiator " +
            "WHERE (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND e.category.id IN :categories " +
            "AND e.paid = :paid " +
            "AND e.eventDate >= :now " +
            "AND e.participantLimit > e.confirmedRequests " +
            "AND e.state = :state")
    List<Event> findAvailableUpcompingEventsFilteredAndPaid(@Param("text") String text,
                                                            @Param("categories") List<Integer> categories,
                                                            @Param("paid") Boolean paid,
                                                            @Param("now") LocalDateTime now,
                                                            @Param("state") State state);

    @Query("SELECT e FROM Event AS e " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.location " +
            "JOIN FETCH e.initiator " +
            "WHERE (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND e.category.id IN :categories " +
            "AND e.eventDate >= :now " +
            "AND e.participantLimit > e.confirmedRequests " +
            "AND e.state = :state")
    List<Event> findAvailableUpcompingEventsFilteredWithoutPaid(@Param("text") String text,
                                                            @Param("categories") List<Integer> categories,
                                                            @Param("now") LocalDateTime now,
                                                            @Param("state") State state);

    @Query("SELECT e FROM Event AS e " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.location " +
            "JOIN FETCH e.initiator " +
            "WHERE (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND e.category.id IN :categories " +
            "AND e.paid = :paid " +
            "AND e.eventDate >= :now " +
            "AND e.state = :state")
    List<Event> findUpcompingEventsFilteredAndPaid(@Param("text") String text,
                                                   @Param("categories") List<Integer> categories,
                                                   @Param("paid") Boolean paid,
                                                   @Param("now") LocalDateTime now,
                                                   @Param("state") State state);

    @Query("SELECT e FROM Event AS e " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.location " +
            "JOIN FETCH e.initiator " +
            "WHERE (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND e.category.id IN :categories " +
            "AND e.eventDate >= :now " +
            "AND e.state = :state")
    List<Event> findUpcompingEventsFilteredWithoutPaid(@Param("text") String text,
                                                   @Param("categories") List<Integer> categories,
                                                   @Param("now") LocalDateTime now,
                                                   @Param("state") State state);

    Optional<Event> findByIdAndState(Integer id, State state);
}
