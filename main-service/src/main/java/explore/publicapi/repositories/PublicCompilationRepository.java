package explore.publicapi.repositories;

import explore.models.Compilation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface PublicCompilationRepository extends JpaRepository<Compilation, Integer> {
    @Query("SELECT c FROM Compilation AS c " +
            "LEFT JOIN FETCH c.events " +
            "WHERE c.pinned = :pinned")
    List<Compilation> findAllByPinned(@Param("pinned") boolean pinned);

    @Query("SELECT c FROM Compilation AS c " +
            "LEFT JOIN FETCH c.events")
    List<Compilation> findAll();
}
