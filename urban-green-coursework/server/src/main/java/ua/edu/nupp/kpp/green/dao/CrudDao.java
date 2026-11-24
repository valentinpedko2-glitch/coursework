package ua.edu.nupp.kpp.green.dao;

import ua.edu.nupp.kpp.green.model.Identifiable;

import java.util.List;
import java.util.Optional;

// Контракт доступу до даних. Сервіси залежать від інтерфейсу, а не від JDBC.
public interface CrudDao<T extends Identifiable> {

    List<T> findAll();

    Optional<T> findById(int id);

    // Зберігає запис і повертає його з присвоєним id.
    T insert(T entity);

    void update(T entity);

    void delete(int id);
}
