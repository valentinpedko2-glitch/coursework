package ua.edu.nupp.kpp.green.dao;

import ua.edu.nupp.kpp.green.model.RequestState;
import ua.edu.nupp.kpp.green.model.WorkRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WorkRequestDao implements CrudDao<WorkRequest> {
    private static final String COLUMNS = "id, tree_id, inspection_id, work_type, description, deadline_days, "
            + "state, f1, f2, f3, priority_index";

    @Override
    public List<WorkRequest> findAll() {
        String sql = "SELECT " + COLUMNS + " FROM work_request ORDER BY id";
        try (Connection c = Db.getConnection();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            List<WorkRequest> result = new ArrayList<>();
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw Db.wrap(e);
        }
    }

    @Override
    public Optional<WorkRequest> findById(int id) {
        String sql = "SELECT " + COLUMNS + " FROM work_request WHERE id = ?";
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
    public WorkRequest insert(WorkRequest r) {
        String sql = "INSERT INTO work_request(tree_id, inspection_id, work_type, description, deadline_days, "
                + "state, f1, f2, f3, priority_index) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = Db.getConnection();
             PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(p, r);
            r.setId(Db.executeInsert(p));
            return r;
        } catch (SQLException e) {
            throw Db.wrap(e);
        }
    }

    @Override
    public void update(WorkRequest r) {
        String sql = "UPDATE work_request SET tree_id = ?, inspection_id = ?, work_type = ?, description = ?, "
                + "deadline_days = ?, state = ?, f1 = ?, f2 = ?, f3 = ?, priority_index = ? WHERE id = ?";
        try (Connection c = Db.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, r);
            p.setInt(11, r.getId());
            Db.requireChanged(p.executeUpdate());
        } catch (SQLException e) {
            throw Db.wrap(e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection c = Db.getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM work_request WHERE id = ?")) {
            p.setInt(1, id);
            Db.requireChanged(p.executeUpdate());
        } catch (SQLException e) {
            throw Db.wrap(e);
        }
    }

    private void bind(PreparedStatement p, WorkRequest r) throws SQLException {
        p.setInt(1, r.getTreeId());
        p.setInt(2, r.getInspectionId());
        p.setString(3, r.getWorkType());
        p.setString(4, r.getDescription());
        p.setInt(5, r.getDeadlineDays());
        p.setString(6, r.getState().name());
        p.setDouble(7, r.getF1());
        p.setDouble(8, r.getF2());
        p.setDouble(9, r.getF3());
        p.setDouble(10, r.getPriorityIndex());
    }

    private WorkRequest map(ResultSet rs) throws SQLException {
        WorkRequest r = new WorkRequest();
        r.setId(rs.getInt("id"));
        r.setTreeId(rs.getInt("tree_id"));
        r.setInspectionId(rs.getInt("inspection_id"));
        r.setWorkType(rs.getString("work_type"));
        r.setDescription(rs.getString("description"));
        r.setDeadlineDays(rs.getInt("deadline_days"));
        r.setState(RequestState.valueOf(rs.getString("state")));
        r.setF1(rs.getDouble("f1"));
        r.setF2(rs.getDouble("f2"));
        r.setF3(rs.getDouble("f3"));
        r.setPriorityIndex(rs.getDouble("priority_index"));
        return r;
    }
}
