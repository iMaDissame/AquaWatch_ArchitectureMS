package stmodelservice.web.controller;

import stmodelservice.client.AlertClient;
import stmodelservice.domain.entity.PredictionHistory;
import stmodelservice.domain.entity.QualityObservation;
import stmodelservice.repository.PredictionHistoryRepository;
import stmodelservice.service.QualityCalculationService;
import stmodelservice.web.dto.AlertDTO;
import stmodelservice.web.dto.PredictionHistoryDTO;
import stmodelservice.web.dto.PredictionRequest;
import stmodelservice.web.dto.PredictionResponse;
import stmodelservice.web.dto.QualityObservationAlertDTO;
import stmodelservice.web.dto.QualityObservationResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/quality")
@RequiredArgsConstructor
public class QualityController {

    private final QualityCalculationService qualityCalculationService;
    private final PredictionHistoryRepository predictionHistoryRepository;
    private final ObjectMapper objectMapper;
    private final AlertClient alertClient;

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

    // Prédiction de qualité à partir de mesures fournies directement
    @PostMapping("/predict")
    public PredictionResponse predict(@RequestBody PredictionRequest request) {
        QualityCalculationService.PredictionResult result = qualityCalculationService.predictQuality(
                request.getPh(),
                request.getTemperature(),
                request.getTurbidity(),
                request.getDissolvedOxygen(),
                request.getConductivity()
        );
        
        // Sauvegarder la prédiction dans l'historique si un stationId est fourni
        if (request.getStationId() != null) {
            savePredictionHistory(request, result);
            
            // Créer une alerte si la qualité est MODERATE ou BAD
            if (result.getStatus() != stmodelservice.domain.enums.QualityStatus.GOOD) {
                createAlertForPrediction(request.getStationId(), result);
            }
        }
        
        return PredictionResponse.builder()
                .stationId(request.getStationId())
                .score(result.getScore())
                .status(result.getStatus())
                .details(result.getDetails())
                .parameterScores(result.getParameterScores())
                .recommendations(result.getRecommendations())
                .build();
    }
    
    /**
     * Crée une alerte via le service d'alertes
     */
    private void createAlertForPrediction(Long stationId, QualityCalculationService.PredictionResult result) {
        try {
            QualityObservationAlertDTO observation = QualityObservationAlertDTO.builder()
                    .id(System.currentTimeMillis()) // ID unique basé sur le timestamp
                    .stationId(stationId)
                    .timestamp(LocalDateTime.now())
                    .score(result.getScore())
                    .status(result.getStatus().name()) // Convertir l'enum en String
                    .details(result.getDetails())
                    .build();
            
            AlertDTO alert = alertClient.createAlertFromObservation(observation);
            log.info("Alerte créée pour station {}: severity={}, message={}", 
                    stationId, alert.getSeverity(), alert.getMessage());
        } catch (Exception e) {
            log.error("Erreur lors de la création de l'alerte pour la station {}: {}", 
                    stationId, e.getMessage());
        }
    }

    /**
     * Récupère l'historique des prédictions pour une station
     */
    @GetMapping("/history/{stationId}")
    public List<PredictionHistoryDTO> getHistory(@PathVariable Long stationId) {
        List<PredictionHistory> history = predictionHistoryRepository
                .findTop20ByStationIdOrderByPredictionTimestampDesc(stationId);
        return history.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tout l'historique des prédictions (toutes stations)
     */
    @GetMapping("/history")
    public List<PredictionHistoryDTO> getAllHistory() {
        List<PredictionHistory> history = predictionHistoryRepository
                .findAllByOrderByPredictionTimestampDesc();
        return history.stream()
                .limit(50)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Sauvegarde une prédiction dans l'historique
     */
    private void savePredictionHistory(PredictionRequest request, QualityCalculationService.PredictionResult result) {
        try {
            String parameterScoresJson = objectMapper.writeValueAsString(result.getParameterScores());
            String recommendationsJson = objectMapper.writeValueAsString(result.getRecommendations());
            
            PredictionHistory history = PredictionHistory.builder()
                    .stationId(request.getStationId())
                    .predictionTimestamp(LocalDateTime.now())
                    .ph(request.getPh())
                    .temperature(request.getTemperature())
                    .turbidity(request.getTurbidity())
                    .dissolvedOxygen(request.getDissolvedOxygen())
                    .conductivity(request.getConductivity())
                    .score(result.getScore())
                    .status(result.getStatus())
                    .details(result.getDetails())
                    .parameterScoresJson(parameterScoresJson)
                    .recommendationsJson(recommendationsJson)
                    .build();
            
            predictionHistoryRepository.save(history);
            log.info("Prédiction sauvegardée pour station {}: score={}", request.getStationId(), result.getScore());
        } catch (JsonProcessingException e) {
            log.error("Erreur lors de la sérialisation de l'historique de prédiction", e);
        }
    }

    /**
     * Convertit une entité PredictionHistory en DTO
     */
    private PredictionHistoryDTO toDTO(PredictionHistory entity) {
        Map<String, Double> parameterScores = null;
        List<String> recommendations = null;
        
        try {
            if (entity.getParameterScoresJson() != null) {
                parameterScores = objectMapper.readValue(
                        entity.getParameterScoresJson(), 
                        new TypeReference<Map<String, Double>>() {});
            }
            if (entity.getRecommendationsJson() != null) {
                recommendations = objectMapper.readValue(
                        entity.getRecommendationsJson(), 
                        new TypeReference<List<String>>() {});
            }
        } catch (JsonProcessingException e) {
            log.error("Erreur lors de la désérialisation de l'historique", e);
        }
        
        return PredictionHistoryDTO.builder()
                .id(entity.getId())
                .stationId(entity.getStationId())
                .predictionTimestamp(entity.getPredictionTimestamp())
                .ph(entity.getPh())
                .temperature(entity.getTemperature())
                .turbidity(entity.getTurbidity())
                .dissolvedOxygen(entity.getDissolvedOxygen())
                .conductivity(entity.getConductivity())
                .score(entity.getScore())
                .status(entity.getStatus())
                .details(entity.getDetails())
                .parameterScores(parameterScores)
                .recommendations(recommendations)
                .build();
    }
}
