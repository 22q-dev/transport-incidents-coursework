package ua.edu.nupp.kpp.transport.server;

public class ResponseTask {
    private int id;
    private int incidentId;
    private String assignedUnit;
    private String actionPlan;
    private String deadlineDate;

    public ResponseTask() {
    }

    public ResponseTask(int id, int incidentId, String assignedUnit, String actionPlan, String deadlineDate) {
        this.id = id;
        this.incidentId = incidentId;
        this.assignedUnit = assignedUnit;
        this.actionPlan = actionPlan;
        this.deadlineDate = deadlineDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIncidentId() { return incidentId; }
    public void setIncidentId(int incidentId) { this.incidentId = incidentId; }

    public String getAssignedUnit() { return assignedUnit; }
    public void setAssignedUnit(String assignedUnit) { this.assignedUnit = assignedUnit; }

    public String getActionPlan() { return actionPlan; }
    public void setActionPlan(String actionPlan) { this.actionPlan = actionPlan; }

    public String getDeadlineDate() { return deadlineDate; }
    public void setDeadlineDate(String deadlineDate) { this.deadlineDate = deadlineDate; }
}
