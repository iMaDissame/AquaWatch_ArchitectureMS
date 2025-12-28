package stmodelservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import stmodelservice.client.SatelliteClient;
import stmodelservice.client.SensorClient;
import stmodelservice.domain.entity.QualityObservation;
import stmodelservice.domain.enums.QualityStatus;
import stmodelservice.repository.QualityObservationRepository;
import stmodelservice.web.dto.SensorMeasurementDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires simples pour QualityCalculationService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("QualityCalculationService Tests")
class QualityCalculationServiceTest {

    @Mock
    private SensorClient sensorClient;

    @Mock
    private SatelliteClient satelliteClient;

    @Mock
    private QualityObservationRepository observationRepository;

    @InjectMocks
    private QualityCalculationService qualityCalculationService;

    private SensorMeasurementDTO goodMeasurement;
    private SensorMeasurementDTO badMeasurement;

    @BeforeEach
    void setUp() {
        // Mesure de bonne qualité
        goodMeasurement = new SensorMeasurementDTO();
        goodMeasurement.setId(1L);
        goodMeasurement.setStationId(100L);
        goodMeasurement.setTimestamp(LocalDateTime.now());
        goodMeasurement.setPh(7.0);
        goodMeasurement.setTemperature(20.0);
        goodMeasurement.setTurbidity(0.5);
        goodMeasurement.setDissolvedOxygen(9.0);
        goodMeasurement.setConductivity(350.0);

        // Mesure de mauvaise qualité
        badMeasurement = new SensorMeasurementDTO();
        badMeasurement.setId(2L);
        badMeasurement.setStationId(100L);
        badMeasurement.setTimestamp(LocalDateTime.now());
        badMeasurement.setPh(5.0);
        badMeasurement.setTemperature(35.0);
        badMeasurement.setTurbidity(15.0);
        badMeasurement.setDissolvedOxygen(3.0);
        badMeasurement.setConductivity(1000.0);
    }

    // ========== Tests computeCurrentQuality ==========

    @Test
    @DisplayName("Calcul qualité GOOD pour mesures idéales")
    void shouldReturnGoodStatusForIdealMeasurements() {
        when(sensorClient.getLatestMeasurement(100L)).thenReturn(goodMeasurement);
        when(satelliteClient.getLatestMetricsForStation(100L)).thenReturn(new ArrayList<>());
        when(observationRepository.save(any(QualityObservation.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        QualityObservation result = qualityCalculationService.computeCurrentQuality(100L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(QualityStatus.GOOD);
        assertThat(result.getScore()).isGreaterThanOrEqualTo(70.0);
    }

    @Test
    @DisplayName("Calcul qualité BAD pour mauvaises mesures")
    void shouldReturnBadStatusForBadMeasurements() {
        when(sensorClient.getLatestMeasurement(100L)).thenReturn(badMeasurement);
        when(satelliteClient.getLatestMetricsForStation(100L)).thenReturn(new ArrayList<>());
        when(observationRepository.save(any(QualityObservation.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        QualityObservation result = qualityCalculationService.computeCurrentQuality(100L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(QualityStatus.BAD);
        assertThat(result.getScore()).isLessThan(40.0);
    }

    @Test
    @DisplayName("Erreur si aucune donnée capteur")
    void shouldThrowExceptionWhenNoSensorData() {
        when(sensorClient.getLatestMeasurement(100L)).thenReturn(null);

        assertThatThrownBy(() -> qualityCalculationService.computeCurrentQuality(100L))
                .isInstanceOf(IllegalStateException.class);
    }

    // ========== Tests predictQuality ==========

    @Test
    @DisplayName("Prédiction GOOD pour valeurs idéales")
    void shouldPredictGoodForIdealValues() {
        QualityCalculationService.PredictionResult result = qualityCalculationService.predictQuality(7.0, 20.0, 0.5,
                9.0, 350.0);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(QualityStatus.GOOD);
        assertThat(result.getScore()).isGreaterThanOrEqualTo(70.0);
    }

    @Test
    @DisplayName("Prédiction BAD pour valeurs critiques")
    void shouldPredictBadForCriticalValues() {
        QualityCalculationService.PredictionResult result = qualityCalculationService.predictQuality(5.0, 35.0, 15.0,
                3.0, 1000.0);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(QualityStatus.BAD);
    }

    @Test
    @DisplayName("Résultat inclut les scores par paramètre")
    void shouldIncludeParameterScores() {
        QualityCalculationService.PredictionResult result = qualityCalculationService.predictQuality(7.0, 20.0, 0.5,
                9.0, 350.0);

        assertThat(result.getParameterScores()).isNotNull();
        assertThat(result.getParameterScores()).containsKeys("ph", "temperature", "turbidity");
    }

    // ========== Tests getters ==========

    @Test
    @DisplayName("Récupérer dernière observation")
    void shouldReturnLatestObservation() {
        QualityObservation obs = QualityObservation.builder()
                .id(1L)
                .stationId(100L)
                .score(85.0)
                .status(QualityStatus.GOOD)
                .build();
        when(observationRepository.findFirstByStationIdOrderByTimestampDesc(100L))
                .thenReturn(Optional.of(obs));

        QualityObservation result = qualityCalculationService.getLatestObservation(100L);

        assertThat(result).isNotNull();
        assertThat(result.getScore()).isEqualTo(85.0);
    }

    @Test
    @DisplayName("Retourner null si pas d'observation")
    void shouldReturnNullWhenNoObservation() {
        when(observationRepository.findFirstByStationIdOrderByTimestampDesc(100L))
                .thenReturn(Optional.empty());

        QualityObservation result = qualityCalculationService.getLatestObservation(100L);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Récupérer observations par station")
    void shouldReturnObservationsByStation() {
        List<QualityObservation> observations = Arrays.asList(
                QualityObservation.builder().id(1L).stationId(100L).build(),
                QualityObservation.builder().id(2L).stationId(100L).build());
        when(observationRepository.findByStationIdOrderByTimestampDesc(100L))
                .thenReturn(observations);

        List<QualityObservation> result = qualityCalculationService.getObservationsByStation(100L);

        assertThat(result).hasSize(2);
    }
}
