package alertservice.repository;

import alertservice.domain.entity.Alert;
import alertservice.domain.enums.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByStatus(AlertStatus status);

    List<Alert> findByStationId(Long stationId);

    List<Alert> findByStationIdAndStatus(Long stationId, AlertStatus status);
}
