package stmodelservice.web.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QualityForecastResponse {

    private Long id;
    private Long stationId;
    private LocalDateTime forecastTime;
    private LocalDateTime createdAt;
    private Double predictedScore;
    private String predictedStatus;
    private String modelName;
    private String modelVersion;
    private Double confidence;
    private Double lowerBound;
    private Double upperBound;
}
