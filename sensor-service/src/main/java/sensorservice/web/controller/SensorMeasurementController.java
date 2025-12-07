package sensorservice.web.controller;

import sensorservice.domain.entity.SensorMeasurement;
import sensorservice.service.SensorMeasurementService;
import sensorservice.web.dto.MeasurementCreateRequest;
import sensorservice.web.dto.MeasurementResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/measurements")
@RequiredArgsConstructor
public class SensorMeasurementController {

    private final SensorMeasurementService measurementService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MeasurementResponse create(@Valid @RequestBody MeasurementCreateRequest request) {
        SensorMeasurement m = measurementService.createManualMeasurement(
                request.getStationId(),
                request.getPh(),
                request.getTemperature(),
                request.getTurbidity(),
                request.getDissolvedOxygen(),
                request.getConductivity()
        );

        return MeasurementResponse.builder()
                .id(m.getId())
                .stationId(m.getStation().getId())
                .stationName(m.getStation().getName())
                .timestamp(m.getTimestamp())
                .ph(m.getPh())
                .temperature(m.getTemperature())
                .turbidity(m.getTurbidity())
                .dissolvedOxygen(m.getDissolvedOxygen())
                .conductivity(m.getConductivity())
                .build();
    }

    @GetMapping
    public List<SensorMeasurement> listByStationAndRange(
            @RequestParam Long stationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return measurementService.getMeasurementsForStation(stationId, from, to);
    }

    @GetMapping("/latest")
    public MeasurementResponse getLatest(@RequestParam Long stationId) {
        SensorMeasurement m = measurementService.getLatestMeasurement(stationId);
        if (m == null) {
            return null;
        }
        return MeasurementResponse.builder()
                .id(m.getId())
                .stationId(m.getStation().getId())
                .stationName(m.getStation().getName())
                .timestamp(m.getTimestamp())
                .ph(m.getPh())
                .temperature(m.getTemperature())
                .turbidity(m.getTurbidity())
                .dissolvedOxygen(m.getDissolvedOxygen())
                .conductivity(m.getConductivity())
                .build();
    }
}