package ua.edu.nupp.kpp.green.dao;

import ua.edu.nupp.kpp.green.model.Tree;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TreeDao implements CrudDao<Tree> {
    private static final String COLUMNS = "id, code, species, location, planting_year, pedestrians_per_hour";

    @Override
    public List<Tree> findAll() {
        String sql = "SELECT " + COLUMNS + " FROM tree ORDER BY id";
        try (Connection c = Db.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            List<Tree> result = new ArrayList<>();
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw Db.wrap(e);
        }
    }

    @Override
    public Optional<Tree> findById(int id) {
        String sql = "SELECT " + COLUMNS + " FROM tree WHERE id = ?";
        try (Connection c = Db.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, id);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw Db.wrap(e);
        }
    }

    @Override
    public Tree insert(Tree t) {
        String sql = "INSERT INTO tree(code, species, location, planting_year, pedestrians_per_hour) "
                + "VALUES(?, ?, ?, ?, ?)";
        try (Connection c = Db.getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, t.getCode());
            p.setString(2, t.getSpecies());
            p.setString(3, t.getLocation());
            p.setInt(4, t.getPlantingYear());
            p.setInt(5, t.getPedestriansPerHour());
            t.setId(Db.executeInsert(p));
            return t;
        } catch (SQLException e) {
            throw Db.wrap(e);
        }
    }

    @Override
    public void update(Tree t) {
        String sql = "UPDATE tree SET code = ?, species = ?, location = ?, planting_year = ?, "
                + "pedestrians_per_hour = ? WHERE id = ?";
        try (Connection c = Db.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, t.getCode());
            p.setString(2, t.getSpecies());
            p.setString(3, t.getLocation());
            p.setInt(4, t.getPlantingYear());
            p.setInt(5, t.getPedestriansPerHour());
            p.setInt(6, t.getId());
            Db.requireChanged(p.executeUpdate());
        } catch (SQLException e) {
            throw Db.wrap(e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection c = Db.getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM tree WHERE id = ?")) {
            p.setInt(1, id);
            Db.requireChanged(p.executeUpdate());
        } catch (SQLException e) {
            throw Db.wrap(e);
        }
    }

    private Tree map(ResultSet rs) throws SQLException {
        Tree t = new Tree();
        t.setId(rs.getInt("id"));
        t.setCode(rs.getString("code"));
        t.setSpecies(rs.getString("species"));
        t.setLocation(rs.getString("location"));
        t.setPlantingYear(rs.getInt("planting_year"));
        t.setPedestriansPerHour(rs.getInt("pedestrians_per_hour"));
        return t;
    }
}
