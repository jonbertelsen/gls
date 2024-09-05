package dat.persistence;

public interface iDAO<T> {
    T create(T t);
    T findById(Long id);
    default T findByTrackingNumber(String trackingNumber) {
        throw new UnsupportedOperationException("findByTrackingNumber is not supported.");
    }
    T update(T t);
    boolean delete(T t);
}
