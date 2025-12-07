package sensorservice.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sensorservice.domain.entity.SensorMeasurement;
import sensorservice.domain.entity.Station;
import sensorservice.domain.enums.MeasurementSource;
import sensorservice.repository.SensorMeasurementRepository;
import sensorservice.repository.StationRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Initialise des données de test pour le développement
 * 
 * Deux modes:
 * 1. Lecture du fichier CSV (si disponible)
 * 2. Génération automatique basée sur le dataset Water Quality Testing
 * 
 * Dataset de référence (500 samples):
 * - pH: 6.83 - 7.48 (moyenne ~7.0)
 * - Temperature: 20.3 - 23.6°C (moyenne ~22°C)
 * - Turbidity: 3.1 - 5.1 NTU (moyenne ~4.0)
 * - Dissolved Oxygen: 6.0 - 9.9 mg/L (moyenne ~8.0)
 * - Conductivity: 316 - 370 µS/cm (moyenne ~343)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final StationRepository stationRepository;
    private final SensorMeasurementRepository measurementRepository;
    private final Random random = new Random(42);

    // Paramètres basés sur le dataset Water Quality Testing
    private static final double PH_MIN = 6.83;
    private static final double PH_MAX = 7.48;
    private static final double TEMP_MIN = 20.3;
    private static final double TEMP_MAX = 23.6;
    private static final double TURBIDITY_MIN = 3.1;
    private static final double TURBIDITY_MAX = 5.1;
    private static final double DO_MIN = 6.0;
    private static final double DO_MAX = 9.9;
    private static final double COND_MIN = 316.0;
    private static final double COND_MAX = 370.0;

    @PostConstruct
    public void init() {
        if (measurementRepository.count() > 0) {
            log.info("Base de données déjà initialisée, skip...");
            return;
        }

        log.info("=== Initialisation des données de test ===");

        // Essayer de charger depuis CSV
        boolean csvLoaded = loadFromCsv();
        
        if (!csvLoaded) {
            // Fallback: générer des données automatiquement
            log.info("CSV non trouvé, génération automatique des données...");
            generateTestData();
        }
        
        log.info("=== Initialisation terminée ===");
    }

    /**
     * Charge les données depuis le fichier CSV
     */
    private boolean loadFromCsv() {
        try {
            var is = getClass().getResourceAsStream("/data/water_quality_samples.csv");
            if (is == null) {
                return false;
            }

            Map<String, Station> stationCache = new HashMap<>();
            int count = 0;

            try (var reader = new BufferedReader(new InputStreamReader(is))) {
                String line = reader.readLine(); // skip header
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length < 6) continue;

                    String code = parts[0].trim();
                    double ph = Double.parseDouble(parts[1]);
                    double temp = Double.parseDouble(parts[2]);
                    double turb = Double.parseDouble(parts[3]);
                    double dox = Double.parseDouble(parts[4]);
                    double cond = Double.parseDouble(parts[5]);

                    Station station = stationCache.computeIfAbsent(code, c -> {
                        Station st = new Station();
                        st.setCode(c);
                        st.setName("Station " + c);
                        return stationRepository.save(st);
                    });

                    SensorMeasurement m = SensorMeasurement.builder()
                            .station(station)
                            .timestamp(LocalDateTime.now().minusMinutes(random.nextInt(1440)))
                            .ph(ph)
                            .temperature(temp)
                            .turbidity(turb)
                            .dissolvedOxygen(dox)
                            .conductivity(cond)
                            .source(MeasurementSource.IOT_SIMULATED)
                            .build();

                    measurementRepository.save(m);
                    count++;
                }
            }

            log.info("{} mesures chargées depuis CSV", count);
            return true;

        } catch (Exception e) {
            log.warn("Erreur lors du chargement CSV: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Génère des données de test automatiquement
     */
    private void generateTestData() {
        // Créer les stations
        List<Station> stations = createStations();
        stationRepository.saveAll(stations);
        log.info("{} stations créées", stations.size());

        // Créer les mesures pour chaque station
        int totalMeasurements = 0;
        for (Station station : stations) {
            List<SensorMeasurement> measurements = createMeasurementsForStation(station);
            measurementRepository.saveAll(measurements);
            totalMeasurements += measurements.size();
        }
        log.info("{} mesures générées au total", totalMeasurements);
    }

    private List<Station> createStations() {
        List<Station> stations = new ArrayList<>();

        // Station 1: Rivière - Bonne qualité
        stations.add(Station.builder()
                .code("ST-001")
                .name("Station Oued Bouregreg")
                .type("RIVER")
                .latitude(33.9716)
                .longitude(-6.8498)
                .commune("Rabat")
                .description("Station de surveillance sur l'Oued Bouregreg, zone urbaine")
                .build());

        // Station 2: Barrage - Bonne qualité
        stations.add(Station.builder()
                .code("ST-002")
                .name("Barrage Sidi Mohammed Ben Abdellah")
                .type("RESERVOIR")
                .latitude(33.9500)
                .longitude(-6.7000)
                .commune("Rabat")
                .description("Réservoir d'eau potable principal de Rabat-Salé")
                .build());

        // Station 3: Côtière - Qualité modérée
        stations.add(Station.builder()
                .code("ST-003")
                .name("Station Côtière Casablanca")
                .type("COASTAL")
                .latitude(33.5731)
                .longitude(-7.5898)
                .commune("Casablanca")
                .description("Point de surveillance côtier, port de Casablanca")
                .build());

        // Station 4: Puits - Bonne qualité
        stations.add(Station.builder()
                .code("ST-004")
                .name("Puits Agricole Meknès")
                .type("WELL")
                .latitude(33.8935)
                .longitude(-5.5547)
                .commune("Meknès")
                .description("Puits d'irrigation zone agricole")
                .build());

        // Station 5: Rivière - Qualité variable
        stations.add(Station.builder()
                .code("ST-005")
                .name("Station Oued Sebou")
                .type("RIVER")
                .latitude(34.2604)
                .longitude(-6.5802)
                .commune("Kénitra")
                .description("Station sur l'Oued Sebou, zone industrielle")
                .build());

        // Station 6: Lac - Bonne qualité
        stations.add(Station.builder()
                .code("ST-006")
                .name("Lac Ifni")
                .type("LAKE")
                .latitude(31.0000)
                .longitude(-7.8500)
                .commune("Toubkal")
                .description("Lac de montagne, zone protégée")
                .build());

        return stations;
    }

    private List<SensorMeasurement> createMeasurementsForStation(Station station) {
        List<SensorMeasurement> measurements = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Créer 24 mesures (une par heure sur les dernières 24h)
        for (int i = 23; i >= 0; i--) {
            LocalDateTime timestamp = now.minusHours(i);
            double[] params = generateParameters(station);

            SensorMeasurement measurement = SensorMeasurement.builder()
                    .station(station)
                    .timestamp(timestamp)
                    .ph(params[0])
                    .temperature(params[1])
                    .turbidity(params[2])
                    .dissolvedOxygen(params[3])
                    .conductivity(params[4])
                    .source(MeasurementSource.IOT_SIMULATED)
                    .build();

            measurements.add(measurement);
        }

        return measurements;
    }

    /**
     * Génère des paramètres réalistes basés sur le type de station
     * @return [pH, température, turbidité, DO, conductivité]
     */
    private double[] generateParameters(Station station) {
        double qualityFactor = getQualityFactor(station);
        
        // pH: basé sur le dataset (6.83-7.48)
        double ph = randomInRange(PH_MIN, PH_MAX);
        if (qualityFactor < 0.7) {
            ph += randomInRange(-0.3, 0.3);
        }
        ph = clamp(ph, 5.5, 9.5);

        // Température
        double temp = randomInRange(TEMP_MIN, TEMP_MAX);
        temp += randomInRange(-1.5, 1.5);
        temp = clamp(temp, 15.0, 30.0);

        // Turbidité
        double turbidity = randomInRange(TURBIDITY_MIN, TURBIDITY_MAX);
        if (qualityFactor < 0.7) {
            turbidity += randomInRange(0, 3);
        }
        turbidity = clamp(turbidity, 0.5, 15.0);

        // Oxygène Dissous
        double dissolvedOxygen = randomInRange(DO_MIN, DO_MAX);
        if (qualityFactor < 0.7) {
            dissolvedOxygen -= randomInRange(0, 2);
        }
        dissolvedOxygen = clamp(dissolvedOxygen, 3.0, 12.0);

        // Conductivité
        double conductivity = randomInRange(COND_MIN, COND_MAX);
        if ("COASTAL".equals(station.getType())) {
            conductivity += randomInRange(50, 150);
        }
        if (qualityFactor < 0.7) {
            conductivity += randomInRange(30, 80);
        }
        conductivity = clamp(conductivity, 100.0, 800.0);

        return new double[]{
                round2(ph), round2(temp), round2(turbidity),
                round2(dissolvedOxygen), round2(conductivity)
        };
    }

    private double getQualityFactor(Station station) {
        if (station.getType() == null) return 0.8;
        
        return switch (station.getType()) {
            case "LAKE" -> 0.95;
            case "RESERVOIR" -> 0.90;
            case "WELL" -> 0.85;
            case "RIVER" -> 0.75;
            case "COASTAL" -> 0.65;
            default -> 0.80;
        };
    }

    private double randomInRange(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
