package ma.projet.mapservice.web.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QualityForecastDTO {
    private Long stationId;
    private LocalDateTime forecastTime;
    private Double predictedScore;
    private String predictedStatus;
    private String modelName;
    private String modelVersion;
}
