package ua.edu.nupp.kpp.green;

import ua.edu.nupp.kpp.green.dao.CrudDao;
import ua.edu.nupp.kpp.green.model.Identifiable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Тестова реалізація CrudDao у пам'яті: сервіси перевіряються без MySQL.
class InMemoryDao<T extends Identifiable> implements CrudDao<T> {
    private final Map<Integer, T> data = new LinkedHashMap<>();
    private int sequence = 0;

    @Override
    public List<T> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Optional<T> findById(int id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public T insert(T entity) {
        entity.setId(++sequence);
        data.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public void update(T entity) {
        if (!data.containsKey(entity.getId())) {
            throw new IllegalArgumentException("Запис не знайдено");
        }
        data.put(entity.getId(), entity);
    }

    @Override
    public void delete(int id) {
        if (data.remove(id) == null) {
            throw new IllegalArgumentException("Запис не знайдено");
        }
    }
}
