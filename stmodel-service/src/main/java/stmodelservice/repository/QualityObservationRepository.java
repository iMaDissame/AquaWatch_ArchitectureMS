package stmodelservice.repository;

import stmodelservice.domain.entity.QualityObservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QualityObservationRepository extends JpaRepository<QualityObservation, Long> {

    Optional<QualityObservation> findFirstByStationIdOrderByTimestampDesc(Long stationId);
}