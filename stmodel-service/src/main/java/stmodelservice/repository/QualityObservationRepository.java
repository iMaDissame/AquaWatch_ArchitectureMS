package stmodelservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stmodelservice.domain.entity.QualityObservation;

import java.util.List;
import java.util.Optional;

public interface QualityObservationRepository extends JpaRepository<QualityObservation, Long> {

    Optional<QualityObservation> findFirstByStationIdOrderByTimestampDesc(Long stationId);
    
    List<QualityObservation> findByStationIdOrderByTimestampDesc(Long stationId);
}