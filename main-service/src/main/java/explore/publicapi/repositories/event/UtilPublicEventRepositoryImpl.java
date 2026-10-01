package explore.publicapi.repositories.event;

import explore.models.Event;
import explore.models.State;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

public class UtilPublicEventRepositoryImpl implements UtilPublicEventRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Event> findAvailableEventsFilteredDateInBetween(String text,
                                                                List<Integer> categories,
                                                                Boolean paid,
                                                                LocalDateTime start,
                                                                LocalDateTime end,
                                                                State state,
                                                                int from,
                                                                int size) {
        String jpql = "SELECT e FROM Event AS " +
                "JOIN FETCH e.category " +
                "JOIN FETCH e.location " +
                "JOIN FETCH e.initiator " +
                "WHERE (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
                "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
                "AND (:categories IS NULL OR e.category.id IN :categories) " +
                "AND (:paid IS NULL OR e.paid = :paid) " +
                "AND e.eventDate BETWEEN :start AND :end " +
                "AND e.participantLimit > e.confirmedRequests " +
                "AND e.state = :state " +
                "ORDER BY e.createdOn";
        TypedQuery<Event> query = em.createQuery(jpql, Event.class)
                .setParameter("text", text)
                .setParameter("categories", categories)
                .setParameter("paid", paid)
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("state", state)
                .setFirstResult(from)
                .setMaxResults(size);

        return query.getResultList();
    }

    @Override
    public List<Event> findEventsFilteredDateInBetween(String text,
                                                       List<Integer> categories,
                                                       Boolean paid,
                                                       LocalDateTime start,
                                                       LocalDateTime end,
                                                       State state,
                                                       int from,
                                                       int size) {
        String jpql = "SELECT e FROM Event AS e " +
                "JOIN FETCH e.category " +
                "JOIN FETCH e.location " +
                "JOIN FETCH e.initiator " +
                "WHERE (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
                "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
                "AND (:categories IS NULL OR e.category.id IN :categories) " +
                "AND (:paid IS NULL OR e.paid = :paid) " +
                "AND e.eventDate BETWEEN :start AND :end " +
                "AND e.state = :state" +
                "ORDER BY e.createdOn";
        TypedQuery<Event> query = em.createQuery(jpql, Event.class)
                .setParameter("text", text)
                .setParameter("categories", categories)
                .setParameter("paid", paid)
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("state", state)
                .setFirstResult(from)
                .setMaxResults(size);

        return query.getResultList();
    }

    @Override
    public List<Event> findAvailableUpcompingEventsFiltered(String text,
                                                            List<Integer> categories,
                                                            Boolean paid,
                                                            LocalDateTime now,
                                                            State state,
                                                            int from,
                                                            int size) {
        String jpql = "SELECT e FROM Event AS e " +
                "JOIN FETCH e.category " +
                "JOIN FETCH e.location " +
                "JOIN FETCH e.initiator " +
                "WHERE (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
                "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
                "AND (:categories IS NULL OR e.category.id IN :categories) " +
                "AND (:paid IS NULL OR e.paid = :paid) " +
                "AND e.eventDate >= :now " +
                "AND e.participantLimit > e.confirmedRequests " +
                "AND e.state = :state" +
                "ORDER BY e.createdOn";
        TypedQuery<Event> query = em.createQuery(jpql, Event.class)
                .setParameter("text", text)
                .setParameter("categories", categories)
                .setParameter("paid", paid)
                .setParameter("now", now)
                .setParameter("state", state)
                .setFirstResult(from)
                .setMaxResults(size);

        return query.getResultList();
    }

    @Override
    public List<Event> findUpcompingEventsFiltered(String text,
                                                   List<Integer> categories,
                                                   Boolean paid,
                                                   LocalDateTime now,
                                                   State state,
                                                   int from,
                                                   int size) {
        String jpql = "SELECT e FROM Event AS e " +
                "JOIN FETCH e.category " +
                "JOIN FETCH e.location " +
                "JOIN FETCH e.initiator " +
                "WHERE (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
                "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
                "AND (:categories IS NULL OR e.category.id IN :categories) " +
                "AND (:paid IS NULL OR e.paid = :paid) " +
                "AND e.eventDate >= :now " +
                "AND e.state = :state" +
                "ORDER BY e.createdOn";
        TypedQuery<Event> query = em.createQuery(jpql, Event.class)
                .setParameter("text", text)
                .setParameter("categories", categories)
                .setParameter("paid", paid)
                .setParameter("now", now)
                .setParameter("state", state)
                .setFirstResult(from)
                .setMaxResults(size);

        return query.getResultList();
    }
}
