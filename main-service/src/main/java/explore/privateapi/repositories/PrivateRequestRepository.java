package explore.privateapi.repositories;

import explore.models.ParticipationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PrivateRequestRepository extends JpaRepository<ParticipationRequest, Integer> {
    @Query("SELECT r FROM ParticipationRequest AS r " +
            "JOIN FETCH r.event AS e " +
            "JOIN FETCH r.requester AS u " +
            "WHERE e.id =:eventId")
    List<ParticipationRequest> findAllByEventId(Integer eventId);

    @Query("SELECT r FROM ParticipationRequest AS r " +
            "JOIN FETCH r.event AS e " +
            "JOIN FETCH r.requester AS u " +
            "WHERE r.id =:requestId AND u.id =:userId")
    Optional<ParticipationRequest> findByIdAndRequesterId(Integer requestId, Integer userId);

    @Query("SELECT r FROM ParticipationRequest AS r " +
            "JOIN FETCH r.event AS e " +
            "JOIN FETCH r.requester AS u " +
            "WHERE r.id IN :ids " +
            "ORDER BY r.created")
    List<ParticipationRequest> findAllByIdIn(List<Integer> ids);

    @Query("SELECT r FROM ParticipationRequest AS r " +
            "JOIN FETCH r.event AS e " +
            "JOIN FETCH r.requester AS u " +
            "WHERE u.id =:userId")
    List<ParticipationRequest> findAllByRequester(Integer userId);

    boolean existsByEventIdAndRequesterId(Integer eventId, Integer userId);
}
