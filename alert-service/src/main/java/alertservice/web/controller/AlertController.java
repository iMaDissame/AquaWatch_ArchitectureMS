package alertservice.web.controller;

import alertservice.domain.enums.AlertStatus;
import alertservice.service.AlertService;
import alertservice.web.dto.AlertResponse;
import alertservice.web.dto.QualityForecastDTO;
import alertservice.web.dto.QualityObservationDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    // -------- endpoints appelés par STModel-service --------

    @PostMapping("/from-observation")
    @ResponseStatus(HttpStatus.CREATED)
    public AlertResponse createFromObservation(
            @Valid @RequestBody QualityObservationDTO obs) {
        return alertService.createAlertFromObservation(obs);
    }

    @PostMapping("/from-forecast")
    @ResponseStatus(HttpStatus.CREATED)
    public AlertResponse createFromForecast(
            @Valid @RequestBody QualityForecastDTO forecast) {
        return alertService.createAlertFromForecast(forecast);
    }

    // -------- endpoints pour le front / dashboard --------

    @GetMapping("/active")
    public List<AlertResponse> getActive() {
        return alertService.getActiveAlerts();
    }

    @GetMapping("/station/{stationId}/active")
    public List<AlertResponse> getActiveForStation(@PathVariable Long stationId) {
        return alertService.getActiveAlertsForStation(stationId);
    }

    @GetMapping
    public List<AlertResponse> getForStation(
            @RequestParam Long stationId,
            @RequestParam(required = false) AlertStatus status
    ) {
        return alertService.getAlertsForStation(stationId, status);
    }

    @PatchMapping("/{id}/acknowledge")
    public AlertResponse acknowledge(@PathVariable Long id) {
        return alertService.acknowledge(id);
    }

    @PatchMapping("/{id}/resolve")
    public AlertResponse resolve(@PathVariable Long id) {
        return alertService.resolve(id);
    }
}