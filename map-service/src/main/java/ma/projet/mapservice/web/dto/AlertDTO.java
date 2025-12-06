package ma.projet.mapservice.web.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertDTO {
    private Long id;
    private Long stationId;
    private String sourceType;
    private Long sourceId;
    private String type;
    private String severity;     // INFO/WARNING/CRITICAL
    private String status;       // OPEN/ACKNOWLEDGED/RESOLVED
    private String title;
    private String message;
    private Double score;
    private LocalDateTime eventTime;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
