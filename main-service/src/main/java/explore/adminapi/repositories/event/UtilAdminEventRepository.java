package explore.adminapi.repositories.event;

import explore.models.Event;
import explore.models.State;

import java.time.LocalDateTime;
import java.util.List;

public interface UtilAdminEventRepository {
    List<Event> findFilteredEvents(List<Integer> users,
                                   List<State> states,
                                   List<Integer> categories,
                                   LocalDateTime start,
                                   LocalDateTime end,
                                   int from,
                                   int size);

    List<Event> findAllEventsInPeriod(LocalDateTime start,
                                      LocalDateTime end,
                                      int from,
                                      int size);

    List<Event> findFilteredUpcomingEvents(List<Integer> users,
                                           List<State> states,
                                           List<Integer> categories,
                                           LocalDateTime start,
                                           int from,
                                           int size);

    List<Event> findAllUpcomingEvents(LocalDateTime start,
                                      int from,
                                      int size);
}
