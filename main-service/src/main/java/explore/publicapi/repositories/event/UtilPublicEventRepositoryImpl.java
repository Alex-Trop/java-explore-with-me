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
        StringBuilder jpql = new StringBuilder("SELECT e FROM Event AS " +
                "JOIN FETCH e.category " +
                "JOIN FETCH e.location " +
                "JOIN FETCH e.initiator " +
                "WHERE (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
                "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
                "AND e.eventDate BETWEEN :start AND :end " +
                "AND e.participantLimit > e.confirmedRequests " +
                "AND e.state = :state ");

        if (categories != null) {
            jpql.append("AND e.category.id IN :categories ");
        }
        if (paid != null) {
            jpql.append("AND e.paid = :paid ");
        }
        jpql.append("ORDER BY e.createdOn");

        TypedQuery<Event> query = em.createQuery(jpql.toString(), Event.class)
                .setParameter("text", text)
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("state", state)
                .setFirstResult(from)
                .setMaxResults(size);

        if (categories != null) {
            query.setParameter("categories", categories);
        }
        if (paid != null) {
            query.setParameter("paid", paid);
        }
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
        StringBuilder jpql = new StringBuilder("SELECT e FROM Event AS e " +
                "JOIN FETCH e.category " +
                "JOIN FETCH e.location " +
                "JOIN FETCH e.initiator " +
                "WHERE (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
                "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
                "AND e.eventDate BETWEEN :start AND :end " +
                "AND e.state = :state ");

        if (categories != null) {
            jpql.append("AND e.category.id IN :categories ");
        }
        if (paid != null) {
            jpql.append("AND e.paid = :paid ");
        }
        jpql.append("ORDER BY e.createdOn");

        TypedQuery<Event> query = em.createQuery(jpql.toString(), Event.class)
                .setParameter("text", text)
                .setParameter("start", start)
                .setParameter("end", end)
                .setParameter("state", state)
                .setFirstResult(from)
                .setMaxResults(size);

        if (categories != null) {
            query.setParameter("categories", categories);
        }
        if (paid != null) {
            query.setParameter("paid", paid);
        }
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
        StringBuilder jpql = new StringBuilder("SELECT e FROM Event AS e " +
                "JOIN FETCH e.category " +
                "JOIN FETCH e.location " +
                "JOIN FETCH e.initiator " +
                "WHERE (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
                "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
                "AND e.eventDate >= :now " +
                "AND e.participantLimit > e.confirmedRequests " +
                "AND e.state = :state ");

        if (categories != null) {
            jpql.append("AND e.category.id IN :categories ");
        }
        if (paid != null) {
            jpql.append("AND e.paid = :paid ");
        }
        jpql.append("ORDER BY e.createdOn");

        TypedQuery<Event> query = em.createQuery(jpql.toString(), Event.class)
                .setParameter("text", text)
                .setParameter("now", now)
                .setParameter("state", state)
                .setFirstResult(from)
                .setMaxResults(size);

        if (categories != null) {
            query.setParameter("categories", categories);
        }
        if (paid != null) {
            query.setParameter("paid", paid);
        }

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
        StringBuilder jpql = new StringBuilder("SELECT e FROM Event AS e " +
                "JOIN FETCH e.category " +
                "JOIN FETCH e.location " +
                "JOIN FETCH e.initiator " +
                "WHERE (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
                "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
                "AND e.eventDate >= :now " +
                "AND e.state = :state ");

        if (categories != null) {
            jpql.append("AND e.category.id IN :categories ");
        }
        if (paid != null) {
            jpql.append("AND e.paid = :paid ");
        }
        jpql.append("ORDER BY e.createdOn");

        TypedQuery<Event> query = em.createQuery(jpql.toString(), Event.class)
                .setParameter("text", text)
                .setParameter("now", now)
                .setParameter("state", state)
                .setFirstResult(from)
                .setMaxResults(size);

        if (categories != null) {
            query.setParameter("categories", categories);
        }
        if (paid != null) {
            query.setParameter("paid", paid);
        }
        return query.getResultList();
    }
}
