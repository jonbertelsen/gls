package dat.persistence;

import dat.entities.Location;
import dat.entities.Package;
import dat.entities.Shipment;
import dat.enums.HibernateConfigState;
import dat.exceptions.JpaException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

public class PackageDAO implements iDAO<Package> {

    private static PackageDAO instance;
    private static EntityManagerFactory emf;

    private PackageDAO() {
    }

    public static PackageDAO getInstance(HibernateConfigState state) {
        if (instance == null) {
            emf = HibernateConfig.getEntityManagerFactoryConfig(state, "gls");
            instance = new PackageDAO();
        }
        return instance;
    }

    public static EntityManagerFactory getEmf() {
        return emf;
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Override
    public Package create(Package newPackage) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();

            // Persist package
            if (newPackage.getId() == null) {
                em.persist(newPackage);
            } else {
                em.merge(newPackage);
            }

            // Persist each shipment and ensure relationship sync
            for (Shipment shipment : newPackage.getShipments()) {
                shipment.setRelatedPackage(newPackage);  // Synchronize relationship

                // Persist or merge locations
                if (shipment.getSourceLocation() != null && shipment.getDestinationLocation() != null) {
                    persistOrMergeLocation(shipment.getSourceLocation(), em);
                    persistOrMergeLocation(shipment.getDestinationLocation(), em);
                } else {
                    if (em != null && em.getTransaction().isActive()) {
                        em.getTransaction().rollback();  // Rollback transaction on failure
                    }
                    throw new JpaException("Error creating package: Shipment locations cannot be null");
                }
                persistOrMergeLocation(shipment.getSourceLocation(), em);
                persistOrMergeLocation(shipment.getDestinationLocation(), em);

                if (shipment.getId() == null) {
                    em.persist(shipment);
                } else {
                    em.merge(shipment);
                }
            }
            em.getTransaction().commit();
        }
        catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();  // Rollback transaction on failure
            }
            throw new JpaException("Error creating package: " + e.getMessage());
        }
        finally {
            if (em != null) {
                em.close();  // Always close the EntityManager
            }
        }
        return newPackage;
    }

    private void persistOrMergeLocation(Location location, EntityManager em) {
        if (location != null) {
            if (location.getId() == null) {
                em.persist(location);
            } else {
                em.merge(location);
            }
        }
    }

    @Override
    public Package findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Package.class, id);
        }
        catch (Exception e) {
            throw new JpaException("Error finding package: " + e.getMessage());
        }
    }

    @Override
    public Package findByTrackingNumber(String trackingNumber) {
        // use a typedquery
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Package> query = em.createQuery("SELECT p FROM Package p WHERE p.trackingNumber = :trackingNumber", Package.class);
            query.setParameter("trackingNumber", trackingNumber);
            query.setMaxResults(1);
            return query.getSingleResult();
        }
        catch (Exception e) {
            throw new JpaException("Error finding package by tracking number: " + e.getMessage());
        }
    }

    @Override
    public Package update(Package aPackage) {
        Package updatedPackage = null;
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            updatedPackage = em.merge(aPackage);
            em.getTransaction().commit();
        }
        catch (Exception e) {
            throw new JpaException("Error updating package: " + e.getMessage());
        }
        return updatedPackage;
    }

    @Override
    public boolean delete(Package aPackage) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            for (Shipment shipment : aPackage.getShipments()) {
                em.remove(shipment);
            }
            for (Shipment shipment : aPackage.getShipments()) {
                shipment.getSourceLocation().getShipmentsAsSource().remove(shipment);
                shipment.getDestinationLocation().getShipmentsAsDestination().remove(shipment);
                em.remove(shipment);
            }
            em.remove(aPackage);
            em.getTransaction().commit();
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
}

