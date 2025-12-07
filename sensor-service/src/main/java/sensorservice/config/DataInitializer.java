package sensorservice.config;

import sensorservice.domain.entity.SensorMeasurement;
import sensorservice.domain.entity.Station;
import sensorservice.domain.enums.MeasurementSource;
import sensorservice.repository.SensorMeasurementRepository;
import sensorservice.repository.StationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final StationRepository stationRepository;
    private final SensorMeasurementRepository measurementRepository;

    @PostConstruct
    public void init() {
        try {
            if (measurementRepository.count() > 0) {
                return; // déjà des données, on ne recharge pas
            }

            Map<String, Station> stationCache = new HashMap<>();

            var is = getClass().getResourceAsStream("/data/water_quality_samples.csv");
            if (is == null) return;

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
                            .timestamp(LocalDateTime.now()) // ou random dans une plage
                            .ph(ph)
                            .temperature(temp)
                            .turbidity(turb)
                            .dissolvedOxygen(dox)
                            .conductivity(cond)
                            .source(MeasurementSource.IOT_SIMULATED)
                            .build();

                    measurementRepository.save(m);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
