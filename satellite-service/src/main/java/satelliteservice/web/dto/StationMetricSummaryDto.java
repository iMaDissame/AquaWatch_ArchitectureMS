package satelliteservice.web.dto;

import lombok.Builder;
import lombok.Data;
import satelliteservice.domain.enums.MetricType;

import java.time.LocalDateTime;


// DTO pour résumé par station (pour STModel ou API-SIG)


@Data
@Builder
public class StationMetricSummaryDto {

    private Long stationId;
    private MetricType metricType;
    private Double value;
    private String unit;
    private LocalDateTime createdAt;
    private String productId;   // de la scene
}
