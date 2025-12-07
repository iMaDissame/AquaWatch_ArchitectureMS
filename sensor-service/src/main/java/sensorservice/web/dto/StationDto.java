package sensorservice.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StationDto {

    private Long id;

    private String code;        // identifiant lisible (ex: ST01)
    private String name;        // nom de la station

    private String type;        // type de station (river, lake, groundwater, ...)

    private Double latitude;    // peut être null si pas encore géolocalisée
    private Double longitude;

    private String commune;     // commune / ville / région
    private String description; // description courte de la station
}
