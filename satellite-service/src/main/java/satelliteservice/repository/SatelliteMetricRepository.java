package satelliteservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import satelliteservice.domain.entity.SatelliteMetric;
import satelliteservice.domain.entity.SatelliteScene;
import satelliteservice.domain.enums.MetricType;

import java.time.LocalDateTime;
import java.util.List;

public interface SatelliteMetricRepository extends JpaRepository<SatelliteMetric, Long> {
    List<SatelliteMetric> findByStationIdAndMetricTypeAndCreatedAtBetween(
            Long stationId,
            MetricType metricType,
            LocalDateTime from,
            LocalDateTime to
    );

    List<SatelliteMetric> findByScene(SatelliteScene scene);

    List<SatelliteMetric> findTop10ByStationIdOrderByCreatedAtDesc(Long stationId);
}
