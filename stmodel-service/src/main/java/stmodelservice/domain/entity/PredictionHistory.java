package stmodelservice.domain.entity;

import stmodelservice.domain.enums.QualityStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entité pour stocker l'historique des prédictions de qualité de l'eau par station.
 * Chaque prédiction effectuée est sauvegardée pour analyse ultérieure.
 */
@Entity
@Table(name = "prediction_history")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PredictionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long stationId;

    @Column(nullable = false)
    private LocalDateTime predictionTimestamp;

    // Paramètres d'entrée
    @Column
    private Double ph;
    
    @Column
    private Double temperature;
    
    @Column
    private Double turbidity;
    
    @Column
    private Double dissolvedOxygen;
    
    @Column
    private Double conductivity;

    // Résultats de la prédiction
    @Column(nullable = false)
    private Double score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QualityStatus status;

    @Column(length = 1000)
    private String details;

    // Scores détaillés par paramètre (stockés en JSON)
    @Column(length = 2000)
    private String parameterScoresJson;

    // Recommandations (stockées en JSON)
    @Column(length = 2000)
    private String recommendationsJson;
}
