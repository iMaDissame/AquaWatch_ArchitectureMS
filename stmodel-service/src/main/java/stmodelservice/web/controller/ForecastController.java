package stmodelservice.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import stmodelservice.domain.entity.QualityForecast;
import stmodelservice.service.ForecastService;
import stmodelservice.web.dto.QualityForecastResponse;

import java.util.List;

@RestController
@RequestMapping("/api/forecast")
@RequiredArgsConstructor
public class ForecastController {

    private final ForecastService forecastService;

    /**
     * Crée une nouvelle prévision pour une station
     */
    @PostMapping("/create")
    public QualityForecastResponse create(
            @RequestParam Long stationId,
            @RequestParam(defaultValue = "24") int horizonHours) {
        QualityForecast forecast = forecastService.createForecast(stationId, horizonHours);
        return toResponse(forecast);
    }

    /**
     * Récupère la dernière prévision pour une station
     */
    @GetMapping("/latest")
    public QualityForecastResponse latest(@RequestParam Long stationId) {
        QualityForecast forecast = forecastService.getLatestForecast(stationId);
        return forecast != null ? toResponse(forecast) : null;
    }

    /**
     * Récupère une prévision simple (24h) pour une station - utilisé par map-service
     */
    @GetMapping("/simple")
    public QualityForecastResponse simple(@RequestParam Long stationId) {
        QualityForecast forecast = forecastService.getLatestForecast(stationId);
        if (forecast == null) {
            // Créer une nouvelle prévision si aucune n'existe
            forecast = forecastService.createForecast(stationId, 24);
        }
        return toResponse(forecast);
    }

    /**
     * Récupère l'historique des prévisions pour une station
     */
    @GetMapping
    public List<QualityForecastResponse> history(@RequestParam Long stationId) {
        return forecastService.getForecastHistory(stationId, 30).stream()
                .map(this::toResponse)
                .toList();
    }

    private QualityForecastResponse toResponse(QualityForecast f) {
        return QualityForecastResponse.builder()
                .id(f.getId())
                .stationId(f.getStationId())
                .modelName(f.getModelName())
                .predictedScore(f.getPredictedScore())
                .predictedStatus(f.getPredictedStatus().name())
                .createdAt(f.getCreatedAt())
                .forecastTime(f.getForecastTime())
                .confidence(f.getConfidence())
                .lowerBound(f.getLowerBound())
                .upperBound(f.getUpperBound())
                .build();
    }
}
