package satelliteservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import satelliteservice.domain.enums.MetricType;

import java.time.LocalDateTime;

@Entity
@Table(name = "satellite_metrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SatelliteMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // stationId côté AquaWatch (sensor-service)
    @Column(nullable = false)
    private Long stationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scene_id", nullable = false)
    private SatelliteScene scene;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetricType metricType;

    private String unit;         // %, -, index...

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
