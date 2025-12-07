package stmodelservice.web.dto;

import stmodelservice.domain.enums.MetricType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SatelliteMetricDTO {
    private Long stationId;
    private MetricType metricType;
    private Double value;
    private String unit;
    private LocalDateTime createdAt;
    private String productId;
}