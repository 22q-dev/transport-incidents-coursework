package ua.edu.nupp.kpp.transport.client;

public class Incident {
    private int id;
    private int vehicleId;
    private String description;
    private int affectedPassengers;
    private double durationScore;
    private IncidentState state;
    private double f1;
    private double f2;
    private double f3;
    private double integralIndex;

    public Incident() {
    }

    public Incident(int id, int vehicleId, String description, int affectedPassengers, double durationScore) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.description = description;
        this.affectedPassengers = affectedPassengers;
        this.durationScore = durationScore;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getAffectedPassengers() { return affectedPassengers; }
    public void setAffectedPassengers(int affectedPassengers) { this.affectedPassengers = affectedPassengers; }

    public double getDurationScore() { return durationScore; }
    public void setDurationScore(double durationScore) { this.durationScore = durationScore; }

    public IncidentState getState() { return state; }
    public void setState(IncidentState state) { this.state = state; }

    public double getF1() { return f1; }
    public void setF1(double f1) { this.f1 = f1; }

    public double getF2() { return f2; }
    public void setF2(double f2) { this.f2 = f2; }

    public double getF3() { return f3; }
    public void setF3(double f3) { this.f3 = f3; }

    public double getIntegralIndex() { return integralIndex; }
    public void setIntegralIndex(double integralIndex) { this.integralIndex = integralIndex; }
}
