package ua.edu.nupp.kpp.transport.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ResponseTaskService {
    private static final Logger LOG = Logger.getLogger(ResponseTaskService.class.getName());
    private final ResponseTaskDao taskDao = new ResponseTaskDao();
    private final IncidentDao incidentDao = new IncidentDao();

    public static void validateTask(ResponseTask task) {
        if (task == null) {
            throw new IllegalArgumentException("Завдання реагування не задано");
        }
        if (task.getIncidentId() <= 0) {
            throw new IllegalArgumentException("Необхідно вказати коректний ID інциденту");
        }
        if (task.getAssignedUnit() == null || task.getAssignedUnit().isBlank()) {
            throw new IllegalArgumentException("Відповідальний підрозділ не може бути порожнім");
        }
        if (task.getActionPlan() == null || task.getActionPlan().isBlank()) {
            throw new IllegalArgumentException("Зміст завдання не може бути порожнім");
        }
        if (task.getDeadlineDate() == null || task.getDeadlineDate().isBlank()) {
            throw new IllegalArgumentException("Планова дата виконання не може бути порожньою");
        }
    }

    public List<ResponseTask> getAll() throws SQLException {
        return taskDao.findAll();
    }

    public void create(ResponseTask task) throws SQLException {
        validateTask(task);
        if (incidentDao.findById(task.getIncidentId()) == null) {
            throw new IllegalArgumentException("Інцидент з ID=" + task.getIncidentId() + " не існує");
        }
        taskDao.insert(task);
        LOG.info("Створено завдання реагування для інциденту ID=" + task.getIncidentId());
    }

    public void update(ResponseTask task) throws SQLException {
        if (task.getId() <= 0) {
            throw new IllegalArgumentException("Некоректний ID завдання реагування");
        }
        validateTask(task);
        if (incidentDao.findById(task.getIncidentId()) == null) {
            throw new IllegalArgumentException("Інцидент з ID=" + task.getIncidentId() + " не існує");
        }
        taskDao.update(task);
        LOG.info("Оновлено завдання реагування ID=" + task.getId());
    }

    public void delete(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Некоректний ID завдання реагування");
        }
        taskDao.delete(id);
        LOG.info("Видалено завдання реагування ID=" + id);
    }
}
