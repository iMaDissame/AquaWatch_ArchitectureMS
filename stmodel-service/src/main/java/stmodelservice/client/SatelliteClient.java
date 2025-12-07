package stmodelservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import stmodelservice.web.dto.SatelliteMetricDTO;

import java.util.List;

/**
 * Feign Client pour communiquer avec satellite-service via Eureka
 */
@FeignClient(name = "satellite-service")
public interface SatelliteClient {

    @GetMapping("/api/satellite/metrics/station/{stationId}/latest")
    List<SatelliteMetricDTO> getLatestMetricsForStation(@PathVariable("stationId") Long stationId);

    @GetMapping("/api/satellite/metrics/station/{stationId}")
    List<SatelliteMetricDTO> getAllMetricsForStation(@PathVariable("stationId") Long stationId);
}
