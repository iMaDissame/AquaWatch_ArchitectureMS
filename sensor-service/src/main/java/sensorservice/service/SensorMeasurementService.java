package sensorservice.service;

import sensorservice.domain.entity.SensorMeasurement;
import sensorservice.domain.entity.Station;
import sensorservice.domain.enums.MeasurementSource;
import sensorservice.repository.SensorMeasurementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SensorMeasurementService {

    private final SensorMeasurementRepository measurementRepository;
    private final StationService stationService;

    public SensorMeasurement createManualMeasurement(
            Long stationId,
            Double ph,
            Double temperature,
            Double turbidity,
            Double dissolvedOxygen,
            Double conductivity
    ) {
        Station station = stationService.getById(stationId);

        SensorMeasurement measurement = SensorMeasurement.builder()
                .station(station)
                .timestamp(LocalDateTime.now())
                .ph(ph)
                .temperature(temperature)
                .turbidity(turbidity)
                .dissolvedOxygen(dissolvedOxygen)
                .conductivity(conductivity)
                .source(MeasurementSource.MANUAL_FORM)
                .build();

        return measurementRepository.save(measurement);
    }

    public List<SensorMeasurement> getMeasurementsForStation(
            Long stationId,
            LocalDateTime from,
            LocalDateTime to
    ) {
        Station station = stationService.getById(stationId);
        return measurementRepository.findByStationAndTimestampBetween(station, from, to);
    }

    public SensorMeasurement getLatestMeasurement(Long stationId) {
        Station station = stationService.getById(stationId);
        return measurementRepository.findFirstByStationOrderByTimestampDesc(station)
                .orElse(null);
    }
}