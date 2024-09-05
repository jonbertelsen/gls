package dat;

import dat.entities.Location;
import dat.entities.Package;
import dat.entities.Shipment;
import dat.enums.DeliveryStatus;
import dat.enums.HibernateConfigState;
import dat.persistence.PackageDAO;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello GLS 2");

        PackageDAO packageDAO = PackageDAO.getInstance(HibernateConfigState.NORMAL);

        // Instantiate a package
        Package glsPackage = Package.builder().trackingNumber("123456789").sender("John Doe").receiver("Jane Doe").deliveryStatus(DeliveryStatus.PENDING).createdDateTime(null).build();

        // Instantiate a location
        Location sourceLocation = Location.builder().latitude(40.7128).longitude(-74.0060).address("New York, NY, USA").build();
        Location destinationLocation = Location.builder().latitude(41.7121).longitude(-62.0070).address("Los Angeles, USA").build();

        // Instantiate a shipment
        Shipment shipment = Shipment.builder().relatedPackage(glsPackage).shipmentDateTime(LocalDateTime.now()).build();

        // Add source location to the shipment
        shipment.addSourceLocation(sourceLocation);

        // Add destination location to the shipment
        shipment.addDestinationLocation(destinationLocation);

        // Add the shipment to the package
        glsPackage.addShipment(shipment);

        packageDAO.create(glsPackage);


    }

}