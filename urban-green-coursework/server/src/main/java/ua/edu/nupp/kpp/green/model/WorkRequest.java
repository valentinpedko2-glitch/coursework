package ua.edu.nupp.kpp.green.model;

// Заявка на роботи (об'єкт життєвого циклу).
public class WorkRequest implements Identifiable {
    private Integer id;
    private Integer treeId;
    private Integer inspectionId;
    private String workType;
    private String description;
    private Integer deadlineDays;
    private RequestState state;
    private Double f1;
    private Double f2;
    private Double f3;
    private Double priorityIndex;

    public WorkRequest() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTreeId() {
        return treeId;
    }

    public void setTreeId(Integer treeId) {
        this.treeId = treeId;
    }

    public Integer getInspectionId() {
        return inspectionId;
    }

    public void setInspectionId(Integer inspectionId) {
        this.inspectionId = inspectionId;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDeadlineDays() {
        return deadlineDays;
    }

    public void setDeadlineDays(Integer deadlineDays) {
        this.deadlineDays = deadlineDays;
    }

    public RequestState getState() {
        return state;
    }

    public void setState(RequestState state) {
        this.state = state;
    }

    public Double getF1() {
        return f1;
    }

    public void setF1(Double f1) {
        this.f1 = f1;
    }

    public Double getF2() {
        return f2;
    }

    public void setF2(Double f2) {
        this.f2 = f2;
    }

    public Double getF3() {
        return f3;
    }

    public void setF3(Double f3) {
        this.f3 = f3;
    }

    public Double getPriorityIndex() {
        return priorityIndex;
    }

    public void setPriorityIndex(Double priorityIndex) {
        this.priorityIndex = priorityIndex;
    }
}
