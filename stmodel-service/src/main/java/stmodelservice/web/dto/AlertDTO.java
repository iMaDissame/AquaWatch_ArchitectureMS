package stmodelservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de réponse pour les alertes créées
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertDTO {
    private Long id;
    private Long stationId;
    private String type;       // THRESHOLD_BREACH, FORECAST_RISK, SENSOR_FAILURE
    private String severity;   // CRITICAL, WARNING, INFO
    private String status;     // OPEN, ACKNOWLEDGED, RESOLVED
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime acknowledgedAt;
    private LocalDateTime resolvedAt;
}
