package stmodelservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import stmodelservice.web.dto.AlertDTO;
import stmodelservice.web.dto.QualityObservationAlertDTO;

/**
 * Feign Client pour communiquer avec alert-service via Eureka
 */
@FeignClient(name = "alert-service")
public interface AlertClient {

    @PostMapping("/api/alerts/from-observation")
    AlertDTO createAlertFromObservation(@RequestBody QualityObservationAlertDTO observation);
}
