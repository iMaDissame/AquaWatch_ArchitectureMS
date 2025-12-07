package stmodelservice.web.dto;

import stmodelservice.domain.enums.QualityStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QualityForecastResponse {

    private Long stationId;
    private LocalDateTime forecastTime;
    private Double predictedScore;
    private QualityStatus predictedStatus;
    private String modelName;
    private String modelVersion;
}
