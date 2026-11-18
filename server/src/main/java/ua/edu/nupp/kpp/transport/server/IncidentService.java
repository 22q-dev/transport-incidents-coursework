package ua.edu.nupp.kpp.transport.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class IncidentService {
    private static final Logger LOG = Logger.getLogger(IncidentService.class.getName());
    private final IncidentDao incidentDao = new IncidentDao();
    private final VehicleDao vehicleDao = new VehicleDao();
    private final RouteDao routeDao = new RouteDao();

    public static boolean isTransitionAllowed(IncidentState from, IncidentState to) {
        if (from == null || to == null) {
            return false;
        }
        return (from == IncidentState.REPORTED && to == IncidentState.CONFIRMED)
                || (from == IncidentState.CONFIRMED && to == IncidentState.RESPONSE)
                || (from == IncidentState.RESPONSE && to == IncidentState.CLOSED);
    }

    public static void validateIncidentInput(Incident inc) {
        if (inc == null) {
            throw new IllegalArgumentException("Дані інциденту відсутні");
        }
        if (inc.getVehicleId() <= 0) {
            throw new IllegalArgumentException("Необхідно вказати коректний ID транспортного засобу");
        }
        if (inc.getDescription() == null || inc.getDescription().isBlank()) {
            throw new IllegalArgumentException("Опис інциденту не може бути порожнім");
        }
        if (inc.getAffectedPassengers() < 0) {
            throw new IllegalArgumentException("Кількість пасажирів не може бути від'ємною");
        }
        IncidentImpactCalculator.validateNormalizedFactor("F3 (тривалість порушення руху)", inc.getDurationScore());
    }

    public List<Incident> getAll() throws SQLException {
        return incidentDao.findAll();
    }

    public void create(Incident inc) throws SQLException {
        validateIncidentInput(inc);
        populateCalculatedMetrics(inc);
        inc.setState(IncidentState.REPORTED);
        incidentDao.insert(inc);
        LOG.info("Зареєстровано інцидент для ТЗ ID=" + inc.getVehicleId() + ", I=" + inc.getIntegralIndex());
    }

    public void update(Incident inc) throws SQLException {
        if (inc.getId() <= 0) {
            throw new IllegalArgumentException("Некоректний ID інциденту");
        }
        Incident existing = incidentDao.findById(inc.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Інцидент з ID=" + inc.getId() + " не знайдено");
        }
        validateIncidentInput(inc);
        populateCalculatedMetrics(inc);
        inc.setState(existing.getState());
        incidentDao.update(inc);
        LOG.info("Оновлено інцидент ID=" + inc.getId() + ", новий I=" + inc.getIntegralIndex());
    }

    public void transitionState(int incidentId, IncidentState targetState) throws SQLException {
        Incident existing = incidentDao.findById(incidentId);
        if (existing == null) {
            throw new IllegalArgumentException("Інцидент з ID=" + incidentId + " не знайдено");
        }
        if (!isTransitionAllowed(existing.getState(), targetState)) {
            throw new IllegalArgumentException(
                    "Недопустимий перехід стану: " + existing.getState() + " -> " + targetState);
        }
        existing.setState(targetState);
        incidentDao.update(existing);
        LOG.info("Змінено стан інциденту ID=" + incidentId + " на " + targetState);
    }

    public void delete(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Некоректний ID інциденту");
        }
        incidentDao.delete(id);
        LOG.info("Видалено інцидент ID=" + id);
    }

    private void populateCalculatedMetrics(Incident inc) throws SQLException {
        Vehicle vehicle = vehicleDao.findById(inc.getVehicleId());
        if (vehicle == null) {
            throw new IllegalArgumentException("Транспортний засіб з ID=" + inc.getVehicleId() + " не існує");
        }
        Route route = routeDao.findById(vehicle.getRouteId());
        if (route == null) {
            throw new IllegalArgumentException("Маршрут для ТЗ не знайдено");
        }
        double f1 = IncidentImpactCalculator.calculatePassengerFactorF1(
                inc.getAffectedPassengers(), vehicle.getCapacity());
        double f2 = route.getImportanceScore();
        double f3 = inc.getDurationScore();
        double integral = IncidentImpactCalculator.calculateIntegralIndex(f1, f2, f3);

        inc.setF1(f1);
        inc.setF2(f2);
        inc.setF3(f3);
        inc.setIntegralIndex(integral);
    }
}
