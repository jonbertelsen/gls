package dat.persistence;

import dat.entities.Location;
import dat.entities.Package;
import dat.enums.HibernateConfigState;
import dat.exceptions.JpaException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

public class LocationDAO implements iDAO<Location> {

    private static LocationDAO instance;
    private static EntityManagerFactory emf;

    private LocationDAO(){
    }

    public static LocationDAO getInstance(HibernateConfigState state){
        if(instance == null){
            emf = HibernateConfig.getEntityManagerFactoryConfig(state, "gls");
            instance = new LocationDAO();
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
    public Location create(Location aLocation) {
        return aLocation;
    }

    @Override
    public Location findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return  em.find(Location.class, id);
        } catch (Exception e) {
            throw new JpaException("Error finding package: " + e.getMessage());
        }
    }

    public Location findByAddress(String address) {
       // use a typedquery
        try (EntityManager em = emf.createEntityManager()){
            TypedQuery<Location> query = em.createQuery("SELECT l FROM Location l WHERE l.address = :address", Location.class);
            query.setParameter("address", address);
            query.setMaxResults(1);
            return query.getSingleResult();
        } catch (Exception e) {
            throw new JpaException("Error finding location by address: " + e.getMessage());
        }
    }

    @Override
    public Location update(Location aLocation) {
        Location updatedLocation = null;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            updatedLocation = em.merge(aLocation);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new JpaException("Error updating package: " + e.getMessage());
        }
        return updatedLocation;
    }

    @Override
    public boolean delete(Location aLocation) {
        return true;
    }
}

