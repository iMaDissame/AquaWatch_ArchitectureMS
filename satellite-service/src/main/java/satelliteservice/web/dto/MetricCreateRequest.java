package satelliteservice.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import satelliteservice.domain.enums.MetricType;


//Créer des métriques pour une scène (MetricCreateRequest)
//
//On prévoit un endpoint batch : le script Python envoie plusieurs métriques d’un coup.


@Data
public class MetricCreateRequest {

    @NotNull
    private Long stationId;

    @NotNull
    private Long sceneId;

    @NotNull
    private MetricType metricType;

    @NotNull
    private Double value;

    private String unit;
}