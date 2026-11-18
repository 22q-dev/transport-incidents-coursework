package ua.edu.nupp.kpp.transport.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RouteDao {

    public List<Route> findAll() throws SQLException {
        List<Route> list = new ArrayList<>();
        String sql = "SELECT id, route_code, route_name, importance_score FROM route ORDER BY id";
        try (Connection conn = DatabaseConnector.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Route findById(int id) throws SQLException {
        String sql = "SELECT id, route_code, route_name, importance_score FROM route WHERE id = ?";
        try (Connection conn = DatabaseConnector.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public void insert(Route route) throws SQLException {
        String sql = "INSERT INTO route (route_code, route_name, importance_score) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, route.getRouteCode());
            ps.setString(2, route.getRouteName());
            ps.setDouble(3, route.getImportanceScore());
            ps.executeUpdate();
        }
    }

    public void update(Route route) throws SQLException {
        String sql = "UPDATE route SET route_code = ?, route_name = ?, importance_score = ? WHERE id = ?";
        try (Connection conn = DatabaseConnector.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, route.getRouteCode());
            ps.setString(2, route.getRouteName());
            ps.setDouble(3, route.getImportanceScore());
            ps.setInt(4, route.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM route WHERE id = ?";
        try (Connection conn = DatabaseConnector.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Route mapRow(ResultSet rs) throws SQLException {
        return new Route(
                rs.getInt("id"),
                rs.getString("route_code"),
                rs.getString("route_name"),
                rs.getDouble("importance_score")
        );
    }
}
