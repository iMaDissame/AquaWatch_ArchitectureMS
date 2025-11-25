package sensorservice.web.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MeasurementCreateRequest {

    @NotNull
    private Long stationId;

    // Tu peux adapter les @DecimalMin / @DecimalMax avec les ranges du dataset

    @NotNull @DecimalMin("0.0") @DecimalMax("14.0")
    private Double ph;

    @NotNull
    private Double temperature;

    @NotNull @DecimalMin("0.0")
    private Double turbidity;

    @NotNull @DecimalMin("0.0")
    private Double dissolvedOxygen;

    @NotNull @DecimalMin("0.0")
    private Double conductivity;
}
