package satelliteservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import satelliteservice.domain.entity.SatelliteScene;
import satelliteservice.repository.SatelliteSceneRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SatelliteSceneService {

    private final SatelliteSceneRepository sceneRepository;

    public SatelliteScene create(SatelliteScene scene) {
        return sceneRepository.save(scene);
    }

    public SatelliteScene getById(Long id) {
        return sceneRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Scene not found: " + id));
    }

    public List<SatelliteScene> findAll() {
        return sceneRepository.findAll();
    }
}

