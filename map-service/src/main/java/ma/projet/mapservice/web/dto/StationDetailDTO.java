package ma.projet.mapservice.web.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class StationDetailDTO {

    private Long stationId;
    private String code;
    private String name;
    private Double latitude;
    private Double longitude;
    private String commune;
    private String description;

    private MeasurementDTO latestMeasurement;
    private QualityObservationDTO latestObservation;
    private QualityForecastDTO latestForecast;

    private List<AlertDTO> activeAlerts;
}
