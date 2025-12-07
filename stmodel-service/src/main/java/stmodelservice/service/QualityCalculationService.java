package stmodelservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stmodelservice.client.SatelliteClient;
import stmodelservice.client.SensorClient;
import stmodelservice.domain.entity.QualityObservation;
import stmodelservice.domain.enums.QualityStatus;
import stmodelservice.repository.QualityObservationRepository;
import stmodelservice.web.dto.SatelliteMetricDTO;
import stmodelservice.web.dto.SensorMeasurementDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service de calcul de qualité de l'eau basé sur le Water Quality Index (WQI)
 * 
 * Algorithme inspiré des normes OMS et du dataset Water Quality Testing:
 * - pH: 6.5-8.5 (OMS recommandé pour l'eau potable)
 * - Temperature: 20-25°C (optimal pour écosystèmes aquatiques)
 * - Turbidity: <5 NTU (OMS pour eau potable)
 * - Dissolved Oxygen: >6 mg/L (critique pour vie aquatique)
 * - Conductivity: 200-800 µS/cm (plage normale eau douce)
 * 
 * Dataset de référence (500 samples):
 * - pH: 6.83 - 7.48
 * - Temperature: 20.3 - 23.6°C
 * - Turbidity: 3.1 - 5.1 NTU
 * - Dissolved Oxygen: 6.0 - 9.9 mg/L
 * - Conductivity: 316 - 370 µS/cm
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QualityCalculationService {

    private final SensorClient sensorClient;
    private final SatelliteClient satelliteClient;
    private final QualityObservationRepository observationRepository;

    // ============================================
    // CONSTANTES - NORMES OMS ET SEUILS
    // ============================================
    
    // pH (OMS: 6.5-8.5 pour eau potable)
    private static final double PH_MIN_IDEAL = 6.5;
    private static final double PH_MAX_IDEAL = 8.5;
    private static final double PH_MIN_ACCEPTABLE = 6.0;
    private static final double PH_MAX_ACCEPTABLE = 9.0;
    private static final double PH_WEIGHT = 0.20; // 20% du score total

    // Température (°C) - optimal pour écosystèmes
    private static final double TEMP_MIN_IDEAL = 15.0;
    private static final double TEMP_MAX_IDEAL = 25.0;
    private static final double TEMP_MIN_ACCEPTABLE = 10.0;
    private static final double TEMP_MAX_ACCEPTABLE = 30.0;
    private static final double TEMP_WEIGHT = 0.15; // 15% du score total

    // Turbidité (NTU) - OMS: <5 NTU pour eau potable
    private static final double TURBIDITY_IDEAL = 1.0;
    private static final double TURBIDITY_ACCEPTABLE = 5.0;
    private static final double TURBIDITY_BAD = 10.0;
    private static final double TURBIDITY_WEIGHT = 0.20; // 20% du score total

    // Oxygène Dissous (mg/L) - critique pour vie aquatique
    private static final double DO_MIN_IDEAL = 8.0;
    private static final double DO_MIN_ACCEPTABLE = 6.0;
    private static final double DO_MIN_CRITICAL = 4.0;
    private static final double DO_WEIGHT = 0.25; // 25% du score total

    // Conductivité (µS/cm) - indicateur de minéralisation
    private static final double COND_MIN_IDEAL = 200.0;
    private static final double COND_MAX_IDEAL = 500.0;
    private static final double COND_MAX_ACCEPTABLE = 800.0;
    private static final double COND_WEIGHT = 0.10; // 10% du score total

    // Satellite - NDWI et indices
    private static final double SATELLITE_WEIGHT = 0.10; // 10% du score total

    // ============================================
    // CALCUL PRINCIPAL
    // ============================================

    @Transactional
    public QualityObservation computeCurrentQuality(Long stationId) {
        log.info("Calcul de qualité pour station {}", stationId);
        
        SensorMeasurementDTO sensor = null;
        List<SatelliteMetricDTO> metrics = null;
        List<String> issues = new ArrayList<>();
        
        // Récupérer données capteurs
        try {
            sensor = sensorClient.getLatestMeasurement(stationId);
        } catch (Exception e) {
            log.warn("Impossible de récupérer les données capteur pour station {}: {}", stationId, e.getMessage());
        }
        
        if (sensor == null) {
            throw new IllegalStateException("Aucune donnée capteur disponible pour la station " + stationId);
        }
        
        // Récupérer données satellite (optionnel)
        try {
            metrics = satelliteClient.getLatestMetricsForStation(stationId);
        } catch (Exception e) {
            log.warn("Impossible de récupérer les données satellite pour station {}: {}", stationId, e.getMessage());
        }

        // Calculer les sous-scores
        double phScore = calculatePhScore(sensor.getPh(), issues);
        double tempScore = calculateTemperatureScore(sensor.getTemperature(), issues);
        double turbidityScore = calculateTurbidityScore(sensor.getTurbidity(), issues);
        double doScore = calculateDissolvedOxygenScore(sensor.getDissolvedOxygen(), issues);
        double condScore = calculateConductivityScore(sensor.getConductivity(), issues);
        double satelliteScore = calculateSatelliteScore(metrics, issues);

        // Calcul du score pondéré (WQI)
        double totalScore = (phScore * PH_WEIGHT) +
                           (tempScore * TEMP_WEIGHT) +
                           (turbidityScore * TURBIDITY_WEIGHT) +
                           (doScore * DO_WEIGHT) +
                           (condScore * COND_WEIGHT) +
                           (satelliteScore * SATELLITE_WEIGHT);

        // Normaliser entre 0 et 100
        totalScore = Math.max(0, Math.min(100, totalScore));

        // Déterminer le statut
        QualityStatus status = determineStatus(totalScore);

        // Construire les détails
        String details = buildDetails(sensor, totalScore, issues);

        log.info("Station {} - Score: {}, Status: {}", stationId, totalScore, status);

        QualityObservation observation = QualityObservation.builder()
                .stationId(stationId)
                .timestamp(LocalDateTime.now())
                .score(Math.round(totalScore * 100.0) / 100.0) // 2 décimales
                .status(status)
                .details(details)
                .build();

        return observationRepository.save(observation);
    }

    // ============================================
    // CALCUL DES SOUS-SCORES
    // ============================================

    /**
     * Score pH (0-100)
     * Optimal: 6.5-8.5, Acceptable: 6.0-9.0
     */
    private double calculatePhScore(Double ph, List<String> issues) {
        if (ph == null) return 50.0;
        
        if (ph >= PH_MIN_IDEAL && ph <= PH_MAX_IDEAL) {
            return 100.0; // Parfait
        } else if (ph >= PH_MIN_ACCEPTABLE && ph < PH_MIN_IDEAL) {
            // Légèrement acide
            double ratio = (ph - PH_MIN_ACCEPTABLE) / (PH_MIN_IDEAL - PH_MIN_ACCEPTABLE);
            issues.add(String.format("pH légèrement acide (%.2f)", ph));
            return 60 + (ratio * 40);
        } else if (ph > PH_MAX_IDEAL && ph <= PH_MAX_ACCEPTABLE) {
            // Légèrement basique
            double ratio = (PH_MAX_ACCEPTABLE - ph) / (PH_MAX_ACCEPTABLE - PH_MAX_IDEAL);
            issues.add(String.format("pH légèrement basique (%.2f)", ph));
            return 60 + (ratio * 40);
        } else if (ph < PH_MIN_ACCEPTABLE) {
            issues.add(String.format("pH trop acide (%.2f) - Risque pour vie aquatique", ph));
            return Math.max(0, 30 - ((PH_MIN_ACCEPTABLE - ph) * 20));
        } else {
            issues.add(String.format("pH trop basique (%.2f) - Risque pour vie aquatique", ph));
            return Math.max(0, 30 - ((ph - PH_MAX_ACCEPTABLE) * 20));
        }
    }

    /**
     * Score Température (0-100)
     * Optimal: 15-25°C
     */
    private double calculateTemperatureScore(Double temp, List<String> issues) {
        if (temp == null) return 50.0;
        
        if (temp >= TEMP_MIN_IDEAL && temp <= TEMP_MAX_IDEAL) {
            return 100.0;
        } else if (temp >= TEMP_MIN_ACCEPTABLE && temp < TEMP_MIN_IDEAL) {
            double ratio = (temp - TEMP_MIN_ACCEPTABLE) / (TEMP_MIN_IDEAL - TEMP_MIN_ACCEPTABLE);
            issues.add(String.format("Température fraîche (%.1f°C)", temp));
            return 70 + (ratio * 30);
        } else if (temp > TEMP_MAX_IDEAL && temp <= TEMP_MAX_ACCEPTABLE) {
            double ratio = (TEMP_MAX_ACCEPTABLE - temp) / (TEMP_MAX_ACCEPTABLE - TEMP_MAX_IDEAL);
            issues.add(String.format("Température élevée (%.1f°C)", temp));
            return 70 + (ratio * 30);
        } else if (temp < TEMP_MIN_ACCEPTABLE) {
            issues.add(String.format("Température trop basse (%.1f°C)", temp));
            return Math.max(0, 40);
        } else {
            issues.add(String.format("Température critique (%.1f°C) - Stress thermique", temp));
            return Math.max(0, 20);
        }
    }

    /**
     * Score Turbidité (0-100)
     * Optimal: <1 NTU, Acceptable: <5 NTU, Mauvais: >10 NTU
     */
    private double calculateTurbidityScore(Double turbidity, List<String> issues) {
        if (turbidity == null) return 50.0;
        
        if (turbidity <= TURBIDITY_IDEAL) {
            return 100.0;
        } else if (turbidity <= TURBIDITY_ACCEPTABLE) {
            double ratio = (TURBIDITY_ACCEPTABLE - turbidity) / (TURBIDITY_ACCEPTABLE - TURBIDITY_IDEAL);
            if (turbidity > 3.0) {
                issues.add(String.format("Turbidité modérée (%.1f NTU)", turbidity));
            }
            return 60 + (ratio * 40);
        } else if (turbidity <= TURBIDITY_BAD) {
            double ratio = (TURBIDITY_BAD - turbidity) / (TURBIDITY_BAD - TURBIDITY_ACCEPTABLE);
            issues.add(String.format("Turbidité élevée (%.1f NTU) - Dépasse norme OMS", turbidity));
            return 30 + (ratio * 30);
        } else {
            issues.add(String.format("Turbidité critique (%.1f NTU) - Eau non potable", turbidity));
            return Math.max(0, 30 - ((turbidity - TURBIDITY_BAD) * 3));
        }
    }

    /**
     * Score Oxygène Dissous (0-100)
     * Optimal: >8 mg/L, Acceptable: >6 mg/L, Critique: <4 mg/L
     */
    private double calculateDissolvedOxygenScore(Double dissolvedOxygen, List<String> issues) {
        if (dissolvedOxygen == null) return 50.0;
        
        if (dissolvedOxygen >= DO_MIN_IDEAL) {
            return 100.0;
        } else if (dissolvedOxygen >= DO_MIN_ACCEPTABLE) {
            double ratio = (dissolvedOxygen - DO_MIN_ACCEPTABLE) / (DO_MIN_IDEAL - DO_MIN_ACCEPTABLE);
            issues.add(String.format("Oxygène dissous modéré (%.1f mg/L)", dissolvedOxygen));
            return 60 + (ratio * 40);
        } else if (dissolvedOxygen >= DO_MIN_CRITICAL) {
            double ratio = (dissolvedOxygen - DO_MIN_CRITICAL) / (DO_MIN_ACCEPTABLE - DO_MIN_CRITICAL);
            issues.add(String.format("Oxygène dissous faible (%.1f mg/L) - Stress pour poissons", dissolvedOxygen));
            return 30 + (ratio * 30);
        } else {
            issues.add(String.format("Oxygène dissous critique (%.1f mg/L) - Zone morte potentielle", dissolvedOxygen));
            return Math.max(0, dissolvedOxygen * 7.5);
        }
    }

    /**
     * Score Conductivité (0-100)
     * Optimal: 200-500 µS/cm, Acceptable: <800 µS/cm
     */
    private double calculateConductivityScore(Double conductivity, List<String> issues) {
        if (conductivity == null) return 50.0;
        
        if (conductivity >= COND_MIN_IDEAL && conductivity <= COND_MAX_IDEAL) {
            return 100.0;
        } else if (conductivity < COND_MIN_IDEAL) {
            // Eau très peu minéralisée
            double ratio = conductivity / COND_MIN_IDEAL;
            if (conductivity < 100) {
                issues.add(String.format("Conductivité faible (%.0f µS/cm) - Eau peu minéralisée", conductivity));
            }
            return 70 + (ratio * 30);
        } else if (conductivity <= COND_MAX_ACCEPTABLE) {
            double ratio = (COND_MAX_ACCEPTABLE - conductivity) / (COND_MAX_ACCEPTABLE - COND_MAX_IDEAL);
            issues.add(String.format("Conductivité élevée (%.0f µS/cm)", conductivity));
            return 50 + (ratio * 50);
        } else {
            issues.add(String.format("Conductivité très élevée (%.0f µS/cm) - Eau très minéralisée", conductivity));
            return Math.max(0, 50 - ((conductivity - COND_MAX_ACCEPTABLE) / 20));
        }
    }

    /**
     * Score basé sur données satellite (NDWI, turbidité satellite, etc.)
     */
    private double calculateSatelliteScore(List<SatelliteMetricDTO> metrics, List<String> issues) {
        if (metrics == null || metrics.isEmpty()) {
            return 80.0; // Score neutre si pas de données satellite
        }
        
        double score = 100.0;
        
        for (SatelliteMetricDTO metric : metrics) {
            String type = metric.getMetricType() != null ? metric.getMetricType().name() : "";
            Double value = metric.getValue();
            
            if (value == null) continue;
            
            switch (type) {
                case "NDWI_MEAN":
                    // NDWI: -1 à +1, valeurs positives = eau
                    if (value < 0) {
                        score -= 20;
                        issues.add("NDWI faible - Possible stress hydrique");
                    }
                    break;
                    
                case "TURBIDITY_INDEX":
                    // Index de turbidité satellite
                    if (value > 0.5) {
                        score -= 15;
                        issues.add("Turbidité satellite élevée");
                    }
                    break;
                    
                case "WATER_COVERAGE_PERCENT":
                    // Couverture en eau de la zone
                    if (value < 20) {
                        score -= 10;
                        issues.add("Faible couverture en eau détectée");
                    }
                    break;
            }
        }
        
        return Math.max(0, score);
    }

    // ============================================
    // UTILITAIRES
    // ============================================

    private QualityStatus determineStatus(double score) {
        if (score >= 70) {
            return QualityStatus.GOOD;
        } else if (score >= 40) {
            return QualityStatus.MODERATE;
        } else {
            return QualityStatus.BAD;
        }
    }

    private String buildDetails(SensorMeasurementDTO sensor, double score, List<String> issues) {
        StringBuilder sb = new StringBuilder();
        
        // Résumé des mesures
        sb.append(String.format("Mesures: pH=%.2f, Temp=%.1f°C, Turb=%.1f NTU, DO=%.1f mg/L, Cond=%.0f µS/cm. ",
                sensor.getPh(), sensor.getTemperature(), sensor.getTurbidity(),
                sensor.getDissolvedOxygen(), sensor.getConductivity()));
        
        // Score WQI
        sb.append(String.format("Score WQI: %.1f/100. ", score));
        
        // Problèmes détectés
        if (issues.isEmpty()) {
            sb.append("Tous les paramètres sont dans les normes.");
        } else {
            sb.append("Alertes: ");
            sb.append(String.join("; ", issues));
        }
        
        return sb.toString();
    }

    public QualityObservation getLatestObservation(Long stationId) {
        return observationRepository.findFirstByStationIdOrderByTimestampDesc(stationId)
                .orElse(null);
    }
    
    public List<QualityObservation> getObservationsByStation(Long stationId) {
        return observationRepository.findByStationIdOrderByTimestampDesc(stationId);
    }

    // ============================================
    // PRÉDICTION DIRECTE (sans station)
    // ============================================

    /**
     * Prédit la qualité de l'eau à partir de mesures fournies directement
     * @param ph pH de l'eau
     * @param temperature Température en °C
     * @param turbidity Turbidité en NTU
     * @param dissolvedOxygen Oxygène dissous en mg/L
     * @param conductivity Conductivité en µS/cm
     * @return Score de qualité et détails
     */
    public PredictionResult predictQuality(Double ph, Double temperature, Double turbidity, 
                                           Double dissolvedOxygen, Double conductivity) {
        log.info("Prédiction de qualité - pH={}, Temp={}, Turb={}, DO={}, Cond={}",
                ph, temperature, turbidity, dissolvedOxygen, conductivity);
        
        List<String> issues = new ArrayList<>();
        java.util.Map<String, Double> parameterScores = new java.util.HashMap<>();

        // Calculer les sous-scores
        double phScore = calculatePhScore(ph, issues);
        double tempScore = calculateTemperatureScore(temperature, issues);
        double turbidityScore = calculateTurbidityScore(turbidity, issues);
        double doScore = calculateDissolvedOxygenScore(dissolvedOxygen, issues);
        double condScore = calculateConductivityScore(conductivity, issues);

        parameterScores.put("ph", phScore);
        parameterScores.put("temperature", tempScore);
        parameterScores.put("turbidity", turbidityScore);
        parameterScores.put("dissolvedOxygen", doScore);
        parameterScores.put("conductivity", condScore);

        // Calcul du score pondéré (WQI) - sans satellite pour prédiction
        double adjustedWeightSum = PH_WEIGHT + TEMP_WEIGHT + TURBIDITY_WEIGHT + DO_WEIGHT + COND_WEIGHT;
        double totalScore = ((phScore * PH_WEIGHT) +
                           (tempScore * TEMP_WEIGHT) +
                           (turbidityScore * TURBIDITY_WEIGHT) +
                           (doScore * DO_WEIGHT) +
                           (condScore * COND_WEIGHT)) / adjustedWeightSum * 100 / 100;

        // Normaliser entre 0 et 100
        totalScore = Math.max(0, Math.min(100, totalScore));

        // Déterminer le statut
        QualityStatus status = determineStatus(totalScore);

        // Construire les recommandations
        List<String> recommendations = buildRecommendations(ph, temperature, turbidity, dissolvedOxygen, conductivity, issues);

        // Construire les détails
        String details = buildPredictionDetails(ph, temperature, turbidity, dissolvedOxygen, conductivity, totalScore, issues);

        log.info("Prédiction - Score: {}, Status: {}", totalScore, status);

        return new PredictionResult(
                Math.round(totalScore * 100.0) / 100.0,
                status,
                details,
                parameterScores,
                recommendations
        );
    }

    private List<String> buildRecommendations(Double ph, Double temp, Double turbidity, 
                                               Double dissolvedOxygen, Double conductivity, List<String> issues) {
        List<String> recommendations = new ArrayList<>();

        if (ph != null && (ph < PH_MIN_IDEAL || ph > PH_MAX_IDEAL)) {
            if (ph < PH_MIN_IDEAL) {
                recommendations.add("Ajouter un agent alcalinisant pour augmenter le pH");
            } else {
                recommendations.add("Ajouter un acidifiant pour diminuer le pH");
            }
        }

        if (temp != null && temp > TEMP_MAX_IDEAL) {
            recommendations.add("Améliorer l'ombrage ou la circulation d'eau pour réduire la température");
        }

        if (turbidity != null && turbidity > TURBIDITY_ACCEPTABLE) {
            recommendations.add("Installer des filtres ou bassins de décantation pour réduire la turbidité");
        }

        if (dissolvedOxygen != null && dissolvedOxygen < DO_MIN_IDEAL) {
            recommendations.add("Installer des aérateurs pour augmenter l'oxygène dissous");
        }

        if (conductivity != null && conductivity > COND_MAX_ACCEPTABLE) {
            recommendations.add("Vérifier les sources de pollution saline ou minérale");
        }

        if (recommendations.isEmpty()) {
            recommendations.add("Tous les paramètres sont dans les normes - Maintenir la surveillance régulière");
        }

        return recommendations;
    }

    private String buildPredictionDetails(Double ph, Double temp, Double turbidity, 
                                          Double dissolvedOxygen, Double conductivity,
                                          double score, List<String> issues) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(String.format("Mesures analysées: pH=%.2f, Temp=%.1f°C, Turb=%.1f NTU, DO=%.1f mg/L, Cond=%.0f µS/cm. ",
                ph != null ? ph : 0, 
                temp != null ? temp : 0, 
                turbidity != null ? turbidity : 0,
                dissolvedOxygen != null ? dissolvedOxygen : 0, 
                conductivity != null ? conductivity : 0));
        
        sb.append(String.format("Score WQI prédit: %.1f/100. ", score));
        
        if (issues.isEmpty()) {
            sb.append("Tous les paramètres sont dans les normes OMS.");
        } else {
            sb.append("Points d'attention: ");
            sb.append(String.join("; ", issues));
        }
        
        return sb.toString();
    }

    // Classe interne pour le résultat de prédiction
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class PredictionResult {
        private Double score;
        private QualityStatus status;
        private String details;
        private java.util.Map<String, Double> parameterScores;
        private List<String> recommendations;
    }
}
