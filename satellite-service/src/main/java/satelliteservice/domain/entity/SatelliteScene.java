package satelliteservice.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import satelliteservice.domain.enums.SatelliteType;

import java.time.LocalDateTime;

@Entity
@Table(name = "satellite_scenes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SatelliteScene {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SatelliteType satellite;

    @Column(nullable = false)
    private LocalDateTime acquiredAt;

    @Column(nullable = false, unique = true)
    private String productId;        // ID du produit Sentinel (nom dossier SAFE)

    private String tileId;           // ex: T31RCK
    private String aoiName;          // nom de zone d'intérêt (Bassin X, etc.)

    @Column(length = 2000)
    private String boundingBoxWkt;   // POLYGON(...) en WKT

    private Double cloudCoverage;

    private String rawProductPath;       // chemin local/Cloud
    private String waterMaskPath;        // raster binaire eau/non-eau
    private String probabilityMapPath;   // raster probabilité d'eau (optionnel)
}
