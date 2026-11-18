package ua.edu.nupp.kpp.transport.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class RouteServlet extends HttpServlet {
    private final RouteService service = new RouteService();
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
            Route route = new Route(
                    req.getParameter("id") != null && !req.getParameter("id").isBlank()
                            ? Integer.parseInt(req.getParameter("id")) : 0,
                    req.getParameter("routeCode"),
                    req.getParameter("routeName"),
                    Double.parseDouble(req.getParameter("importanceScore"))
            );
            if ("update".equals(action)) {
                service.update(route);
            } else {
                service.create(route);
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
