package ua.edu.nupp.kpp.green.dao;

import ua.edu.nupp.kpp.green.model.Inspection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InspectionDao implements CrudDao<Inspection> {
    private static final String COLUMNS = "id, tree_id, inspection_date, health_score, defects";

    @Override
    public List<Inspection> findAll() {
        String sql = "SELECT " + COLUMNS + " FROM inspection ORDER BY id";
        try (Connection c = Db.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            List<Inspection> result = new ArrayList<>();
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw Db.wrap(e);
        }
    }

    @Override
    public Optional<Inspection> findById(int id) {
        String sql = "SELECT " + COLUMNS + " FROM inspection WHERE id = ?";
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
    public Inspection insert(Inspection i) {
        String sql = "INSERT INTO inspection(tree_id, inspection_date, health_score, defects) VALUES(?, ?, ?, ?)";
        try (Connection c = Db.getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(p, i);
            i.setId(Db.executeInsert(p));
            return i;
        } catch (SQLException e) {
            throw Db.wrap(e);
        }
    }

    @Override
    public void update(Inspection i) {
        String sql = "UPDATE inspection SET tree_id = ?, inspection_date = ?, health_score = ?, defects = ? "
                + "WHERE id = ?";
        try (Connection c = Db.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, i);
            p.setInt(5, i.getId());
            Db.requireChanged(p.executeUpdate());
        } catch (SQLException e) {
            throw Db.wrap(e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection c = Db.getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM inspection WHERE id = ?")) {
            p.setInt(1, id);
            Db.requireChanged(p.executeUpdate());
        } catch (SQLException e) {
            throw Db.wrap(e);
        }
    }

    private void bind(PreparedStatement p, Inspection i) throws SQLException {
        p.setInt(1, i.getTreeId());
        p.setDate(2, Date.valueOf(i.getInspectionDate().trim()));
        p.setInt(3, i.getHealthScore());
        p.setString(4, i.getDefects());
    }

    private Inspection map(ResultSet rs) throws SQLException {
        Inspection i = new Inspection();
        i.setId(rs.getInt("id"));
        i.setTreeId(rs.getInt("tree_id"));
        i.setInspectionDate(rs.getDate("inspection_date").toLocalDate().toString());
        i.setHealthScore(rs.getInt("health_score"));
        i.setDefects(rs.getString("defects"));
        return i;
    }
}
