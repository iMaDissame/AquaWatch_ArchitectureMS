package stmodelservice.domain.entity;
import stmodelservice.domain.enums.QualityStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "quality_forecasts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class QualityForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long stationId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime forecastTime;

    @Column(nullable = false)
    private Double predictedScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QualityStatus predictedStatus;

    private String modelName;
    private String modelVersion;
}
