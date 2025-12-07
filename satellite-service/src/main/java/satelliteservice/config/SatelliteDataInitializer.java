package satelliteservice.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import satelliteservice.domain.entity.SatelliteMetric;
import satelliteservice.domain.entity.SatelliteScene;
import satelliteservice.domain.enums.MetricType;
import satelliteservice.domain.enums.SatelliteType;
import satelliteservice.repository.SatelliteMetricRepository;
import satelliteservice.repository.SatelliteSceneRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Initialise des données satellite de test pour le développement
 * 
 * Basé sur les concepts du projet Sentinel_1-2_Water_Mapping:
 * - NDWI (Normalized Difference Water Index): -1 à +1, >0 = eau
 * - MNDWI (Modified NDWI): meilleur pour zones urbaines
 * - Turbidity Index: indice de turbidité satellite
 * - Water Coverage: pourcentage de couverture en eau
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SatelliteDataInitializer {

    private final SatelliteSceneRepository sceneRepository;
    private final SatelliteMetricRepository metricRepository;
    private final Random random = new Random(42);

    // IDs des stations (correspondant à sensor-service)
    private static final Long[] STATION_IDS = {1L, 2L, 3L, 4L, 5L, 6L};

    @PostConstruct
    public void init() {
        if (sceneRepository.count() > 0) {
            log.info("Base satellite déjà initialisée, skip...");
            return;
        }

        log.info("=== Initialisation des données satellite de test ===");
        
        // Créer des scènes satellite
        List<SatelliteScene> scenes = createScenes();
        sceneRepository.saveAll(scenes);
        log.info("{} scènes satellite créées", scenes.size());

        // Créer des métriques pour chaque station
        int totalMetrics = 0;
        for (SatelliteScene scene : scenes) {
            List<SatelliteMetric> metrics = createMetricsForScene(scene);
            metricRepository.saveAll(metrics);
            totalMetrics += metrics.size();
        }
        log.info("{} métriques satellite créées", totalMetrics);
        
        log.info("=== Initialisation satellite terminée ===");
    }

    private List<SatelliteScene> createScenes() {
        List<SatelliteScene> scenes = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Scène Sentinel-2 récente - Zone Rabat
        scenes.add(SatelliteScene.builder()
                .satellite(SatelliteType.SENTINEL_2)
                .acquiredAt(now.minusDays(1))
                .productId("S2B_MSIL2A_" + now.minusDays(1).toLocalDate() + "_N0509_R022_T29RNQ_20240101T000000")
                .tileId("T29RNQ")
                .aoiName("Bassin Bouregreg")
                .boundingBoxWkt("POLYGON((-7.0 33.8, -6.7 33.8, -6.7 34.1, -7.0 34.1, -7.0 33.8))")
                .cloudCoverage(12.5)
                .rawProductPath("/data/sentinel2/S2B_20240101_T29RNQ.SAFE")
                .waterMaskPath("/data/processed/water_mask_20240101_T29RNQ.tif")
                .probabilityMapPath("/data/processed/prob_map_20240101_T29RNQ.tif")
                .build());

        // Scène Sentinel-1 récente - Zone Rabat (SAR)
        scenes.add(SatelliteScene.builder()
                .satellite(SatelliteType.SENTINEL_1)
                .acquiredAt(now.minusDays(2))
                .productId("S1A_IW_GRDH_1SDV_" + now.minusDays(2).toLocalDate() + "_T000000_T000000")
                .tileId("T29RNQ")
                .aoiName("Bassin Bouregreg")
                .boundingBoxWkt("POLYGON((-7.0 33.8, -6.7 33.8, -6.7 34.1, -7.0 34.1, -7.0 33.8))")
                .cloudCoverage(0.0) // SAR pas affecté par nuages
                .rawProductPath("/data/sentinel1/S1A_20240102.SAFE")
                .waterMaskPath("/data/processed/water_mask_S1_20240102.tif")
                .build());

        // Scène Sentinel-2 - Zone Casablanca
        scenes.add(SatelliteScene.builder()
                .satellite(SatelliteType.SENTINEL_2)
                .acquiredAt(now.minusDays(3))
                .productId("S2A_MSIL2A_" + now.minusDays(3).toLocalDate() + "_N0509_R022_T29RNP_20240101T000000")
                .tileId("T29RNP")
                .aoiName("Zone Côtière Casablanca")
                .boundingBoxWkt("POLYGON((-7.7 33.4, -7.4 33.4, -7.4 33.7, -7.7 33.7, -7.7 33.4))")
                .cloudCoverage(8.3)
                .rawProductPath("/data/sentinel2/S2A_20240103_T29RNP.SAFE")
                .waterMaskPath("/data/processed/water_mask_20240103_T29RNP.tif")
                .build());

        // Scène Sentinel-2 - Zone Meknès/Fès
        scenes.add(SatelliteScene.builder()
                .satellite(SatelliteType.SENTINEL_2)
                .acquiredAt(now.minusDays(4))
                .productId("S2B_MSIL2A_" + now.minusDays(4).toLocalDate() + "_N0509_R065_T29SMD_20240101T000000")
                .tileId("T29SMD")
                .aoiName("Bassin Sebou")
                .boundingBoxWkt("POLYGON((-5.8 33.7, -5.3 33.7, -5.3 34.4, -5.8 34.4, -5.8 33.7))")
                .cloudCoverage(22.1)
                .rawProductPath("/data/sentinel2/S2B_20240104_T29SMD.SAFE")
                .waterMaskPath("/data/processed/water_mask_20240104_T29SMD.tif")
                .build());

        return scenes;
    }

    private List<SatelliteMetric> createMetricsForScene(SatelliteScene scene) {
        List<SatelliteMetric> metrics = new ArrayList<>();
        LocalDateTime createdAt = LocalDateTime.now();

        // Créer des métriques pour chaque station
        for (Long stationId : STATION_IDS) {
            // NDWI Mean
            metrics.add(SatelliteMetric.builder()
                    .stationId(stationId)
                    .scene(scene)
                    .metricType(MetricType.NDWI_MEAN)
                    .unit("index")
                    .value(generateNdwi(stationId))
                    .createdAt(createdAt)
                    .build());

            // MNDWI Mean
            metrics.add(SatelliteMetric.builder()
                    .stationId(stationId)
                    .scene(scene)
                    .metricType(MetricType.MNDWI_MEAN)
                    .unit("index")
                    .value(generateMndwi(stationId))
                    .createdAt(createdAt)
                    .build());

            // Water Coverage Percent
            metrics.add(SatelliteMetric.builder()
                    .stationId(stationId)
                    .scene(scene)
                    .metricType(MetricType.WATER_COVERAGE_PERCENT)
                    .unit("%")
                    .value(generateWaterCoverage(stationId))
                    .createdAt(createdAt)
                    .build());

            // Turbidity Index
            metrics.add(SatelliteMetric.builder()
                    .stationId(stationId)
                    .scene(scene)
                    .metricType(MetricType.TURBIDITY_INDEX)
                    .unit("index")
                    .value(generateTurbidityIndex(stationId))
                    .createdAt(createdAt)
                    .build());
        }

        return metrics;
    }

    /**
     * NDWI: Normalized Difference Water Index
     * Valeurs: -1 à +1, >0 indique présence d'eau
     * Eau pure: ~0.3 à 0.8
     */
    private double generateNdwi(Long stationId) {
        // Base selon type de station
        double base = switch (stationId.intValue()) {
            case 1, 5 -> 0.35;  // Rivières
            case 2 -> 0.55;     // Barrage (plus d'eau)
            case 3 -> 0.25;     // Côtier (mélange)
            case 4 -> 0.15;     // Puits (faible surface)
            case 6 -> 0.65;     // Lac
            default -> 0.30;
        };
        return round3(base + randomVariation(0.1));
    }

    /**
     * MNDWI: Modified NDWI - meilleur pour zones urbaines
     */
    private double generateMndwi(Long stationId) {
        double base = switch (stationId.intValue()) {
            case 1, 5 -> 0.40;
            case 2 -> 0.60;
            case 3 -> 0.30;
            case 4 -> 0.20;
            case 6 -> 0.70;
            default -> 0.35;
        };
        return round3(base + randomVariation(0.12));
    }

    /**
     * Pourcentage de couverture en eau dans la zone
     */
    private double generateWaterCoverage(Long stationId) {
        double base = switch (stationId.intValue()) {
            case 1, 5 -> 25.0;  // Rivières: ~25%
            case 2 -> 85.0;     // Barrage: ~85%
            case 3 -> 45.0;     // Côtier
            case 4 -> 5.0;      // Puits: faible
            case 6 -> 90.0;     // Lac
            default -> 30.0;
        };
        return round2(Math.max(0, Math.min(100, base + randomVariation(10))));
    }

    /**
     * Index de turbidité dérivé des bandes satellite
     * Valeurs: 0-1, plus élevé = plus turbide
     */
    private double generateTurbidityIndex(Long stationId) {
        double base = switch (stationId.intValue()) {
            case 1 -> 0.25;     // Rivière urbaine
            case 2 -> 0.15;     // Barrage: plus clair
            case 3 -> 0.45;     // Côtier: plus turbide
            case 4 -> 0.10;     // Puits: clair
            case 5 -> 0.35;     // Rivière industrielle
            case 6 -> 0.08;     // Lac montagne: très clair
            default -> 0.20;
        };
        return round3(Math.max(0, Math.min(1, base + randomVariation(0.08))));
    }

    private double randomVariation(double maxVariation) {
        return (random.nextDouble() - 0.5) * 2 * maxVariation;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private double round3(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}
