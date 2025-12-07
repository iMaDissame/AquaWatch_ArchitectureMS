package sensorservice.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;          // ex: ST-001

    @Column(nullable = false)
    private String name;          // ex: "Station Oued Bouregreg"

    private String type;          // RIVER, WELL, COASTAL...

    private Double latitude;
    private Double longitude;

    private String commune;
    private String description;
}
