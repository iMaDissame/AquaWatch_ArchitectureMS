package ma.projet.mapservice.client;

import ma.projet.mapservice.web.dto.QualityForecastDTO;
import ma.projet.mapservice.web.dto.QualityObservationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class StModelClient {

    private final RestTemplate restTemplate;

    @Value("${services.stmodel.base-url}")
    private String stmodelBaseUrl; // ex: http://stmodel-service:8083

    public QualityObservationDTO getLatestObservation(Long stationId) {
        String url = stmodelBaseUrl + "/api/quality/latest?stationId=" + stationId;
        return restTemplate.getForObject(url, QualityObservationDTO.class);
    }

    public QualityForecastDTO getSimpleForecast(Long stationId) {
        // si ton endpoint forecast est POST, tu peux l'ajuster;
        // ici on simplifie en supposant un GET (à adapter selon ton implémentation réelle).
        String url = stmodelBaseUrl + "/api/forecast?stationId=" + stationId + "&horizonHours=24";
        return restTemplate.getForObject(url, QualityForecastDTO.class);
    }
}
