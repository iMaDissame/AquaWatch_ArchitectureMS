package alertservice.domain.entity;


import alertservice.domain.enums.AlertSeverity;
import alertservice.domain.enums.AlertSourceType;
import alertservice.domain.enums.AlertStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long stationId;

    // id de l'observation ou du forecast côté STModel
    private Long sourceId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertSourceType sourceType;

    // moment du phénomène : temps de l'observation ou forecastTime
    @Column(nullable = false)
    private LocalDateTime eventTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertStatus status;

    // type logique d'alerte (texte simple)
    @Column(nullable = false)
    private String type;   // ex: "THRESHOLD_BREACH", "FORECAST_RISK"

    private String title;

    @Column(length = 2000)
    private String message;

    // score de qualité lié à l'alerte (obs ou forecast)
    private Double score;

    private LocalDateTime resolvedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
