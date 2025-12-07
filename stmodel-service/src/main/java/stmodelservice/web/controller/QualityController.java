package stmodelservice.web.controller;

import stmodelservice.domain.entity.QualityObservation;
import stmodelservice.service.QualityCalculationService;
import stmodelservice.web.dto.QualityObservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quality")
@RequiredArgsConstructor
@CrossOrigin
public class QualityController {

    private final QualityCalculationService qualityCalculationService;

    // force un recalcul (fusion capteurs + satellite)
    @PostMapping("/compute")
    public QualityObservationResponse compute(@RequestParam Long stationId) {
        QualityObservation obs = qualityCalculationService.computeCurrentQuality(stationId);
        return QualityObservationResponse.builder()
                .stationId(obs.getStationId())
                .timestamp(obs.getTimestamp())
                .score(obs.getScore())
                .status(obs.getStatus())
                .details(obs.getDetails())
                .build();
    }

    // retourne la dernière observation en base (sans recalcul obligatoire)
    @GetMapping("/latest")
    public QualityObservationResponse latest(@RequestParam Long stationId) {
        QualityObservation obs = qualityCalculationService.getLatestObservation(stationId);
        if (obs == null) {
            return null;
        }
        return QualityObservationResponse.builder()
                .stationId(obs.getStationId())
                .timestamp(obs.getTimestamp())
                .score(obs.getScore())
                .status(obs.getStatus())
                .details(obs.getDetails())
                .build();
    }
}
