package ua.edu.nupp.kpp.transport.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class RouteService {
    private static final Logger LOG = Logger.getLogger(RouteService.class.getName());
    private final RouteDao routeDao = new RouteDao();

    public static void validateRoute(Route r) {
        if (r == null) {
            throw new IllegalArgumentException("Об'єкт маршруту не задано");
        }
        if (r.getRouteCode() == null || r.getRouteCode().isBlank()) {
            throw new IllegalArgumentException("Код маршруту не може бути порожнім");
        }
        if (r.getRouteName() == null || r.getRouteName().isBlank()) {
            throw new IllegalArgumentException("Назва маршруту не може бути порожньою");
        }
        IncidentImpactCalculator.validateNormalizedFactor("F2 (важливість маршруту)", r.getImportanceScore());
    }

    public List<Route> getAll() throws SQLException {
        return routeDao.findAll();
    }

    public void create(Route r) throws SQLException {
        validateRoute(r);
        routeDao.insert(r);
        LOG.info("Створено маршрут: " + r.getRouteCode());
    }

    public void update(Route r) throws SQLException {
        if (r.getId() <= 0) {
            throw new IllegalArgumentException("Некоректний ID маршруту");
        }
        validateRoute(r);
        routeDao.update(r);
        LOG.info("Оновлено маршрут ID=" + r.getId());
    }

    public void delete(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Некоректний ID маршруту");
        }
        routeDao.delete(id);
        LOG.info("Видалено маршрут ID=" + id);
    }
}
