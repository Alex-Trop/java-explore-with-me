package explore.repositories;

import dto.App;
import dto.views.ViewStatsProjection;
import explore.models.Hit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface HitsRepository extends JpaRepository<Hit, Integer> {
    @Query("SELECT h.app AS app, h.uri AS uri, COUNT(h.ip) AS hits FROM Hit AS h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "AND h.uri IN :uris " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<ViewStatsProjection> findAllHitsByDateAndUri(@Param("start")LocalDateTime start,
                                                      @Param("end") LocalDateTime end,
                                                      @Param("uris") List<String> uris);

    @Query("SELECT h.app AS app, h.uri AS uri, COUNT(h.ip) AS hits FROM Hit AS h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(h.ip) DESC")
    List<ViewStatsProjection> findAllHitsByDate(@Param("start")LocalDateTime start,
                                                      @Param("end") LocalDateTime end);

    @Query("SELECT h.app AS app, h.uri AS uri, COUNT(DISTINCT h.ip) AS hits FROM Hit AS h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "AND h.uri IN :uris " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStatsProjection> findUniqueHitsByDateAndUri(@Param("start")LocalDateTime start,
                                                         @Param("end") LocalDateTime end,
                                                         @Param("uris") List<String> uris);

    @Query("SELECT h.app AS app, h.uri AS uri, COUNT(DISTINCT h.ip) AS hits FROM Hit AS h " +
            "WHERE h.timestamp BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<ViewStatsProjection> findUniqueHitsByDate(@Param("start")LocalDateTime start,
                                                         @Param("end") LocalDateTime end);

    boolean existsByAppAndUriAndIpAndTimestamp(App app, String uri, String ip, LocalDateTime timestamp);
}
