package stmodelservice.repository;

import stmodelservice.domain.entity.PredictionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository pour l'accès aux données de l'historique des prédictions.
 */
@Repository
public interface PredictionHistoryRepository extends JpaRepository<PredictionHistory, Long> {

    /**
     * Récupère l'historique des prédictions pour une station, trié par date décroissante.
     */
    List<PredictionHistory> findByStationIdOrderByPredictionTimestampDesc(Long stationId);

    /**
     * Récupère les dernières N prédictions pour une station.
     */
    List<PredictionHistory> findTop20ByStationIdOrderByPredictionTimestampDesc(Long stationId);

    /**
     * Récupère les prédictions dans une plage de dates pour une station.
     */
    List<PredictionHistory> findByStationIdAndPredictionTimestampBetweenOrderByPredictionTimestampDesc(
            Long stationId, LocalDateTime start, LocalDateTime end);

    /**
     * Compte le nombre de prédictions pour une station.
     */
    long countByStationId(Long stationId);

    /**
     * Récupère toutes les prédictions triées par date décroissante.
     */
    List<PredictionHistory> findAllByOrderByPredictionTimestampDesc();
}
