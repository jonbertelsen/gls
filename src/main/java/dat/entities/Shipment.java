package dat.entities;

import jakarta.persistence.*;
import lombok.*;

import java.awt.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter // Added setter for easier updates, if needed
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "package_id", nullable = false)
    private Package relatedPackage; // Renamed to 'relatedPackage' for clarity

    @ManyToOne(optional = false)
    @JoinColumn(name = "source_location_id", nullable = false)
    private Location sourceLocation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destination_location_id", nullable = false)
    private Location destinationLocation;

    @Column(name = "shipment_date_time", nullable = false)
    private LocalDateTime shipmentDateTime;

    // Timestamp fields for auditing
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Auto populate timestamp fields before persisting or updating
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Add bidirectional relationship with Package

    public void addSourceLocation(Location location) {
        if (location != null) {
            this.sourceLocation = location;
            location.getShipmentsAsSource().add(this);
        }
    }

    public void addDestinationLocation(Location location) {
        if (location != null) {
            this.destinationLocation = location;
            location.getShipmentsAsDestination().add(this);
        }
    }

}
