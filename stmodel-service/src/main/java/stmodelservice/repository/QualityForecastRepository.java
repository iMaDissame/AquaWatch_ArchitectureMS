package stmodelservice.repository;
import stmodelservice.domain.entity.QualityForecast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface QualityForecastRepository extends JpaRepository<QualityForecast, Long> {

    List<QualityForecast> findByStationIdAndForecastTimeAfter(Long stationId, LocalDateTime from);
}