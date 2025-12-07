package stmodelservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stmodelservice.domain.enums.QualityStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO pour afficher l'historique des prédictions
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionHistoryDTO {
    
    private Long id;
    private Long stationId;
    private LocalDateTime predictionTimestamp;
    
    // Paramètres d'entrée
    private Double ph;
    private Double temperature;
    private Double turbidity;
    private Double dissolvedOxygen;
    private Double conductivity;
    
    // Résultats
    private Double score;
    private QualityStatus status;
    private String details;
    private Map<String, Double> parameterScores;
    private List<String> recommendations;
}
