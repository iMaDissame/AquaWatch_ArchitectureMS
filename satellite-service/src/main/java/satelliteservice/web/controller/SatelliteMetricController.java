package satelliteservice.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import satelliteservice.domain.entity.SatelliteMetric;
import satelliteservice.service.SatelliteMetricService;
import satelliteservice.web.dto.MetricCreateRequest;
import satelliteservice.web.dto.StationMetricSummaryDto;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/satellite/metrics")
@RequiredArgsConstructor
public class SatelliteMetricController {

    private final SatelliteMetricService metricService;

    // appelé par le script Python Sentinel
    @PostMapping("/batch")
    @ResponseStatus(HttpStatus.CREATED)
    public List<StationMetricSummaryDto> createBatch(
            @Valid @RequestBody List<MetricCreateRequest> requests) {

        return requests.stream()
                .map(req -> {
                    SatelliteMetric m = metricService.createMetric(
                            req.getStationId(),
                            req.getSceneId(),
                            req.getMetricType(),
                            req.getValue(),
                            req.getUnit()
                    );
                    return StationMetricSummaryDto.builder()
                            .stationId(m.getStationId())
                            .metricType(m.getMetricType())
                            .value(m.getValue())
                            .unit(m.getUnit())
                            .createdAt(m.getCreatedAt())
                            .productId(m.getScene().getProductId())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // utilisé par STModel-service pour récupérer les dernières métriques
    @GetMapping("/station/{stationId}/latest")
    public List<StationMetricSummaryDto> getLatestForStation(@PathVariable Long stationId) {
        return metricService.getLatestMetricsForStation(stationId).stream()
                .map(m -> StationMetricSummaryDto.builder()
                        .stationId(m.getStationId())
                        .metricType(m.getMetricType())
                        .value(m.getValue())
                        .unit(m.getUnit())
                        .createdAt(m.getCreatedAt())
                        .productId(m.getScene().getProductId())
                        .build())
                .toList();
    }
}
