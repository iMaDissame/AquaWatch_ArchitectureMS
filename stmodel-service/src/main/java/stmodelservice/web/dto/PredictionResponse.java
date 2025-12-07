package stmodelservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stmodelservice.domain.enums.QualityStatus;

import java.util.List;
import java.util.Map;

/**
 * DTO pour la réponse de prédiction de qualité
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionResponse {
    
    private Long stationId;
    private Double score;
    private QualityStatus status;
    private String details;
    private Map<String, Double> parameterScores;
    private List<String> recommendations;
}
