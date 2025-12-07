package alertservice.service;


import alertservice.domain.entity.Alert;
import alertservice.domain.enums.AlertSeverity;
import alertservice.domain.enums.AlertSourceType;
import alertservice.domain.enums.AlertStatus;
import alertservice.repository.AlertRepository;
import alertservice.web.dto.AlertResponse;
import alertservice.web.dto.QualityForecastDTO;
import alertservice.web.dto.QualityObservationDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;


    // ----------- à partir d'une observation -----------
    @Transactional
    public AlertResponse createAlertFromObservation(QualityObservationDTO obs) {
        String status = obs.getStatus(); // "GOOD", "MODERATE", "BAD"
        Double score = obs.getScore();

        // règle simple : on n'alerte que MODERATE ou BAD
        if ("GOOD".equalsIgnoreCase(status)) {
            return null; // pas d'alerte
        }

        AlertSeverity severity;
        String type = "THRESHOLD_BREACH";
        String title;
        String message = "Quality status: " + status + ", score=" + score + ". Details: " + obs.getDetails();

        if ("BAD".equalsIgnoreCase(status) || (score != null && score < 50)) {
            severity = AlertSeverity.CRITICAL;
            title = "Critical water quality issue";
        } else {
            severity = AlertSeverity.WARNING;
            title = "Degradation of water quality";
        }

        Alert alert = Alert.builder()
                .stationId(obs.getStationId())
                .sourceId(obs.getId())
                .sourceType(AlertSourceType.OBSERVATION)
                .createdAt(LocalDateTime.now())
                .eventTime(obs.getTimestamp())
                .severity(severity)
                .status(AlertStatus.OPEN)
                .type(type)
                .title(title)
                .message(message)
                .score(score)
                .build();

        Alert saved = alertRepository.save(alert);
        return toResponse(saved);
    }

    // ----------- à partir d'un forecast -----------
    @Transactional
    public AlertResponse createAlertFromForecast(QualityForecastDTO forecast) {
        String status = forecast.getPredictedStatus();
        Double score = forecast.getPredictedScore();

        // ex : on alerte seulement si forecast indique BAD
        if (!"BAD".equalsIgnoreCase(status)) {
            return null; // pas d'alerte si forecast GOOD/MODERATE (version simple)
        }

        AlertSeverity severity = AlertSeverity.WARNING; // prévision = warning, pas encore critique
        String type = "FORECAST_RISK";
        String title = "Predicted high-risk water quality";
        String message = "Forecast (" + forecast.getModelName() + "): status="
                + status + ", score=" + score
                + " at " + forecast.getForecastTime();

        Alert alert = Alert.builder()
                .stationId(forecast.getStationId())
                .sourceId(forecast.getId())
                .sourceType(AlertSourceType.FORECAST)
                .createdAt(LocalDateTime.now())
                .eventTime(forecast.getForecastTime())
                .severity(severity)
                .status(AlertStatus.OPEN)
                .type(type)
                .title(title)
                .message(message)
                .score(score)
                .build();

        Alert saved = alertRepository.save(alert);
        return toResponse(saved);
    }

    // ----------- gestion & consultation -----------
    public List<AlertResponse> getActiveAlerts() {
        return alertRepository.findByStatus(AlertStatus.OPEN).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AlertResponse> getActiveAlertsForStation(Long stationId) {
        return alertRepository.findByStationIdAndStatus(stationId, AlertStatus.OPEN).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AlertResponse> getAlertsForStation(Long stationId, AlertStatus status) {
        List<Alert> alerts;
        if (status == null) {
            alerts = alertRepository.findByStationId(stationId);
        } else {
            alerts = alertRepository.findByStationIdAndStatus(stationId, status);
        }
        return alerts.stream().map(this::toResponse).toList();
    }

    @Transactional
    public AlertResponse acknowledge(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + alertId));
        if (alert.getStatus() == AlertStatus.OPEN) {
            alert.setStatus(AlertStatus.ACKNOWLEDGED);
        }
        return toResponse(alert);
    }

    @Transactional
    public AlertResponse resolve(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + alertId));
        alert.setStatus(AlertStatus.RESOLVED);
        alert.setResolvedAt(LocalDateTime.now());
        return toResponse(alert);
    }

    private AlertResponse toResponse(Alert a) {
        return AlertResponse.builder()
                .id(a.getId())
                .stationId(a.getStationId())
                .sourceType(a.getSourceType())
                .sourceId(a.getSourceId())
                .type(a.getType())
                .severity(a.getSeverity())
                .status(a.getStatus())
                .title(a.getTitle())
                .message(a.getMessage())
                .score(a.getScore())
                .eventTime(a.getEventTime())
                .createdAt(a.getCreatedAt())
                .resolvedAt(a.getResolvedAt())
                .build();
    }
}
