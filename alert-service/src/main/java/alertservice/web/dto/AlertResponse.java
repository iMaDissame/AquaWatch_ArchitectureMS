package alertservice.web.dto;


import alertservice.domain.enums.AlertSeverity;
import alertservice.domain.enums.AlertSourceType;
import alertservice.domain.enums.AlertStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AlertResponse {

    private Long id;
    private Long stationId;
    private AlertSourceType sourceType;
    private Long sourceId;
    private String type;
    private AlertSeverity severity;
    private AlertStatus status;
    private String title;
    private String message;
    private Double score;
    private LocalDateTime eventTime;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
