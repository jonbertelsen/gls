package dat;

import dat.entities.Location;
import dat.entities.Package;
import dat.entities.Shipment;
import dat.enums.DeliveryStatus;
import dat.enums.HibernateConfigState;
import dat.exceptions.JpaException;
import dat.persistence.LocationDAO;
import dat.persistence.PackageDAO;
import dat.persistence.ShipmentDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PackageDAOTest {

    private final static HibernateConfigState state = HibernateConfigState.TEST;
    private static final PackageDAO packageDAO = PackageDAO.getInstance(state);
    private static final LocationDAO locationDAO = LocationDAO.getInstance(state);
    private static final ShipmentDAO shipmentDAO = ShipmentDAO.getInstance(state);
    private static Package p1, p2, p3;
    private static Location l1, l2, l3;
    private static Shipment s1, s2, s3;

    @BeforeAll
    void setUpAll() {
    }

    @BeforeEach
    void setUp() {
        EntityManagerFactory emf = packageDAO.getEmf();

        // Reset table and sequence before each test
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Shipment").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE shipment_id_seq RESTART WITH 1").executeUpdate();
            em.createQuery("DELETE FROM Location").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE location_id_seq RESTART WITH 1").executeUpdate();
            em.createQuery("DELETE FROM Package").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE package_id_seq RESTART WITH 1").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new JpaException("Error deleting packages: " + e.getMessage());
        }

        // Create test data
        p1 = Package.builder()
                .trackingNumber("123456789")
                .sender("John Snow")
                .receiver("Daenerys Targaryen")
                .deliveryStatus(DeliveryStatus.PENDING)
                .build();

        p2 = Package.builder()
                .trackingNumber("987654321")
                .sender("Ayria Stark")
                .receiver("The Mountain")
                .deliveryStatus(DeliveryStatus.DELIVERED)
                .build();
        p3 = Package.builder()
                .trackingNumber("123456987")
                .sender("Cersei Lannister")
                .receiver("Tyrion Lannister")
                .deliveryStatus(DeliveryStatus.PENDING)
                .build();
        l1 = Location.builder()
                .latitude(40.7128)
                .longitude(-74.0060)
                .address("New York, NY, USA")
                .build();
        l2 = Location.builder()
                .latitude(34.0522)
                .longitude(-118.2437)
                .address("Los Angeles, CA, USA")
                .build();
        l3 = Location.builder()
                .latitude(41.8781)
                .longitude(-87.6298)
                .address("Chicago, IL, USA")
                .build();
        s1 = Shipment.builder()
                .shipmentDateTime(LocalDateTime.of(2021, 1, 1, 12, 0))
                .build();
        s2 = Shipment.builder()
                .shipmentDateTime(LocalDateTime.of(2021, 2, 1, 12, 0))
                .build();
        s3 = Shipment.builder()
                .shipmentDateTime(LocalDateTime.of(2021, 3, 1, 12, 0))
                .build();

        s1.addSourceLocation(l1);
        s1.addDestinationLocation(l2);
        p1.addShipment(s1);
        packageDAO.create(p1);
        s2.addSourceLocation(l3);
        s2.addDestinationLocation(l2);
    }

    @AfterAll
    static void tearDown() {
        packageDAO.close();
    }

    @Test
    void getInstance() {
        assertNotNull(packageDAO);
    }

    @Test
    void create() {
        Package p4 = Package.builder()
                            .trackingNumber("121231234")
                            .sender("Robb Stark")
                            .receiver("Sansa Stark")
                            .deliveryStatus(DeliveryStatus.PENDING)
                            .build();
        p4.addShipment(s2);
        packageDAO.create(p4);
        assertEquals(2, p4.getId());
    }

    @Test
    void findById() {
        Package actual = packageDAO.findById(p1.getId());
        assertEquals(p1, actual);
    }

    @Test
    void findByTrackingNumber() {
        Package actual = packageDAO.findByTrackingNumber(p1.getTrackingNumber());
        assertEquals(p1, actual);
    }

    @Test
    void update() {
        Package updated = Package.builder()
                .id(p1.getId())
                .trackingNumber(p1.getTrackingNumber())
                .sender(p1.getSender())
                .receiver(p1.getReceiver())
                .createdDateTime(p1.getCreatedDateTime())
                .updatedDateTime(p1.getUpdatedDateTime())
                .deliveryStatus(DeliveryStatus.DELIVERED)
                .build();
        assertNotEquals(p1.getDeliveryStatus(), updated.getDeliveryStatus());
        Package actual = packageDAO.create(updated);
        // This needs an equals method in the Package class to work
        assertEquals(updated, actual);
        Package actual2 = packageDAO.findById(p1.getId());
        assertEquals(DeliveryStatus.DELIVERED, actual2.getDeliveryStatus());
    }

    @Test
    void delete() {
        boolean actual = packageDAO.delete(p1);
        assertTrue(actual);
        actual = packageDAO.delete(p1);
        assertFalse(actual);
        Package deleted = packageDAO.findById(p1.getId());
        assertNull(deleted);
    }
}