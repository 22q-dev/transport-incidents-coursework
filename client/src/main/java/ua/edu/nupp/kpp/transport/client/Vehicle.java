package ua.edu.nupp.kpp.transport.client;

public class Vehicle {
    private int id;
    private int routeId;
    private String boardNumber;
    private String modelName;
    private int capacity;

    public Vehicle() {
    }

    public Vehicle(int id, int routeId, String boardNumber, String modelName, int capacity) {
        this.id = id;
        this.routeId = routeId;
        this.boardNumber = boardNumber;
        this.modelName = modelName;
        this.capacity = capacity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRouteId() { return routeId; }
    public void setRouteId(int routeId) { this.routeId = routeId; }

    public String getBoardNumber() { return boardNumber; }
    public void setBoardNumber(String boardNumber) { this.boardNumber = boardNumber; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}
