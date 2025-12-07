package sensorservice.service;

import sensorservice.domain.entity.Station;
import sensorservice.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;

    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    public Station getById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Station not found: " + id));
    }

    public Station save(Station station) {
        return stationRepository.save(station);
    }
}