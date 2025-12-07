package stmodelservice.web.dto;

import stmodelservice.domain.enums.QualityStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class QualityObservationResponse {

    private Long stationId;
    private LocalDateTime timestamp;
    private Double score;
    private QualityStatus status;
    private String details;
}
