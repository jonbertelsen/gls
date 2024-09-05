package dat.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    // Timestamps for auditing
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Automatically populate timestamps
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /* Bidirectional one-to-many relationship with Shipment */

    // Eager loading is default. Lazy gives better performance, but may cause issues if not handled properly
    @OneToMany(mappedBy = "sourceLocation")
    @ToString.Exclude // Prevent circular reference in toString()
    @Builder.Default
    private Set<Shipment> shipmentsAsSource = new HashSet<>();

    @OneToMany(mappedBy = "destinationLocation")
    @ToString.Exclude // Prevent circular reference in toString()
    @Builder.Default
    private Set<Shipment> shipmentsAsDestination = new HashSet<>();

    public void addShipmentAsSource(Shipment shipment) {
        if (shipment != null) {
            shipmentsAsSource.add(shipment);
            shipment.setSourceLocation(this);
        }
    }

    public void addShipmentAsDestination(Shipment shipment) {
        if (shipment != null) {
            shipmentsAsDestination.add(shipment);
            shipment.setDestinationLocation(this);
        }
    }

    public void removeShipmentAsSource(Shipment shipment) {
        if (shipment != null) {
            shipmentsAsSource.remove(shipment);
            shipment.setSourceLocation(null);
        }
    }

    public void removeShipmentAsDestination(Shipment shipment) {
        if (shipment != null) {
            shipmentsAsDestination.remove(shipment);
            shipment.setDestinationLocation(null);
        }
    }

}
