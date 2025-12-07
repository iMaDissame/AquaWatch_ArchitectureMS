package stmodelservice.client;

import stmodelservice.web.dto.SatelliteMetricDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SatelliteClient {

    private final RestTemplate restTemplate;

    @Value("${services.satellite.base-url}")
    private String satelliteBaseUrl; // ex: http://satellite-service:8082

    public List<SatelliteMetricDTO> getLatestMetricsForStation(Long stationId) {
        String url = satelliteBaseUrl + "/api/satellite/metrics/station/" + stationId + "/latest";

        ResponseEntity<List<SatelliteMetricDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SatelliteMetricDTO>>() {}
        );
        return response.getBody();
    }
}