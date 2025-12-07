package alertservice.web.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QualityObservationDTO {

    private Long id;              // id de l'observation STModel
    private Long stationId;
    private LocalDateTime timestamp;
    private Double score;
    private String status;        // "GOOD", "MODERATE", "BAD"
    private String details;
}
