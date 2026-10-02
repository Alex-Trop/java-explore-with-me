package explore.privateapi.repositories;

import explore.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivateUserRepository extends JpaRepository<User, Integer> {
}
