package ua.edu.nupp.kpp.green.client.ui;

import javafx.scene.control.TextField;
import ua.edu.nupp.kpp.green.client.api.ApiClient;
import ua.edu.nupp.kpp.green.client.model.WorkAssignment;

import java.util.List;

class WorkAssignmentsTab extends CrudTab<WorkAssignment> {
    private final TextField requestId = new TextField();
    private final TextField crew = new TextField();
    private final TextField plannedDate = new TextField();
    private final TextField note = new TextField();

    WorkAssignmentsTab(ApiClient api) {
        super("Наряди", api);
        init();
    }

    @Override
    protected void buildColumns() {
        column("ID", "id");
        column("ID заявки", "requestId");
        column("Бригада", "crewName");
        column("Планова дата", "plannedDate");
        column("Примітка", "note");
    }

    @Override
    protected void buildForm() {
        plannedDate.setPromptText("рррр-мм-дд");
        row(0, "ID заявки", requestId);
        row(1, "Бригада", crew);
        row(2, "Планова дата", plannedDate);
        row(3, "Примітка", note);
    }

    @Override
    protected WorkAssignment readForm(Integer id) {
        WorkAssignment a = new WorkAssignment();
        a.setId(id);
        a.setRequestId(Fields.integer(requestId, "ID заявки", 1, Integer.MAX_VALUE));
        a.setCrewName(Fields.text(crew, "Бригада"));
        a.setPlannedDate(Fields.date(plannedDate, "Планова дата"));
        a.setNote(Fields.optional(note));
        return a;
    }

    @Override
    protected void fillForm(WorkAssignment a) {
        requestId.setText(String.valueOf(a.getRequestId()));
        crew.setText(a.getCrewName());
        plannedDate.setText(a.getPlannedDate());
        note.setText(a.getNote() == null ? "" : a.getNote());
    }

    @Override
    protected void clearForm() {
        requestId.clear();
        crew.clear();
        plannedDate.clear();
        note.clear();
    }

    @Override
    protected Integer idOf(WorkAssignment a) {
        return a.getId();
    }

    @Override
    protected List<WorkAssignment> fetchAll() {
        return api.getAssignments();
    }

    @Override
    protected void create(WorkAssignment a) {
        api.addAssignment(a);
    }

    @Override
    protected void modify(WorkAssignment a) {
        api.updateAssignment(a);
    }

    @Override
    protected void remove(Integer id) {
        api.deleteAssignment(id);
    }
}
