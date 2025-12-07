package sensorservice.web.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MeasurementResponse {

    private Long id;
    private Long stationId;
    private String stationName;
    private LocalDateTime timestamp;

    private Double ph;
    private Double temperature;
    private Double turbidity;
    private Double dissolvedOxygen;
    private Double conductivity;
}