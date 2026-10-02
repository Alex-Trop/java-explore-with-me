package explore.adminapi.repositories;

import explore.models.Compilation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminCompilationRepository extends JpaRepository<Compilation, Integer> {
}
