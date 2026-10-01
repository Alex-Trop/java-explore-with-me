package explore.publicapi.repositories.event;

import explore.models.Event;
import explore.models.State;


import java.time.LocalDateTime;
import java.util.List;

public interface UtilPublicEventRepository {
    List<Event> findAvailableEventsFilteredDateInBetween(String text,
                                                                List<Integer> categories,
                                                                Boolean paid,
                                                                LocalDateTime start,
                                                                LocalDateTime end,
                                                                State state,
                                                                int from,
                                                                int size);

    List<Event> findEventsFilteredDateInBetween(String text,
                                                List<Integer> categories,
                                                Boolean paid,
                                                LocalDateTime start,
                                                LocalDateTime end,
                                                State state,
                                                int from,
                                                int size);

    List<Event> findAvailableUpcompingEventsFiltered(String text,
                                                     List<Integer> categories,
                                                     Boolean paid,
                                                     LocalDateTime now,
                                                     State state,
                                                     int from,
                                                     int size);

    List<Event> findUpcompingEventsFiltered(String text,
                                            List<Integer> categories,
                                            Boolean paid,
                                            LocalDateTime now,
                                            State state,
                                            int from,
                                            int size);
}
