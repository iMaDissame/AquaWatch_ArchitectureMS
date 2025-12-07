package stmodelservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stmodelservice.domain.entity.QualityForecast;

import java.time.LocalDateTime;
import java.util.List;

public interface QualityForecastRepository extends JpaRepository<QualityForecast, Long> {

    List<QualityForecast> findByStationIdAndForecastTimeAfter(Long stationId, LocalDateTime from);
    
    List<QualityForecast> findByStationIdOrderByForecastTimeAsc(Long stationId);

    QualityForecast findTopByStationIdOrderByCreatedAtDesc(Long stationId);

    List<QualityForecast> findByStationIdAndCreatedAtAfterOrderByCreatedAtDesc(Long stationId, LocalDateTime since);
}