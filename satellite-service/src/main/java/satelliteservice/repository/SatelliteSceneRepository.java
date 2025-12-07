package satelliteservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import satelliteservice.domain.entity.SatelliteScene;

import java.util.Optional;

public interface SatelliteSceneRepository extends JpaRepository<SatelliteScene,Long> {
    Optional<SatelliteScene> findByProductId(String projectId);
}
