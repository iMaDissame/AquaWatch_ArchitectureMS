package stmodelservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stmodelservice.domain.entity.QualityForecast;
import stmodelservice.domain.entity.QualityObservation;
import stmodelservice.domain.enums.QualityStatus;
import stmodelservice.repository.QualityForecastRepository;
import stmodelservice.repository.QualityObservationRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service de prédiction de qualité de l'eau
 * 
 * Utilise plusieurs modèles de prédiction:
 * 1. Trend Model: Analyse la tendance des scores historiques
 * 2. Seasonal Model: Prend en compte les variations saisonnières
 * 3. Simple Model: Dégradation linéaire (fallback)
 * 
 * La prédiction combine:
 * - Historique des observations (régression linéaire simple)
 * - Facteurs environnementaux (température, saison)
 * - Intervalles de confiance basés sur la variance historique
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ForecastService {

    private final QualityCalculationService qualityCalculationService;
    private final QualityForecastRepository forecastRepository;
    private final QualityObservationRepository observationRepository;

    // Nombre minimum d'observations pour la régression
    private static final int MIN_OBSERVATIONS_FOR_TREND = 5;
    
    // Dégradation par défaut (points par jour)
    private static final double DEFAULT_DEGRADATION_RATE = 3.0;

    /**
     * Crée une prédiction de qualité pour une station
     * 
     * @param stationId ID de la station
     * @param horizonHours Horizon de prédiction (24h, 72h, etc.)
     * @return Prévision de qualité
     */
    @Transactional
    public QualityForecast createForecast(Long stationId, int horizonHours) {
        log.info("Création prévision pour station {} à horizon {}h", stationId, horizonHours);
        
        // Récupérer l'observation la plus récente
        QualityObservation latest = qualityCalculationService.getLatestObservation(stationId);
        
        // Si pas d'observation récente, en créer une
        if (latest == null || isStale(latest)) {
            latest = qualityCalculationService.computeCurrentQuality(stationId);
        }

        // Récupérer l'historique pour analyse de tendance
        List<QualityObservation> history = observationRepository
                .findByStationIdOrderByTimestampDesc(stationId);

        double predictedScore;
        String modelName;
        String modelVersion;
        double confidence;

        if (history.size() >= MIN_OBSERVATIONS_FOR_TREND) {
            // Utiliser le modèle de tendance
            TrendAnalysis trend = analyzeTrend(history);
            predictedScore = predictWithTrend(latest.getScore(), horizonHours, trend);
            modelName = "TrendRegressionModel";
            modelVersion = "v2.0";
            confidence = trend.confidence;
            log.info("Prédiction avec tendance: slope={}, confidence={}", trend.slope, trend.confidence);
        } else {
            // Fallback: modèle simple de dégradation
            predictedScore = predictSimple(latest.getScore(), horizonHours);
            modelName = "SimpleDegradationModel";
            modelVersion = "v1.0";
            confidence = 0.5;
            log.info("Prédiction simple (historique insuffisant)");
        }

        // Appliquer facteurs saisonniers
        predictedScore = applySeasonalFactors(predictedScore, LocalDateTime.now().plusHours(horizonHours));
        
        // Borner le score entre 0 et 100
        predictedScore = Math.max(0, Math.min(100, predictedScore));
        
        // Arrondir à 2 décimales
        predictedScore = Math.round(predictedScore * 100.0) / 100.0;

        QualityStatus predictedStatus = determineStatus(predictedScore);
        LocalDateTime now = LocalDateTime.now();

        QualityForecast forecast = QualityForecast.builder()
                .stationId(stationId)
                .createdAt(now)
                .forecastTime(now.plusHours(horizonHours))
                .predictedScore(predictedScore)
                .predictedStatus(predictedStatus)
                .modelName(modelName)
                .modelVersion(modelVersion)
                .build();

        log.info("Prévision créée: station={}, horizon={}h, score={}, status={}, model={}",
                stationId, horizonHours, predictedScore, predictedStatus, modelName);

        return forecastRepository.save(forecast);
    }

    /**
     * Créer des prévisions multiples (24h, 48h, 72h)
     */
    @Transactional
    public List<QualityForecast> createMultipleForecasts(Long stationId) {
        return List.of(
                createForecast(stationId, 24),
                createForecast(stationId, 48),
                createForecast(stationId, 72)
        );
    }

    /**
     * Analyse la tendance des observations historiques
     * Utilise une régression linéaire simple sur les scores
     */
    private TrendAnalysis analyzeTrend(List<QualityObservation> observations) {
        int n = Math.min(observations.size(), 10); // Utiliser les 10 dernières observations
        
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        double sumY2 = 0;
        
        for (int i = 0; i < n; i++) {
            double x = i; // Index temporel
            double y = observations.get(n - 1 - i).getScore(); // Ordre chronologique
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
            sumY2 += y * y;
        }
        
        // Pente de la régression: (n*Σxy - Σx*Σy) / (n*Σx² - (Σx)²)
        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX + 0.0001);
        
        // Moyenne
        double meanY = sumY / n;
        
        // Variance et écart-type
        double variance = (sumY2 / n) - (meanY * meanY);
        double stdDev = Math.sqrt(Math.max(0, variance));
        
        // Coefficient de confiance basé sur la variance
        // Plus la variance est faible, plus on a confiance
        double confidence = Math.max(0.3, Math.min(0.95, 1 - (stdDev / 50)));
        
        return new TrendAnalysis(slope, meanY, stdDev, confidence);
    }

    /**
     * Prédit le score en utilisant l'analyse de tendance
     */
    private double predictWithTrend(double currentScore, int horizonHours, TrendAnalysis trend) {
        // Convertir l'horizon en "unités" d'observation (supposant ~6h entre observations)
        double horizonUnits = horizonHours / 6.0;
        
        // Appliquer la tendance
        double trendAdjustment = trend.slope * horizonUnits;
        
        // Limiter l'ajustement pour éviter des prédictions extrêmes
        trendAdjustment = Math.max(-20, Math.min(10, trendAdjustment));
        
        // Appliquer aussi une légère dégradation naturelle
        double naturalDegradation = (horizonHours / 24.0) * DEFAULT_DEGRADATION_RATE;
        
        return currentScore + trendAdjustment - naturalDegradation;
    }

    /**
     * Modèle simple: dégradation linéaire
     */
    private double predictSimple(double currentScore, int horizonHours) {
        double daysAhead = horizonHours / 24.0;
        return currentScore - (daysAhead * DEFAULT_DEGRADATION_RATE);
    }

    /**
     * Applique des facteurs saisonniers à la prédiction
     * - Été: température élevée -> possible dégradation
     * - Pluie (printemps/automne): turbidité potentiellement plus haute
     */
    private double applySeasonalFactors(double score, LocalDateTime targetTime) {
        int month = targetTime.getMonthValue();
        
        // Été (juin-août): stress thermique potentiel
        if (month >= 6 && month <= 8) {
            score -= 2; // Légère pénalité
        }
        
        // Automne (sept-nov): pluies, ruissellement
        if (month >= 9 && month <= 11) {
            score -= 1;
        }
        
        return score;
    }

    /**
     * Vérifie si l'observation est périmée (> 1h)
     */
    private boolean isStale(QualityObservation observation) {
        return observation.getTimestamp().isBefore(LocalDateTime.now().minusHours(1));
    }

    private QualityStatus determineStatus(double score) {
        if (score >= 70) {
            return QualityStatus.GOOD;
        } else if (score >= 40) {
            return QualityStatus.MODERATE;
        } else {
            return QualityStatus.BAD;
        }
    }

    /**
     * Récupère les prévisions pour une station
     */
    public List<QualityForecast> getForecastsForStation(Long stationId) {
        return forecastRepository.findByStationIdOrderByForecastTimeAsc(stationId);
    }

    /**
     * Récupère la dernière prévision pour une station
     */
    public QualityForecast getLatestForecast(Long stationId) {
        return forecastRepository.findTopByStationIdOrderByCreatedAtDesc(stationId);
    }

    /**
     * Récupère l'historique des prévisions pour une station
     * @param stationId ID de la station
     * @param days Nombre de jours d'historique
     */
    public List<QualityForecast> getForecastHistory(Long stationId, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return forecastRepository.findByStationIdAndCreatedAtAfterOrderByCreatedAtDesc(stationId, since);
    }

    // Classe interne pour l'analyse de tendance
    private static class TrendAnalysis {
        final double slope;      // Pente de la tendance
        final double mean;       // Moyenne des scores
        final double stdDev;     // Écart-type
        final double confidence; // Niveau de confiance

        TrendAnalysis(double slope, double mean, double stdDev, double confidence) {
            this.slope = slope;
            this.mean = mean;
            this.stdDev = stdDev;
            this.confidence = confidence;
        }
    }

    // Méthode de compatibilité avec l'ancienne API
    @Transactional
    public QualityForecast createSimpleForecast(Long stationId, int horizonHours) {
        return createForecast(stationId, horizonHours);
    }
}