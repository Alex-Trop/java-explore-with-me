package explore.adminapi.repositories;

import explore.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminCommentRepository extends JpaRepository<Comment, Integer> {
}
