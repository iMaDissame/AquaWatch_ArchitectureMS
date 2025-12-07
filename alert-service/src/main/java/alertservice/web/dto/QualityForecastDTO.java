package alertservice.web.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QualityForecastDTO {

    private Long id;                 // id du forecast STModel
    private Long stationId;
    private LocalDateTime forecastTime;
    private Double predictedScore;
    private String predictedStatus;  // "GOOD", "MODERATE", "BAD"
    private String modelName;
    private String modelVersion;
}
