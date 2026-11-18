package ua.edu.nupp.kpp.transport.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransportApiClient {
    private static final String BASE_URL = "http://localhost:8080/transport-server";
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Route> getRoutes() throws IOException, InterruptedException {
        return Arrays.asList(mapper.readValue(sendGet("/routes"), Route[].class));
    }

    public void addRoute(Route r) throws IOException, InterruptedException {
        sendPost("/routes", Map.of(
                "action", "create",
                "routeCode", r.getRouteCode(),
                "routeName", r.getRouteName(),
                "importanceScore", String.valueOf(r.getImportanceScore())
        ));
    }

    public void updateRoute(Route r) throws IOException, InterruptedException {
        sendPost("/routes", Map.of(
                "action", "update",
                "id", String.valueOf(r.getId()),
                "routeCode", r.getRouteCode(),
                "routeName", r.getRouteName(),
                "importanceScore", String.valueOf(r.getImportanceScore())
        ));
    }

    public void deleteRoute(int id) throws IOException, InterruptedException {
        sendGet("/routes?action=delete&id=" + id);
    }

    public List<Vehicle> getVehicles() throws IOException, InterruptedException {
        return Arrays.asList(mapper.readValue(sendGet("/vehicles"), Vehicle[].class));
    }

    public void addVehicle(Vehicle v) throws IOException, InterruptedException {
        sendPost("/vehicles", Map.of(
                "action", "create",
                "routeId", String.valueOf(v.getRouteId()),
                "boardNumber", v.getBoardNumber(),
                "modelName", v.getModelName(),
                "capacity", String.valueOf(v.getCapacity())
        ));
    }

    public void updateVehicle(Vehicle v) throws IOException, InterruptedException {
        sendPost("/vehicles", Map.of(
                "action", "update",
                "id", String.valueOf(v.getId()),
                "routeId", String.valueOf(v.getRouteId()),
                "boardNumber", v.getBoardNumber(),
                "modelName", v.getModelName(),
                "capacity", String.valueOf(v.getCapacity())
        ));
    }

    public void deleteVehicle(int id) throws IOException, InterruptedException {
        sendGet("/vehicles?action=delete&id=" + id);
    }

    public List<Incident> getIncidents() throws IOException, InterruptedException {
        return Arrays.asList(mapper.readValue(sendGet("/incidents"), Incident[].class));
    }

    public void addIncident(Incident inc) throws IOException, InterruptedException {
        sendPost("/incidents", Map.of(
                "action", "create",
                "vehicleId", String.valueOf(inc.getVehicleId()),
                "description", inc.getDescription(),
                "affectedPassengers", String.valueOf(inc.getAffectedPassengers()),
                "durationScore", String.valueOf(inc.getDurationScore())
        ));
    }

    public void updateIncident(Incident inc) throws IOException, InterruptedException {
        sendPost("/incidents", Map.of(
                "action", "update",
                "id", String.valueOf(inc.getId()),
                "vehicleId", String.valueOf(inc.getVehicleId()),
                "description", inc.getDescription(),
                "affectedPassengers", String.valueOf(inc.getAffectedPassengers()),
                "durationScore", String.valueOf(inc.getDurationScore())
        ));
    }

    public void changeIncidentState(int id, IncidentState nextState) throws IOException, InterruptedException {
        sendPost("/incidents", Map.of(
                "action", "transition",
                "id", String.valueOf(id),
                "state", nextState.name()
        ));
    }

    public void deleteIncident(int id) throws IOException, InterruptedException {
        sendGet("/incidents?action=delete&id=" + id);
    }

    public List<ResponseTask> getTasks() throws IOException, InterruptedException {
        return Arrays.asList(mapper.readValue(sendGet("/tasks"), ResponseTask[].class));
    }

    public void addTask(ResponseTask t) throws IOException, InterruptedException {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "create");
        params.put("incidentId", String.valueOf(t.getIncidentId()));
        params.put("assignedUnit", t.getAssignedUnit());
        params.put("actionPlan", t.getActionPlan());
        params.put("deadlineDate", t.getDeadlineDate());
        sendPost("/tasks", params);
    }

    public void updateTask(ResponseTask t) throws IOException, InterruptedException {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("action", "update");
        params.put("id", String.valueOf(t.getId()));
        params.put("incidentId", String.valueOf(t.getIncidentId()));
        params.put("assignedUnit", t.getAssignedUnit());
        params.put("actionPlan", t.getActionPlan());
        params.put("deadlineDate", t.getDeadlineDate());
        sendPost("/tasks", params);
    }

    public void deleteTask(int id) throws IOException, InterruptedException {
        sendGet("/tasks?action=delete&id=" + id);
    }

    private String sendGet(String path) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE_URL + path)).GET().build();
        return handleResponse(http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)));
    }

    private String sendPost(String path, Map<String, String> form) throws IOException, InterruptedException {
        String body = form.entrySet().stream()
                .map(e -> encode(e.getKey()) + "=" + encode(e.getValue()))
                .collect(Collectors.joining("&"));
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE_URL + path))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();
        return handleResponse(http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)));
    }

    private String handleResponse(HttpResponse<String> resp) throws IOException {
        if (resp.statusCode() >= 400) {
            JsonNode node = mapper.readTree(resp.body());
            String err = node.has("error") ? node.get("error").asText() : "HTTP помилка " + resp.statusCode();
            throw new IllegalArgumentException(err);
        }
        return resp.body();
    }

    private String encode(String val) {
        return URLEncoder.encode(val != null ? val : "", StandardCharsets.UTF_8);
    }
}
