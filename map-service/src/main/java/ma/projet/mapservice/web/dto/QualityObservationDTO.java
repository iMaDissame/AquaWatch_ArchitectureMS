package ma.projet.mapservice.web.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QualityObservationDTO {
    private Long stationId;
    private LocalDateTime timestamp;
    private Double score;
    private String status;   // "GOOD","MODERATE","BAD"
    private String details;
}
