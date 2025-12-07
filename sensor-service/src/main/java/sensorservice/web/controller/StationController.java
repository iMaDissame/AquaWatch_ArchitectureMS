package sensorservice.web.controller;

import sensorservice.domain.entity.Station;
import sensorservice.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    @GetMapping
    public List<Station> getAll() {
        return stationService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Station> getById(@PathVariable Long id) {
        return stationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Station create(@RequestBody Station station) {
        return stationService.save(station);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Station> update(@PathVariable Long id, @RequestBody Station station) {
        return stationService.findById(id)
                .map(existing -> {
                    existing.setCode(station.getCode());
                    existing.setName(station.getName());
                    existing.setType(station.getType());
                    existing.setLatitude(station.getLatitude());
                    existing.setLongitude(station.getLongitude());
                    existing.setCommune(station.getCommune());
                    existing.setDescription(station.getDescription());
                    return ResponseEntity.ok(stationService.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return stationService.findById(id)
                .map(station -> {
                    stationService.delete(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}