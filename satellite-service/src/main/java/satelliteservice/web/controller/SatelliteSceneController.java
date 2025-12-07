package satelliteservice.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import satelliteservice.domain.entity.SatelliteScene;
import satelliteservice.service.SatelliteSceneService;
import satelliteservice.web.dto.SceneCreateRequest;

import java.util.List;

@RestController
@RequestMapping("/api/satellite/scenes")
@RequiredArgsConstructor
public class SatelliteSceneController {

    private final SatelliteSceneService sceneService;

    @GetMapping
    public List<SatelliteScene> list() {
        return sceneService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SatelliteScene create(@Valid @RequestBody SceneCreateRequest req) {
        SatelliteScene scene = SatelliteScene.builder()
                .satellite(req.getSatellite())
                .acquiredAt(req.getAcquiredAt())
                .productId(req.getProductId())
                .tileId(req.getTileId())
                .aoiName(req.getAoiName())
                .boundingBoxWkt(req.getBoundingBoxWkt())
                .cloudCoverage(req.getCloudCoverage())
                .rawProductPath(req.getRawProductPath())
                .waterMaskPath(req.getWaterMaskPath())
                .probabilityMapPath(req.getProbabilityMapPath())
                .build();

        return sceneService.create(scene);
    }
}
