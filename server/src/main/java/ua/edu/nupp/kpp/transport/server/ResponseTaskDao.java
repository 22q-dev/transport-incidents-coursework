package ua.edu.nupp.kpp.transport.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResponseTaskDao {

    public List<ResponseTask> findAll() throws SQLException {
        List<ResponseTask> list = new ArrayList<>();
        String sql = "SELECT id, incident_id, assigned_unit, action_plan, deadline_date FROM response_task ORDER BY id";
        try (Connection conn = DatabaseConnector.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public ResponseTask findById(int id) throws SQLException {
        String sql = "SELECT id, incident_id, assigned_unit, action_plan, deadline_date FROM response_task WHERE id = ?";
        try (Connection conn = DatabaseConnector.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public void insert(ResponseTask task) throws SQLException {
        String sql = "INSERT INTO response_task (incident_id, assigned_unit, action_plan, deadline_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, task.getIncidentId());
            ps.setString(2, task.getAssignedUnit());
            ps.setString(3, task.getActionPlan());
            ps.setString(4, task.getDeadlineDate());
            ps.executeUpdate();
        }
    }

    public void update(ResponseTask task) throws SQLException {
        String sql = "UPDATE response_task SET incident_id = ?, assigned_unit = ?, action_plan = ?, deadline_date = ? WHERE id = ?";
        try (Connection conn = DatabaseConnector.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, task.getIncidentId());
            ps.setString(2, task.getAssignedUnit());
            ps.setString(3, task.getActionPlan());
            ps.setString(4, task.getDeadlineDate());
            ps.setInt(5, task.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM response_task WHERE id = ?";
        try (Connection conn = DatabaseConnector.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private ResponseTask mapRow(ResultSet rs) throws SQLException {
        return new ResponseTask(
                rs.getInt("id"),
                rs.getInt("incident_id"),
                rs.getString("assigned_unit"),
                rs.getString("action_plan"),
                rs.getString("deadline_date")
        );
    }
}
