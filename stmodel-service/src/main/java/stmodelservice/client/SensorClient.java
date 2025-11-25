package stmodelservice.client;

import stmodelservice.web.dto.SensorMeasurementDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class SensorClient {

    private final RestTemplate restTemplate;

    @Value("${services.sensor.base-url}")
    private String sensorBaseUrl; // ex: http://sensor-service:8081

    public SensorMeasurementDTO getLatestMeasurement(Long stationId) {
        String url = sensorBaseUrl + "/api/measurements/latest?stationId=" + stationId;
        return restTemplate.getForObject(url, SensorMeasurementDTO.class);
    }
}
