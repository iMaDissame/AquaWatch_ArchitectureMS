package stmodelservice.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la demande de prédiction de qualité de l'eau
 * Permet de prédire la qualité sans avoir besoin d'une station existante
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionRequest {
    
    private Long stationId; // optionnel, pour référence
    private Double ph;
    private Double temperature;
    private Double turbidity;
    private Double dissolvedOxygen;
    private Double conductivity;
}
