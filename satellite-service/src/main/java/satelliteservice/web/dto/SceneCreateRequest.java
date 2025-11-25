package satelliteservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import satelliteservice.domain.enums.SatelliteType;

import java.time.LocalDateTime;


// Ce DTO sera utilisé par l’admin ou par un script Python qui enregistre une nouvelle scène.
@Data
public class SceneCreateRequest {

    @NotNull
    private SatelliteType satellite;

    @NotNull
    private LocalDateTime acquiredAt;

    @NotBlank
    private String productId;

    private String tileId;
    private String aoiName;
    private String boundingBoxWkt;
    private Double cloudCoverage;
    private String rawProductPath;
    private String waterMaskPath;
    private String probabilityMapPath;
}
