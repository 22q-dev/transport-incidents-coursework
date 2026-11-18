package ua.edu.nupp.kpp.transport.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class IncidentServlet extends HttpServlet {
    private final IncidentService service = new IncidentService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setupJson(resp);
        try {
            String action = req.getParameter("action");
            if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                service.delete(id);
                mapper.writeValue(resp.getWriter(), Map.of("status", "ok"));
            } else {
                mapper.writeValue(resp.getWriter(), service.getAll());
            }
        } catch (Exception e) {
            sendError(resp, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        setupJson(resp);
        try {
            String action = req.getParameter("action");
            if ("transition".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                IncidentState state = IncidentState.valueOf(req.getParameter("state"));
                service.transitionState(id, state);
            } else {
                Incident incident = new Incident();
                if (req.getParameter("id") != null && !req.getParameter("id").isBlank()) {
                    incident.setId(Integer.parseInt(req.getParameter("id")));
                }
                incident.setVehicleId(Integer.parseInt(req.getParameter("vehicleId")));
                incident.setDescription(req.getParameter("description"));
                incident.setAffectedPassengers(Integer.parseInt(req.getParameter("affectedPassengers")));
                incident.setDurationScore(Double.parseDouble(req.getParameter("durationScore")));

                if ("update".equals(action)) {
                    service.update(incident);
                } else {
                    service.create(incident);
                }
            }
            mapper.writeValue(resp.getWriter(), Map.of("status", "ok"));
        } catch (Exception e) {
            sendError(resp, e.getMessage());
        }
    }

    private void setupJson(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    private void sendError(HttpServletResponse resp, String msg) throws IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        mapper.writeValue(resp.getWriter(), Map.of("error", msg != null ? msg : "Помилка запиту"));
    }
}
