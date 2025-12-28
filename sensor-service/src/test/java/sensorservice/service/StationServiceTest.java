package sensorservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sensorservice.domain.entity.Station;
import sensorservice.repository.StationRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires simples pour StationService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StationService Tests")
class StationServiceTest {

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private StationService stationService;

    private Station sampleStation;

    @BeforeEach
    void setUp() {
        sampleStation = new Station();
        sampleStation.setId(1L);
        sampleStation.setCode("ST001");
        sampleStation.setName("Station Test");
        sampleStation.setLatitude(33.5);
        sampleStation.setLongitude(-7.6);
    }

    @Test
    @DisplayName("findAll retourne toutes les stations")
    void shouldReturnAllStations() {
        when(stationRepository.findAll()).thenReturn(Arrays.asList(sampleStation));

        List<Station> result = stationService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("ST001");
    }

    @Test
    @DisplayName("findById retourne station si existe")
    void shouldReturnStationById() {
        when(stationRepository.findById(1L)).thenReturn(Optional.of(sampleStation));

        Optional<Station> result = stationService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Station Test");
    }

    @Test
    @DisplayName("findById retourne empty si n'existe pas")
    void shouldReturnEmptyWhenNotFound() {
        when(stationRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Station> result = stationService.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getById lève exception si n'existe pas")
    void shouldThrowExceptionWhenGetByIdNotFound() {
        when(stationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stationService.getById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Station not found");
    }

    @Test
    @DisplayName("save sauvegarde la station")
    void shouldSaveStation() {
        when(stationRepository.save(any(Station.class))).thenReturn(sampleStation);

        Station result = stationService.save(sampleStation);

        assertThat(result.getId()).isEqualTo(1L);
        verify(stationRepository).save(sampleStation);
    }

    @Test
    @DisplayName("delete supprime la station")
    void shouldDeleteStation() {
        doNothing().when(stationRepository).deleteById(1L);

        stationService.delete(1L);

        verify(stationRepository).deleteById(1L);
    }
}
