package satelliteservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import satelliteservice.domain.entity.SatelliteMetric;
import satelliteservice.domain.entity.SatelliteScene;
import satelliteservice.domain.enums.MetricType;
import satelliteservice.repository.SatelliteMetricRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SatelliteMetricService {

    private final SatelliteMetricRepository metricRepository;
    private final SatelliteSceneService sceneService;

    public SatelliteMetric createMetric(Long stationId,
                                        Long sceneId,
                                        MetricType type,
                                        Double value,
                                        String unit) {

        SatelliteScene scene = sceneService.getById(sceneId);

        SatelliteMetric metric = SatelliteMetric.builder()
                .stationId(stationId)
                .scene(scene)
                .metricType(type)
                .value(value)
                .unit(unit)
                .createdAt(LocalDateTime.now())
                .build();

        return metricRepository.save(metric);
    }

    public List<SatelliteMetric> getLatestMetricsForStation(Long stationId) {
        return metricRepository.findTop10ByStationIdOrderByCreatedAtDesc(stationId);
    }
}
