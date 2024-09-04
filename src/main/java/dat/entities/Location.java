package dat.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter // Allows for updating fields if needed
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    // Eager loading is default. Lazy gives better performance, but may cause issues if not handled properly
    @OneToMany(mappedBy = "sourceLocation", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude // Prevent circular reference in toString()
    private List<Shipment> shipmentsAsSource;

    @OneToMany(mappedBy = "destinationLocation", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude // Prevent circular reference in toString()
    private List<Shipment> shipmentsAsDestination;

    // Timestamps for auditing
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Automatically populate timestamps
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
