package ua.edu.nupp.kpp.transport.server;

public class Route {
    private int id;
    private String routeCode;
    private String routeName;
    private double importanceScore;

    public Route() {
    }

    public Route(int id, String routeCode, String routeName, double importanceScore) {
        this.id = id;
        this.routeCode = routeCode;
        this.routeName = routeName;
        this.importanceScore = importanceScore;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRouteCode() { return routeCode; }
    public void setRouteCode(String routeCode) { this.routeCode = routeCode; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public double getImportanceScore() { return importanceScore; }
    public void setImportanceScore(double importanceScore) { this.importanceScore = importanceScore; }
}
