package sensorservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import sensorservice.domain.entity.SensorMeasurement;
import sensorservice.domain.entity.Station;
import sensorservice.domain.enums.MeasurementSource;
import sensorservice.repository.SensorMeasurementRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires simples pour SensorMeasurementService
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("SensorMeasurementService Tests")
class SensorMeasurementServiceTest {

    @Mock
    private SensorMeasurementRepository measurementRepository;

    @Mock
    private StationService stationService;

    @InjectMocks
    private SensorMeasurementService measurementService;

    private Station sampleStation;
    private SensorMeasurement sampleMeasurement;

    @BeforeEach
    void setUp() {
        sampleStation = new Station();
        sampleStation.setId(1L);
        sampleStation.setCode("ST001");
        sampleStation.setName("Station Test");

        sampleMeasurement = new SensorMeasurement();
        sampleMeasurement.setId(1L);
        sampleMeasurement.setStation(sampleStation);
        sampleMeasurement.setTimestamp(LocalDateTime.now());
        sampleMeasurement.setPh(7.0);
        sampleMeasurement.setTemperature(20.0);
        sampleMeasurement.setTurbidity(1.5);
        sampleMeasurement.setDissolvedOxygen(8.5);
        sampleMeasurement.setConductivity(350.0);
        sampleMeasurement.setSource(MeasurementSource.MANUAL_FORM);
    }

    @Test
    @DisplayName("Créer mesure manuelle")
    void shouldCreateManualMeasurement() {
        when(stationService.getById(1L)).thenReturn(sampleStation);
        when(measurementRepository.save(any(SensorMeasurement.class))).thenReturn(sampleMeasurement);

        SensorMeasurement result = measurementService.createManualMeasurement(
                1L, 7.2, 21.0, 2.0, 8.0, 400.0);

        assertThat(result).isNotNull();
        assertThat(result.getStation().getId()).isEqualTo(1L);
        verify(measurementRepository).save(any(SensorMeasurement.class));
    }

    @Test
    @DisplayName("Récupérer mesures par station et période")
    void shouldGetMeasurementsByStationAndTimeRange() {
        LocalDateTime start = LocalDateTime.now().minusHours(24);
        LocalDateTime end = LocalDateTime.now();

        when(stationService.getById(1L)).thenReturn(sampleStation);
        when(measurementRepository.findByStationAndTimestampBetween(sampleStation, start, end))
                .thenReturn(Arrays.asList(sampleMeasurement));

        List<SensorMeasurement> result = measurementService.getMeasurementsForStation(1L, start, end);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Récupérer dernière mesure d'une station")
    void shouldGetLatestMeasurement() {
        when(stationService.getById(1L)).thenReturn(sampleStation);
        when(measurementRepository.findFirstByStationOrderByTimestampDesc(sampleStation))
                .thenReturn(Optional.of(sampleMeasurement));

        SensorMeasurement result = measurementService.getLatestMeasurement(1L);

        assertThat(result).isNotNull();
        assertThat(result.getPh()).isEqualTo(7.0);
    }

    @Test
    @DisplayName("Retourner null si pas de mesure")
    void shouldReturnNullWhenNoMeasurement() {
        when(stationService.getById(1L)).thenReturn(sampleStation);
        when(measurementRepository.findFirstByStationOrderByTimestampDesc(sampleStation))
                .thenReturn(Optional.empty());

        SensorMeasurement result = measurementService.getLatestMeasurement(1L);

        assertThat(result).isNull();
    }
}
