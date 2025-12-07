package ma.projet.mapservice.client;

import ma.projet.mapservice.web.dto.AlertDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AlertClient {

    private final RestTemplate restTemplate;

    @Value("${services.alert.base-url}")
    private String alertBaseUrl; // ex: http://alert-service:8084

    public List<AlertDTO> getActiveAlerts() {
        String url = alertBaseUrl + "/api/alerts/active";
        ResponseEntity<List<AlertDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<AlertDTO>>() {}
        );
        return response.getBody();
    }

    public List<AlertDTO> getActiveAlertsForStation(Long stationId) {
        String url = alertBaseUrl + "/api/alerts?stationId=" + stationId + "&status=OPEN";
        ResponseEntity<List<AlertDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<AlertDTO>>() {}
        );
        return response.getBody();
    }
}
