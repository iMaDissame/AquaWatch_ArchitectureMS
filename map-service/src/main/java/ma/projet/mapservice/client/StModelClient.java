package ma.projet.mapservice.client;

import ma.projet.mapservice.web.dto.QualityForecastDTO;
import ma.projet.mapservice.web.dto.QualityObservationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign Client pour communiquer avec stmodel-service via Eureka
 */
@FeignClient(name = "stmodel-service")
public interface StModelClient {

    @GetMapping("/api/quality/latest")
    QualityObservationDTO getLatestObservation(@RequestParam("stationId") Long stationId);

    @GetMapping("/api/quality")
    List<QualityObservationDTO> getObservationsByStation(@RequestParam("stationId") Long stationId);

    @GetMapping("/api/forecast/latest")
    QualityForecastDTO getLatestForecast(@RequestParam("stationId") Long stationId);

    @GetMapping("/api/forecast")
    List<QualityForecastDTO> getForecastsByStation(@RequestParam("stationId") Long stationId);

    @GetMapping("/api/forecast/simple")
    QualityForecastDTO getSimpleForecast(@RequestParam("stationId") Long stationId);
}
