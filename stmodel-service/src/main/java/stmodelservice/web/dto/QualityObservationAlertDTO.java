package stmodelservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pour envoyer une observation au service d'alertes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualityObservationAlertDTO {
    private Long id;
    private Long stationId;
    private LocalDateTime timestamp;
    private Double score;
    private String status;  // "GOOD", "MODERATE", "BAD"
    private String details;
}
