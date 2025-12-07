package sensorservice.web.controller;

import sensorservice.domain.entity.Station;
import sensorservice.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
@CrossOrigin // si tu as un front séparé
public class StationController {

    private final StationService stationService;

    @GetMapping
    public List<Station> getAll() {
        return stationService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Station create(@RequestBody Station station) {
        return stationService.save(station);
    }
}