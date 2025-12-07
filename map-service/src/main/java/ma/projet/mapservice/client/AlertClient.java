package ma.projet.mapservice.client;

import ma.projet.mapservice.web.dto.AlertDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign Client pour communiquer avec alert-service via Eureka
 */
@FeignClient(name = "alert-service")
public interface AlertClient {

    @GetMapping("/api/alerts")
    List<AlertDTO> getAlerts(
            @RequestParam(value = "stationId", required = false) Long stationId,
            @RequestParam(value = "status", required = false) String status
    );

    @GetMapping("/api/alerts/active")
    List<AlertDTO> getActiveAlerts();

    @GetMapping("/api/alerts/station/{stationId}/active")
    List<AlertDTO> getActiveAlertsForStation(@org.springframework.web.bind.annotation.PathVariable("stationId") Long stationId);
}
