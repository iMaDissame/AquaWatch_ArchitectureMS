package sensorservice.domain.entity;

import sensorservice.domain.enums.MeasurementSource;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_measurements")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SensorMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // relation locale vers Station (dans ce microservice)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // --------- paramètres qualité de l’eau ---------
    @Column(nullable = false)
    private Double ph;

    @Column(nullable = false)
    private Double temperature;       // °C

    @Column(nullable = false)
    private Double turbidity;         // NTU

    @Column(name = "dissolved_oxygen", nullable = false)
    private Double dissolvedOxygen;   // mg/L

    @Column(nullable = false)
    private Double conductivity;      // µS/cm

    // --------- meta ---------
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeasurementSource source; // MANUAL_FORM / IOT_SIMULATED
}
