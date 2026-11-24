package ua.edu.nupp.kpp.green.dao;

import ua.edu.nupp.kpp.green.model.WorkAssignment;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WorkAssignmentDao implements CrudDao<WorkAssignment> {
    private static final String COLUMNS = "id, request_id, crew_name, planned_date, note";

    @Override
    public List<WorkAssignment> findAll() {
        String sql = "SELECT " + COLUMNS + " FROM work_assignment ORDER BY id";
        try (Connection c = Db.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            List<WorkAssignment> result = new ArrayList<>();
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw Db.wrap(e);
        }
    }

    @Override
    public Optional<WorkAssignment> findById(int id) {
        String sql = "SELECT " + COLUMNS + " FROM work_assignment WHERE id = ?";
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
    public WorkAssignment insert(WorkAssignment a) {
        String sql = "INSERT INTO work_assignment(request_id, crew_name, planned_date, note) VALUES(?, ?, ?, ?)";
        try (Connection c = Db.getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(p, a);
            a.setId(Db.executeInsert(p));
            return a;
        } catch (SQLException e) {
            throw Db.wrap(e);
        }
    }

    @Override
    public void update(WorkAssignment a) {
        String sql = "UPDATE work_assignment SET request_id = ?, crew_name = ?, planned_date = ?, note = ? "
                + "WHERE id = ?";
        try (Connection c = Db.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, a);
            p.setInt(5, a.getId());
            Db.requireChanged(p.executeUpdate());
        } catch (SQLException e) {
            throw Db.wrap(e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection c = Db.getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM work_assignment WHERE id = ?")) {
            p.setInt(1, id);
            Db.requireChanged(p.executeUpdate());
        } catch (SQLException e) {
            throw Db.wrap(e);
        }
    }

    private void bind(PreparedStatement p, WorkAssignment a) throws SQLException {
        p.setInt(1, a.getRequestId());
        p.setString(2, a.getCrewName());
        p.setDate(3, Date.valueOf(a.getPlannedDate().trim()));
        p.setString(4, a.getNote());
    }

    private WorkAssignment map(ResultSet rs) throws SQLException {
        WorkAssignment a = new WorkAssignment();
        a.setId(rs.getInt("id"));
        a.setRequestId(rs.getInt("request_id"));
        a.setCrewName(rs.getString("crew_name"));
        a.setPlannedDate(rs.getDate("planned_date").toLocalDate().toString());
        a.setNote(rs.getString("note"));
        return a;
    }
}
