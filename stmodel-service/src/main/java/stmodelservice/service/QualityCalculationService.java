package stmodelservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stmodelservice.client.SatelliteClient;
import stmodelservice.client.SensorClient;
import stmodelservice.domain.entity.QualityObservation;
import stmodelservice.domain.enums.QualityStatus;
import stmodelservice.repository.QualityObservationRepository;
import stmodelservice.web.dto.SatelliteMetricDTO;
import stmodelservice.web.dto.SensorMeasurementDTO;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QualityCalculationService {

    private final SensorClient sensorClient;
    private final SatelliteClient satelliteClient;
    private final QualityObservationRepository observationRepository;

    @Transactional
    public QualityObservation computeCurrentQuality(Long stationId) {
        SensorMeasurementDTO sensor = sensorClient.getLatestMeasurement(stationId);
        if (sensor == null) {
            throw new IllegalStateException("No sensor data for station " + stationId);
        }

        List<SatelliteMetricDTO> metrics = satelliteClient.getLatestMetricsForStation(stationId);

        double score = 100.0;
        StringBuilder details = new StringBuilder();

        // ------- règles sur capteurs (simple exemple) -------
        // pH idéal 6.5 - 8.5
        if (sensor.getPh() < 6.5 || sensor.getPh() > 8.5) {
            score -= 20;
            details.append("pH hors plage (6.5-8.5). ");
        }

        // Turbidité
        if (sensor.getTurbidity() > 5.0) {
            score -= 20;
            details.append("Turbidité élevée (>5 NTU). ");
        }

        // DO
        if (sensor.getDissolvedOxygen() < 7.0) {
            score -= 15;
            details.append("Oxygène dissous faible (<7 mg/L). ");
        }

        // Conductivité (simple règle indicative, à adapter)
        if (sensor.getConductivity() > 400) {
            score -= 10;
            details.append("Conductivité élevée. ");
        }

        // ------- règles sur satellite (si dispo) -------
        if (metrics != null) {
            metrics.forEach(m -> {
                if ("WATER_COVERAGE_PERCENT".equalsIgnoreCase(m.getMetricType().name())) {
                    // ex: si très faible couverture d'eau dans la zone d'intérêt
                    if (m.getValue() < 10.0) {
                        // ici on ne pénalise pas trop, c'est contextuel
                        // score -= 5;
                        details.append("Faible couverture d'eau détectée par satellite. ");
                    }
                }
                // tu peux ajouter d'autres règles NDWI, TURBIDITY_INDEX...
            });
        }

        if (score < 0) score = 0;

        QualityStatus status;
        if (score >= 80) {
            status = QualityStatus.GOOD;
        } else if (score >= 50) {
            status = QualityStatus.MODERATE;
        } else {
            status = QualityStatus.BAD;
        }

        if (details.length() == 0) {
            details.append("Paramètres dans la plage normale.");
        }

        QualityObservation observation = QualityObservation.builder()
                .stationId(stationId)
                .timestamp(LocalDateTime.now())
                .score(score)
                .status(status)
                .details(details.toString())
                .build();

        return observationRepository.save(observation);
    }

    public QualityObservation getLatestObservation(Long stationId) {
        return observationRepository.findFirstByStationIdOrderByTimestampDesc(stationId)
                .orElse(null);
    }
}
