package stmodelservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import stmodelservice.web.dto.SensorMeasurementDTO;

import java.util.List;

/**
 * Feign Client pour communiquer avec sensor-service via Eureka
 */
@FeignClient(name = "sensor-service")
public interface SensorClient {

    @GetMapping("/api/measurements/latest")
    SensorMeasurementDTO getLatestMeasurement(@RequestParam("stationId") Long stationId);

    @GetMapping("/api/measurements")
    List<SensorMeasurementDTO> getMeasurementsByStation(@RequestParam("stationId") Long stationId);
}
