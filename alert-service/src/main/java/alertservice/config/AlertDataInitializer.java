package alertservice.config;

import alertservice.domain.entity.Alert;
import alertservice.domain.enums.AlertSeverity;
import alertservice.domain.enums.AlertSourceType;
import alertservice.domain.enums.AlertStatus;
import alertservice.repository.AlertRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Initialise des alertes de test pour le développement
 * 
 * Types d'alertes:
 * - THRESHOLD_BREACH: Dépassement de seuil sur paramètres
 * - FORECAST_RISK: Risque prévu de dégradation
 * - TREND_ALERT: Tendance négative détectée
 * - SATELLITE_ANOMALY: Anomalie détectée par satellite
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AlertDataInitializer {

    private final AlertRepository alertRepository;

    @PostConstruct
    public void init() {
        if (alertRepository.count() > 0) {
            log.info("Base alertes déjà initialisée, skip...");
            return;
        }

        log.info("=== Initialisation des alertes de test ===");
        
        List<Alert> alerts = createAlerts();
        alertRepository.saveAll(alerts);
        log.info("{} alertes créées", alerts.size());
        
        log.info("=== Initialisation alertes terminée ===");
    }

    private List<Alert> createAlerts() {
        List<Alert> alerts = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Alerte 1: Turbidité élevée - Station Côtière Casablanca (Station 3)
        alerts.add(Alert.builder()
                .stationId(3L)
                .sourceId(1L)
                .createdAt(now.minusHours(2))
                .sourceType(AlertSourceType.OBSERVATION)
                .eventTime(now.minusHours(2))
                .severity(AlertSeverity.WARNING)
                .status(AlertStatus.OPEN)
                .type("THRESHOLD_BREACH")
                .title("Turbidité élevée détectée")
                .message("La turbidité a atteint 6.8 NTU, dépassant le seuil OMS de 5 NTU. " +
                        "Cause possible: ruissellement après précipitations. " +
                        "Recommandation: surveillance renforcée.")
                .score(58.5)
                .build());

        // Alerte 2: Oxygène dissous faible - Station Oued Sebou (Station 5)
        alerts.add(Alert.builder()
                .stationId(5L)
                .sourceId(2L)
                .createdAt(now.minusHours(6))
                .sourceType(AlertSourceType.OBSERVATION)
                .eventTime(now.minusHours(6))
                .severity(AlertSeverity.CRITICAL)
                .status(AlertStatus.OPEN)
                .type("THRESHOLD_BREACH")
                .title("Oxygène dissous critique")
                .message("L'oxygène dissous est tombé à 4.2 mg/L, sous le seuil critique de 5 mg/L. " +
                        "Risque d'hypoxie pour la faune aquatique. " +
                        "Cause possible: rejets industriels en amont. " +
                        "Action urgente requise.")
                .score(35.2)
                .build());

        // Alerte 3: Prévision de dégradation - Barrage (Station 2)
        alerts.add(Alert.builder()
                .stationId(2L)
                .sourceId(1L)
                .createdAt(now.minusHours(12))
                .sourceType(AlertSourceType.FORECAST)
                .eventTime(now.plusHours(24))
                .severity(AlertSeverity.INFO)
                .status(AlertStatus.ACKNOWLEDGED)
                .type("FORECAST_RISK")
                .title("Prévision de baisse de qualité")
                .message("Le modèle prévoit une baisse du score de qualité de 85 à 72 dans les 24h. " +
                        "Tendance saisonnière normale due aux températures estivales. " +
                        "Aucune action immédiate requise.")
                .score(72.0)
                .build());

        // Alerte 4: pH anormal - Puits Meknès (Station 4)
        alerts.add(Alert.builder()
                .stationId(4L)
                .sourceId(3L)
                .createdAt(now.minusDays(1))
                .sourceType(AlertSourceType.OBSERVATION)
                .eventTime(now.minusDays(1))
                .severity(AlertSeverity.WARNING)
                .status(AlertStatus.RESOLVED)
                .type("THRESHOLD_BREACH")
                .title("pH hors plage optimale")
                .message("pH mesuré à 8.7, légèrement au-dessus de la plage optimale (6.5-8.5). " +
                        "Cause probable: terrain calcaire. " +
                        "Impact limité sur la qualité globale.")
                .score(75.0)
                .build());

        // Alerte 5: Anomalie satellite - Station Bouregreg (Station 1)
        alerts.add(Alert.builder()
                .stationId(1L)
                .sourceId(4L)
                .createdAt(now.minusDays(2))
                .sourceType(AlertSourceType.OBSERVATION)
                .eventTime(now.minusDays(2))
                .severity(AlertSeverity.INFO)
                .status(AlertStatus.RESOLVED)
                .type("SATELLITE_ANOMALY")
                .title("Variation de couverture d'eau détectée")
                .message("Analyse Sentinel-2: baisse de 15% de la couverture d'eau dans la zone. " +
                        "Corrélation avec période de sécheresse. " +
                        "Niveau d'eau à surveiller.")
                .score(82.0)
                .build());

        // Alerte 6: Tendance négative - Lac Ifni (Station 6)
        alerts.add(Alert.builder()
                .stationId(6L)
                .sourceId(5L)
                .createdAt(now.minusHours(4))
                .sourceType(AlertSourceType.OBSERVATION)
                .eventTime(now.minusHours(4))
                .severity(AlertSeverity.INFO)
                .status(AlertStatus.OPEN)
                .type("TREND_ALERT")
                .title("Tendance de température à la hausse")
                .message("Température en hausse de 2°C sur les dernières 48h (21°C → 23°C). " +
                        "Dans les limites acceptables mais à surveiller. " +
                        "Qualité globale reste excellente.")
                .score(92.0)
                .build());

        // Alerte 7: Conductivité élevée - Station Côtière (Station 3)
        alerts.add(Alert.builder()
                .stationId(3L)
                .sourceId(6L)
                .createdAt(now.minusDays(3))
                .sourceType(AlertSourceType.OBSERVATION)
                .eventTime(now.minusDays(3))
                .severity(AlertSeverity.WARNING)
                .status(AlertStatus.ACKNOWLEDGED)
                .type("THRESHOLD_BREACH")
                .title("Conductivité élevée")
                .message("Conductivité mesurée à 520 µS/cm, au-dessus de la moyenne normale. " +
                        "Influence marine plus prononcée que d'habitude. " +
                        "Phénomène attendu en zone côtière.")
                .score(65.0)
                .build());

        return alerts;
    }
}
