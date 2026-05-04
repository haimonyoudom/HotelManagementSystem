package hotel.dao;
import java.util.List;
import java.sql.SQLException;

public interface IDao<T> {
    void add(T obj) throws SQLException;
    T getById(int id) throws SQLException;
    List<T> getAll() throws SQLException;
    void update(T obj) throws SQLException;
    void delete(int id) throws SQLException;
}