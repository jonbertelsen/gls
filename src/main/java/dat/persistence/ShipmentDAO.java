package dat.persistence;

import dat.entities.Shipment;
import dat.enums.HibernateConfigState;
import dat.exceptions.JpaException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

public class ShipmentDAO implements iDAO<Shipment> {

    private static ShipmentDAO instance;
    private static EntityManagerFactory emf;

    private ShipmentDAO(){
    }

    public static ShipmentDAO getInstance(HibernateConfigState state){
        if(instance == null){
            emf = HibernateConfig.getEntityManagerFactoryConfig(state, "gls");
            instance = new ShipmentDAO();
        }
        return instance;
    }

    public static EntityManagerFactory getEmf(){
        return emf;
    }

    public static void close(){
        if(emf != null && emf.isOpen()){
            emf.close();
        }
    }

    @Override
    public Shipment create(Shipment aShipment) {
        return aShipment;
    }

    @Override
    public Shipment findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return  em.find(Shipment.class, id);
        } catch (Exception e) {
            throw new JpaException("Error finding shipment: " + e.getMessage());
        }
    }

    @Override
    public Shipment update(Shipment aShipment) {
        return aShipment;
    }

    @Override
    public boolean delete(Shipment aShipment) {
        return true;
    }
}

