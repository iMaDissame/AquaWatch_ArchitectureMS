package ma.projet.mapservice.client;

import ma.projet.mapservice.web.dto.MeasurementDTO;
import ma.projet.mapservice.web.dto.StationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign Client pour communiquer avec sensor-service via Eureka
 */
@FeignClient(name = "sensor-service")
public interface SensorClient {

    @GetMapping("/api/stations")
    List<StationDTO> getAllStations();

    @GetMapping("/api/stations/{id}")
    StationDTO getStationById(@PathVariable("id") Long id);

    @GetMapping("/api/measurements/latest")
    MeasurementDTO getLatestMeasurement(@RequestParam("stationId") Long stationId);

    @GetMapping("/api/measurements")
    List<MeasurementDTO> getMeasurementsByStation(@RequestParam("stationId") Long stationId);
}
