package explore.privateapi.repositories;

import explore.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PrivateCommentRepository extends JpaRepository<Comment, Integer> {
    boolean existsByIdAndAuthorId(int id, int authorId);

    @Query("SELECT c FROM Comment AS c " +
            "JOIN FETCH c.author " +
            "JOIN FETCH c.event " +
            "WHERE c.author.id = :authorId " +
            "ORDER BY c.createdOn DESC")
    List<Comment> findCommentsByAuthorId(@Param("authorId") int authorId);
}
