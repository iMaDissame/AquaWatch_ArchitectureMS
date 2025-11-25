package sensorservice.repository;

import sensorservice.domain.entity.SensorMeasurement;
import sensorservice.domain.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SensorMeasurementRepository extends JpaRepository<SensorMeasurement, Long> {

    List<SensorMeasurement> findByStationAndTimestampBetween(
            Station station,
            LocalDateTime from,
            LocalDateTime to
    );

    Optional<SensorMeasurement> findFirstByStationOrderByTimestampDesc(Station station);
}