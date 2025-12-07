package ma.projet.mapservice.web.dto;

import lombok.Data;

@Data
public class StationDTO {
    private Long id;
    private String code;
    private String name;
    private String type;
    private Double latitude;
    private Double longitude;
    private String commune;
    private String description;
}