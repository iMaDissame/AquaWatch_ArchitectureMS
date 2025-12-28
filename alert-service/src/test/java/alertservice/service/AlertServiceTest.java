package alertservice.service;

import alertservice.domain.entity.Alert;
import alertservice.domain.enums.AlertSeverity;
import alertservice.domain.enums.AlertSourceType;
import alertservice.domain.enums.AlertStatus;
import alertservice.repository.AlertRepository;
import alertservice.web.dto.AlertResponse;
import alertservice.web.dto.QualityForecastDTO;
import alertservice.web.dto.QualityObservationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires simples pour AlertService
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AlertService Tests")
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertService alertService;

    private Alert sampleAlert;

    @BeforeEach
    void setUp() {
        sampleAlert = Alert.builder()
                .id(1L)
                .stationId(100L)
                .sourceId(1L)
                .sourceType(AlertSourceType.OBSERVATION)
                .severity(AlertSeverity.WARNING)
                .status(AlertStatus.OPEN)
                .type("THRESHOLD_BREACH")
                .title("Test Alert")
                .message("Test message")
                .score(45.0)
                .eventTime(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ========== Tests createAlertFromObservation ==========

    @Test
    @DisplayName("Créer alerte CRITICAL pour observation BAD")
    void shouldCreateCriticalAlertForBadObservation() {
        QualityObservationDTO obs = new QualityObservationDTO();
        obs.setId(1L);
        obs.setStationId(100L);
        obs.setTimestamp(LocalDateTime.now());
        obs.setScore(30.0);
        obs.setStatus("BAD");
        obs.setDetails("Mauvaise qualité");

        when(alertRepository.save(any(Alert.class))).thenAnswer(inv -> {
            Alert a = inv.getArgument(0);
            a.setId(1L);
            return a;
        });

        AlertResponse result = alertService.createAlertFromObservation(obs);

        assertThat(result).isNotNull();
        assertThat(result.getSeverity()).isEqualTo(AlertSeverity.CRITICAL);
        verify(alertRepository).save(any(Alert.class));
    }

    @Test
    @DisplayName("Créer alerte WARNING pour observation MODERATE")
    void shouldCreateWarningAlertForModerateObservation() {
        QualityObservationDTO obs = new QualityObservationDTO();
        obs.setId(1L);
        obs.setStationId(100L);
        obs.setTimestamp(LocalDateTime.now());
        obs.setScore(55.0);
        obs.setStatus("MODERATE");

        when(alertRepository.save(any(Alert.class))).thenAnswer(inv -> {
            Alert a = inv.getArgument(0);
            a.setId(1L);
            return a;
        });

        AlertResponse result = alertService.createAlertFromObservation(obs);

        assertThat(result).isNotNull();
        assertThat(result.getSeverity()).isEqualTo(AlertSeverity.WARNING);
    }

    @Test
    @DisplayName("Ne pas créer d'alerte pour observation GOOD")
    void shouldNotCreateAlertForGoodObservation() {
        QualityObservationDTO obs = new QualityObservationDTO();
        obs.setStatus("GOOD");
        obs.setScore(85.0);

        AlertResponse result = alertService.createAlertFromObservation(obs);

        assertThat(result).isNull();
        verify(alertRepository, never()).save(any(Alert.class));
    }

    // ========== Tests createAlertFromForecast ==========

    @Test
    @DisplayName("Créer alerte pour prévision BAD")
    void shouldCreateAlertFromBadForecast() {
        QualityForecastDTO forecast = new QualityForecastDTO();
        forecast.setId(1L);
        forecast.setStationId(100L);
        forecast.setForecastTime(LocalDateTime.now().plusHours(24));
        forecast.setPredictedScore(25.0);
        forecast.setPredictedStatus("BAD");
        forecast.setModelName("WQI-ML");

        when(alertRepository.save(any(Alert.class))).thenAnswer(inv -> {
            Alert a = inv.getArgument(0);
            a.setId(1L);
            return a;
        });

        AlertResponse result = alertService.createAlertFromForecast(forecast);

        assertThat(result).isNotNull();
        assertThat(result.getSeverity()).isEqualTo(AlertSeverity.WARNING);
    }

    @Test
    @DisplayName("Ne pas créer d'alerte pour prévision GOOD")
    void shouldNotCreateAlertFromGoodForecast() {
        QualityForecastDTO forecast = new QualityForecastDTO();
        forecast.setPredictedStatus("GOOD");

        AlertResponse result = alertService.createAlertFromForecast(forecast);

        assertThat(result).isNull();
    }

    // ========== Tests acknowledge/resolve ==========

    @Test
    @DisplayName("Acquitter une alerte OPEN")
    void shouldAcknowledgeOpenAlert() {
        sampleAlert.setStatus(AlertStatus.OPEN);
        when(alertRepository.findById(1L)).thenReturn(Optional.of(sampleAlert));
        when(alertRepository.save(any(Alert.class))).thenReturn(sampleAlert);

        AlertResponse result = alertService.acknowledge(1L);

        assertThat(result.getStatus()).isEqualTo(AlertStatus.ACKNOWLEDGED);
    }

    @Test
    @DisplayName("Résoudre une alerte")
    void shouldResolveAlert() {
        when(alertRepository.findById(1L)).thenReturn(Optional.of(sampleAlert));
        when(alertRepository.save(any(Alert.class))).thenReturn(sampleAlert);

        AlertResponse result = alertService.resolve(1L);

        assertThat(result.getStatus()).isEqualTo(AlertStatus.RESOLVED);
        assertThat(sampleAlert.getResolvedAt()).isNotNull();
    }

    @Test
    @DisplayName("Erreur si alerte non trouvée")
    void shouldThrowExceptionWhenAlertNotFound() {
        when(alertRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> alertService.acknowledge(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    // ========== Tests getters ==========

    @Test
    @DisplayName("Retourner alertes actives")
    void shouldReturnActiveAlerts() {
        List<Alert> alerts = Arrays.asList(sampleAlert);
        when(alertRepository.findByStatus(AlertStatus.OPEN)).thenReturn(alerts);
        when(alertRepository.findByStatus(AlertStatus.ACKNOWLEDGED)).thenReturn(Arrays.asList());

        List<AlertResponse> result = alertService.getActiveAlerts();

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Retourner alertes par station")
    void shouldReturnAlertsForStation() {
        List<Alert> alerts = Arrays.asList(sampleAlert);
        when(alertRepository.findByStationIdAndStatus(100L, AlertStatus.OPEN)).thenReturn(alerts);

        List<AlertResponse> result = alertService.getAlertsForStation(100L, AlertStatus.OPEN);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStationId()).isEqualTo(100L);
    }
}
