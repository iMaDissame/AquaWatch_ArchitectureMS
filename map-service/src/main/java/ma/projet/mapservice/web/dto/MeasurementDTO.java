package ma.projet.mapservice.web.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MeasurementDTO {
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
