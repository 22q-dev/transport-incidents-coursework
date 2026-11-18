package ua.edu.nupp.kpp.transport.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VehicleDao {

    public List<Vehicle> findAll() throws SQLException {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT id, route_id, board_number, model_name, capacity FROM vehicle ORDER BY id";
        try (Connection conn = DatabaseConnector.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Vehicle findById(int id) throws SQLException {
        String sql = "SELECT id, route_id, board_number, model_name, capacity FROM vehicle WHERE id = ?";
        try (Connection conn = DatabaseConnector.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public void insert(Vehicle v) throws SQLException {
        String sql = "INSERT INTO vehicle (route_id, board_number, model_name, capacity) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, v.getRouteId());
            ps.setString(2, v.getBoardNumber());
            ps.setString(3, v.getModelName());
            ps.setInt(4, v.getCapacity());
            ps.executeUpdate();
        }
    }

    public void update(Vehicle v) throws SQLException {
        String sql = "UPDATE vehicle SET route_id = ?, board_number = ?, model_name = ?, capacity = ? WHERE id = ?";
        try (Connection conn = DatabaseConnector.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, v.getRouteId());
            ps.setString(2, v.getBoardNumber());
            ps.setString(3, v.getModelName());
            ps.setInt(4, v.getCapacity());
            ps.setInt(5, v.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM vehicle WHERE id = ?";
        try (Connection conn = DatabaseConnector.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Vehicle mapRow(ResultSet rs) throws SQLException {
        return new Vehicle(
                rs.getInt("id"),
                rs.getInt("route_id"),
                rs.getString("board_number"),
                rs.getString("model_name"),
                rs.getInt("capacity")
        );
    }
}
