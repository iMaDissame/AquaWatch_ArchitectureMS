package ma.projet.mapservice.web.controller;

import ma.projet.mapservice.client.AlertClient;
import ma.projet.mapservice.client.SensorClient;
import ma.projet.mapservice.client.StModelClient;
import ma.projet.mapservice.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapController {

    private final SensorClient sensorClient;
    private final StModelClient stModelClient;
    private final AlertClient alertClient;

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ----------- vue liste pour la carte / dashboard -----------
    @GetMapping("/stations")
    public List<StationOverviewDTO> getStationOverview() {

        List<StationDTO> stations = sensorClient.getAllStations();

        return stations.stream().map(st -> {
            MeasurementDTO latestMeas = sensorClient.getLatestMeasurement(st.getId());
            QualityObservationDTO latestObs = stModelClient.getLatestObservation(st.getId());
            List<AlertDTO> activeAlerts = alertClient.getActiveAlertsForStation(st.getId());

            boolean hasAlert = activeAlerts != null && !activeAlerts.isEmpty();
            String severity = hasAlert ? activeAlerts.get(0).getSeverity() : null;

            Double score = latestObs != null ? latestObs.getScore() : null;
            String status = latestObs != null ? latestObs.getStatus() : null;
            String lastMeasTime = (latestMeas != null && latestMeas.getTimestamp() != null)
                    ? latestMeas.getTimestamp().format(TIME_FMT)
                    : null;

            return StationOverviewDTO.builder()
                    .stationId(st.getId())
                    .code(st.getCode())
                    .name(st.getName())
                    .latitude(st.getLatitude())
                    .longitude(st.getLongitude())
                    .currentScore(score)
                    .currentStatus(status)
                    .hasActiveAlert(hasAlert)
                    .latestAlertSeverity(severity)
                    .lastMeasurementTime(lastMeasTime)
                    .build();
        }).toList();
    }

    // ----------- vue détail pour une station -----------
    @GetMapping("/stations/{stationId}")
    public StationDetailDTO getStationDetail(@PathVariable Long stationId) {

        // Récupérer la station directement par ID
        StationDTO st = sensorClient.getStationById(stationId);
        if (st == null) {
            throw new IllegalArgumentException("Station not found: " + stationId);
        }

        MeasurementDTO latestMeas = null;
        QualityObservationDTO latestObs = null;
        QualityForecastDTO latestForecast = null;
        List<AlertDTO> activeAlerts = null;

        try {
            latestMeas = sensorClient.getLatestMeasurement(stationId);
        } catch (Exception e) {
            // Pas de mesure pour cette station
        }

        try {
            latestObs = stModelClient.getLatestObservation(stationId);
        } catch (Exception e) {
            // Pas d'observation de qualité pour cette station
        }

        try {
            latestForecast = stModelClient.getSimpleForecast(stationId);
        } catch (Exception e) {
            // Pas de prévision pour cette station
        }

        try {
            activeAlerts = alertClient.getActiveAlertsForStation(stationId);
        } catch (Exception e) {
            // Pas d'alertes pour cette station
        }

        return StationDetailDTO.builder()
                .stationId(st.getId())
                .code(st.getCode())
                .name(st.getName())
                .latitude(st.getLatitude())
                .longitude(st.getLongitude())
                .commune(st.getCommune())
                .description(st.getDescription())
                .latestMeasurement(latestMeas)
                .latestObservation(latestObs)
                .latestForecast(latestForecast)
                .activeAlerts(activeAlerts != null ? activeAlerts : List.of())
                .build();
    }

    // ----------- toutes les alertes actives pour le dashboard -----------
    @GetMapping("/alerts/active")
    public List<AlertDTO> getActiveAlerts() {
        return alertClient.getActiveAlerts();
    }
}
