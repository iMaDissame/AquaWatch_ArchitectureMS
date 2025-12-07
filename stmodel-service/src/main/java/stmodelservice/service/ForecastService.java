package stmodelservice.service;

import stmodelservice.domain.entity.QualityForecast;
import stmodelservice.domain.entity.QualityObservation;
import stmodelservice.domain.enums.QualityStatus;
import stmodelservice.repository.QualityForecastRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ForecastService {

    private final QualityCalculationService qualityCalculationService;
    private final QualityForecastRepository forecastRepository;

    @Transactional
    public QualityForecast createSimpleForecast(Long stationId, int horizonHours) {
        // assurer qu'on a une observation fraîche
        QualityObservation latest = qualityCalculationService.computeCurrentQuality(stationId);

        double baseScore = latest.getScore();
        // v1 : petite dérive négative avec l'horizon
        double predictedScore = baseScore - (horizonHours / 24.0) * 5.0; // -5 points par jour
        if (predictedScore < 0) predictedScore = 0;

        QualityStatus predictedStatus;
        if (predictedScore >= 80) {
            predictedStatus = QualityStatus.GOOD;
        } else if (predictedScore >= 50) {
            predictedStatus = QualityStatus.MODERATE;
        } else {
            predictedStatus = QualityStatus.BAD;
        }

        LocalDateTime now = LocalDateTime.now();

        QualityForecast forecast = QualityForecast.builder()
                .stationId(stationId)
                .createdAt(now)
                .forecastTime(now.plusHours(horizonHours))
                .predictedScore(predictedScore)
                .predictedStatus(predictedStatus)
                .modelName("SimpleTrendModel")
                .modelVersion("v1.0")
                .build();

        return forecastRepository.save(forecast);
    }
}