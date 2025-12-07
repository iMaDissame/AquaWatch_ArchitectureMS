package ma.projet.mapservice.client;

import ma.projet.mapservice.web.dto.MeasurementDTO;
import ma.projet.mapservice.web.dto.StationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SensorClient {

    private final RestTemplate restTemplate;

    @Value("${services.sensor.base-url}")
    private String sensorBaseUrl; // ex: http://sensor-service:8081

    public List<StationDTO> getAllStations() {
        String url = sensorBaseUrl + "/api/stations";

        ResponseEntity<List<StationDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<StationDTO>>() {}
        );
        return response.getBody();
    }

    public MeasurementDTO getLatestMeasurement(Long stationId) {
        String url = sensorBaseUrl + "/api/measurements/latest?stationId=" + stationId;
        return restTemplate.getForObject(url, MeasurementDTO.class);
    }
}
