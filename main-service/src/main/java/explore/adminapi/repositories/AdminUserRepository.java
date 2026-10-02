package explore.adminapi.repositories;

import explore.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AdminUserRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User AS u " +
            "WHERE u.id IN :ids")
    List<User> findFilteredUsers(@Param("ids") Integer[] ids);
}
