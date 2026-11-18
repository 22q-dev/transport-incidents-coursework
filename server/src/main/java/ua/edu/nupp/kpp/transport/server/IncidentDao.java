package ua.edu.nupp.kpp.transport.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IncidentDao {

    public List<Incident> findAll() throws SQLException {
        List<Incident> list = new ArrayList<>();
        String sql = "SELECT id, vehicle_id, description, affected_passengers, duration_score, " +
                     "state, f1, f2, f3, integral_index FROM incident ORDER BY id";
        try (Connection conn = DatabaseConnector.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Incident findById(int id) throws SQLException {
        String sql = "SELECT id, vehicle_id, description, affected_passengers, duration_score, " +
                     "state, f1, f2, f3, integral_index FROM incident WHERE id = ?";
        try (Connection conn = DatabaseConnector.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public void insert(Incident inc) throws SQLException {
        String sql = "INSERT INTO incident (vehicle_id, description, affected_passengers, " +
                     "duration_score, state, f1, f2, f3, integral_index) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, inc.getVehicleId());
            ps.setString(2, inc.getDescription());
            ps.setInt(3, inc.getAffectedPassengers());
            ps.setDouble(4, inc.getDurationScore());
            ps.setString(5, inc.getState().name());
            ps.setDouble(6, inc.getF1());
            ps.setDouble(7, inc.getF2());
            ps.setDouble(8, inc.getF3());
            ps.setDouble(9, inc.getIntegralIndex());
            ps.executeUpdate();
        }
    }

    public void update(Incident inc) throws SQLException {
        String sql = "UPDATE incident SET vehicle_id = ?, description = ?, affected_passengers = ?, " +
                     "duration_score = ?, state = ?, f1 = ?, f2 = ?, f3 = ?, integral_index = ? WHERE id = ?";
        try (Connection conn = DatabaseConnector.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, inc.getVehicleId());
            ps.setString(2, inc.getDescription());
            ps.setInt(3, inc.getAffectedPassengers());
            ps.setDouble(4, inc.getDurationScore());
            ps.setString(5, inc.getState().name());
            ps.setDouble(6, inc.getF1());
            ps.setDouble(7, inc.getF2());
            ps.setDouble(8, inc.getF3());
            ps.setDouble(9, inc.getIntegralIndex());
            ps.setInt(10, inc.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM incident WHERE id = ?";
        try (Connection conn = DatabaseConnector.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Incident mapRow(ResultSet rs) throws SQLException {
        return new Incident(
                rs.getInt("id"),
                rs.getInt("vehicle_id"),
                rs.getString("description"),
                rs.getInt("affected_passengers"),
                rs.getDouble("duration_score"),
                IncidentState.valueOf(rs.getString("state")),
                rs.getDouble("f1"),
                rs.getDouble("f2"),
                rs.getDouble("f3"),
                rs.getDouble("integral_index")
        );
    }
}
