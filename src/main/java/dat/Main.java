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
        Package aPackage = Package.builder().trackingNumber("123456789").sender("John Doe").receiver("Jane Doe").deliveryStatus(DeliveryStatus.PENDING).createdDateTime(null).build();

        // Instantiate a location
        Location location = Location.builder().latitude(40.7128).longitude(-74.0060).address("New York, NY, USA").build();

        // Instantiate a shipment
        Shipment shipment = Shipment.builder().pkg(aPackage).sourceLocation(location).destinationLocation(location).shipmentDateTime(LocalDateTime.now()).build();

    }

}