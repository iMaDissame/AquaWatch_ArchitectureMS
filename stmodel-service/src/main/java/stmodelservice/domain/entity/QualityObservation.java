package stmodelservice.domain.entity;

import stmodelservice.domain.enums.QualityStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quality_observations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class QualityObservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long stationId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private Double score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QualityStatus status;

    @Column(length = 2000)
    private String details;
}
