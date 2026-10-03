package explore.publicapi.repositories;

import explore.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PublicCommentRepository extends JpaRepository<Comment, Integer> {
    @Query("SELECT c FROM Comment AS c " +
            "JOIN FETCH c.author " +
            "JOIN FETCH c.event " +
            "WHERE c.event.id = :eventId " +
            "AND LOWER(c.text) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "ORDER BY c.createdOn DESC")
    List<Comment> findAllByEventIdAndText(@Param("eventId") int eventId,
                                          @Param("text") String text);
}
