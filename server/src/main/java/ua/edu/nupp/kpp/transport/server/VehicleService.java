package ua.edu.nupp.kpp.transport.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class VehicleService {
    private static final Logger LOG = Logger.getLogger(VehicleService.class.getName());
    private final VehicleDao vehicleDao = new VehicleDao();
    private final RouteDao routeDao = new RouteDao();

    public static void validateVehicleFields(Vehicle v) {
        if (v == null) {
            throw new IllegalArgumentException("Транспортний засіб не задано");
        }
        if (v.getRouteId() <= 0) {
            throw new IllegalArgumentException("Необхідно вказати коректний ID маршруту");
        }
        if (v.getBoardNumber() == null || v.getBoardNumber().isBlank()) {
            throw new IllegalArgumentException("Бортовий номер не може бути порожнім");
        }
        if (v.getModelName() == null || v.getModelName().isBlank()) {
            throw new IllegalArgumentException("Модель транспортного засобу не може бути порожньою");
        }
        if (v.getCapacity() <= 0) {
            throw new IllegalArgumentException("Пасажиромісткість має бути більшою за 0");
        }
    }

    public List<Vehicle> getAll() throws SQLException {
        return vehicleDao.findAll();
    }

    public void create(Vehicle v) throws SQLException {
        validateVehicleFields(v);
        if (routeDao.findById(v.getRouteId()) == null) {
            throw new IllegalArgumentException("Маршрут з ID=" + v.getRouteId() + " не існує");
        }
        vehicleDao.insert(v);
        LOG.info("Додано транспортний засіб: " + v.getBoardNumber());
    }

    public void update(Vehicle v) throws SQLException {
        if (v.getId() <= 0) {
            throw new IllegalArgumentException("Некоректний ID транспортного засобу");
        }
        validateVehicleFields(v);
        if (routeDao.findById(v.getRouteId()) == null) {
            throw new IllegalArgumentException("Маршрут з ID=" + v.getRouteId() + " не існує");
        }
        vehicleDao.update(v);
        LOG.info("Оновлено транспортний засіб ID=" + v.getId());
    }

    public void delete(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Некоректний ID транспортного засобу");
        }
        vehicleDao.delete(id);
        LOG.info("Видалено транспортний засіб ID=" + id);
    }
}
