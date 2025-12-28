package alertservice.web.controller;

import alertservice.domain.enums.AlertSeverity;
import alertservice.domain.enums.AlertSourceType;
import alertservice.domain.enums.AlertStatus;
import alertservice.service.AlertService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires simples pour AlertController
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AlertController Tests")
class AlertControllerTest {

    @Mock
    private AlertService alertService;

    @InjectMocks
    private AlertController alertController;

    private AlertResponse sampleResponse;

    @BeforeEach
    void setUp() {
        sampleResponse = AlertResponse.builder()
                .id(1L)
                .stationId(100L)
                .sourceType(AlertSourceType.OBSERVATION)
                .severity(AlertSeverity.WARNING)
                .status(AlertStatus.OPEN)
                .title("Test Alert")
                .build();
    }

    @Test
    @DisplayName("createFromObservation retourne AlertResponse")
    void shouldCreateAlertFromObservation() {
        QualityObservationDTO obs = new QualityObservationDTO();
        obs.setStatus("BAD");
        when(alertService.createAlertFromObservation(any())).thenReturn(sampleResponse);

        AlertResponse result = alertController.createFromObservation(obs);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("createFromForecast retourne AlertResponse")
    void shouldCreateAlertFromForecast() {
        QualityForecastDTO forecast = new QualityForecastDTO();
        forecast.setPredictedStatus("BAD");
        when(alertService.createAlertFromForecast(any())).thenReturn(sampleResponse);

        AlertResponse result = alertController.createFromForecast(forecast);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("getActive retourne liste d'alertes")
    void shouldReturnActiveAlerts() {
        when(alertService.getActiveAlerts()).thenReturn(Arrays.asList(sampleResponse));

        List<AlertResponse> result = alertController.getActive();

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getActiveForStation retourne alertes de la station")
    void shouldReturnActiveAlertsForStation() {
        when(alertService.getActiveAlertsForStation(100L)).thenReturn(Arrays.asList(sampleResponse));

        List<AlertResponse> result = alertController.getActiveForStation(100L);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("acknowledge met à jour le statut")
    void shouldAcknowledgeAlert() {
        AlertResponse acknowledged = AlertResponse.builder()
                .id(1L)
                .status(AlertStatus.ACKNOWLEDGED)
                .build();
        when(alertService.acknowledge(1L)).thenReturn(acknowledged);

        AlertResponse result = alertController.acknowledge(1L);

        assertThat(result.getStatus()).isEqualTo(AlertStatus.ACKNOWLEDGED);
    }

    @Test
    @DisplayName("resolve met à jour le statut")
    void shouldResolveAlert() {
        AlertResponse resolved = AlertResponse.builder()
                .id(1L)
                .status(AlertStatus.RESOLVED)
                .build();
        when(alertService.resolve(1L)).thenReturn(resolved);

        AlertResponse result = alertController.resolve(1L);

        assertThat(result.getStatus()).isEqualTo(AlertStatus.RESOLVED);
    }
}
