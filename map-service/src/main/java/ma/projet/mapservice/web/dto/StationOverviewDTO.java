package ma.projet.mapservice.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StationOverviewDTO {

    private Long stationId;
    private String code;
    private String name;
    private Double latitude;
    private Double longitude;

    private Double currentScore;
    private String currentStatus;     // GOOD/MODERATE/BAD

    private boolean hasActiveAlert;
    private String latestAlertSeverity;   // CRITICAL/WARNING/...

    private String lastMeasurementTime;   // format string pour UI
}
