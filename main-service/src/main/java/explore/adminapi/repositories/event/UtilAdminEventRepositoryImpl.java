package explore.adminapi.repositories.event;

import explore.models.Event;
import explore.models.State;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

public class UtilAdminEventRepositoryImpl implements UtilAdminEventRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Event> findFilteredEvents(List<Integer> users,
                                          List<State> states,
                                          List<Integer> categories,
                                          LocalDateTime start,
                                          LocalDateTime end,
                                          int from,
                                          int size) {
        String jpql = "SELECT e FROM Event AS e " +
                "JOIN FETCH e.category " +
                "JOIN FETCH e.location " +
                "JOIN FETCH e.initiator " +
                "WHERE e.initiator.id IN :users " +
                "AND e.state IN :states " +
                "AND e.category.id IN :categories " +
                "AND e.eventDate BETWEEN :start AND :end " +
                "ORDER BY e.eventDate";
        TypedQuery<Event> query = em.createQuery(jpql, Event.class)
                .setParameter("users", users)
                .setParameter("states", states)
                .setParameter("categories", categories)
                .setParameter("start", start)
                .setParameter("end", end)
                .setFirstResult(from)
                .setMaxResults(size);

        return query.getResultList();
    }

    @Override
    public List<Event> findAllEventsInPeriod(LocalDateTime start,
                                             LocalDateTime end,
                                             int from,
                                             int size) {
        String jpql = "SELECT e FROM Event AS e " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.location " +
            "JOIN FETCH e.initiator " +
            "WHERE e.eventDate BETWEEN :start AND :end " +
                "ORDER BY e.eventDate";
        TypedQuery<Event> query = em.createQuery(jpql, Event.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .setFirstResult(from)
                .setMaxResults(size);

        return query.getResultList();

    }

    @Override
    public List<Event> findFilteredUpcomingEvents(List<Integer> users,
                                                  List<State> states,
                                                  List<Integer> categories,
                                                  LocalDateTime start,
                                                  int from,
                                                  int size) {
        String jpql = "SELECT e FROM Event AS e " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.location " +
            "JOIN FETCH e.initiator " +
            "WHERE e.initiator.id IN :users " +
            "AND e.state IN :states " +
            "AND e.category.id IN :categories " +
            "AND e.eventDate >= :start " +
                "ORDER BY e.eventDate";
        TypedQuery<Event> query = em.createQuery(jpql, Event.class)
                .setParameter("users", users)
                .setParameter("states", states)
                .setParameter("categories", categories)
                .setParameter("start", start)
                .setFirstResult(from)
                .setMaxResults(size);

        return query.getResultList();
    }

    @Override
    public List<Event> findAllUpcomingEvents(LocalDateTime start,
                                             int from,
                                             int size) {
        String jpql = "SELECT e FROM Event AS e " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.location " +
            "JOIN FETCH e.initiator " +
            "WHERE e.eventDate >= :start ORDER BY e.eventDate";
        TypedQuery<Event> query = em.createQuery(jpql, Event.class)
                .setParameter("start", start)
                .setFirstResult(from)
                .setMaxResults(size);

        return query.getResultList();
    }
}
