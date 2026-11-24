package ua.edu.nupp.kpp.green.client.ui;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ua.edu.nupp.kpp.green.client.api.ApiClient;
import ua.edu.nupp.kpp.green.client.model.RequestState;
import ua.edu.nupp.kpp.green.client.model.WorkRequest;

import java.util.List;

// Вкладка «Заявки на роботи»: CRUD, зміна стану та інтегральний показник I (його рахує сервер).
class WorkRequestsTab extends CrudTab<WorkRequest> {
    private final TextField treeId = new TextField();
    private final TextField inspectionId = new TextField();
    private final ComboBox<String> workType = new ComboBox<>();
    private final TextField description = new TextField();
    private final TextField deadline = new TextField();
    private final ComboBox<RequestState> stateBox = new ComboBox<>();

    WorkRequestsTab(ApiClient api) {
        super("Заявки на роботи", api);
        init();
    }

    @Override
    protected void buildColumns() {
        column("ID", "id");
        column("ID дерева", "treeId");
        column("ID огляду", "inspectionId");
        column("Вид робіт", "workType");
        column("Строк, діб", "deadlineDays");
        column("Стан", "state");
        column("F1", "f1");
        column("F2", "f2");
        column("F3", "f3");
        column("Індекс I", "priorityIndex");
    }

    @Override
    protected void buildForm() {
        workType.getItems().addAll("PRUNING", "TREATMENT", "REMOVAL", "WATERING");
        row(0, "ID дерева", treeId);
        row(1, "ID огляду", inspectionId);
        row(2, "Вид робіт", workType);
        row(3, "Опис робіт", description);
        row(4, "Строк виконання, діб (0-365)", deadline);
    }

    @Override
    protected void addExtras(VBox root) {
        stateBox.getItems().addAll(RequestState.values());
        stateBox.setPromptText("Новий стан");
        Button change = new Button("Змінити стан вибраної заявки");
        change.setOnAction(e -> run(() -> {
            WorkRequest selected = selected();
            if (stateBox.getValue() == null) {
                throw new IllegalArgumentException("Оберіть новий стан");
            }
            api.changeRequestState(selected.getId(), stateBox.getValue());
            reload();
        }));
        root.getChildren().add(new HBox(10, new Label("Життєвий цикл:"), stateBox, change));
    }

    @Override
    protected WorkRequest readForm(Integer id) {
        WorkRequest r = new WorkRequest();
        r.setId(id);
        r.setTreeId(Fields.integer(treeId, "ID дерева", 1, Integer.MAX_VALUE));
        r.setInspectionId(Fields.integer(inspectionId, "ID огляду", 1, Integer.MAX_VALUE));
        if (workType.getValue() == null) {
            throw new IllegalArgumentException("Оберіть вид робіт");
        }
        r.setWorkType(workType.getValue());
        r.setDescription(Fields.text(description, "Опис робіт"));
        r.setDeadlineDays(Fields.integer(deadline, "Строк виконання", 0, 365));
        return r;
    }

    @Override
    protected void fillForm(WorkRequest r) {
        treeId.setText(String.valueOf(r.getTreeId()));
        inspectionId.setText(String.valueOf(r.getInspectionId()));
        workType.setValue(r.getWorkType());
        description.setText(r.getDescription());
        deadline.setText(String.valueOf(r.getDeadlineDays()));
    }

    @Override
    protected void clearForm() {
        treeId.clear();
        inspectionId.clear();
        workType.setValue(null);
        description.clear();
        deadline.clear();
    }

    @Override
    protected Integer idOf(WorkRequest r) {
        return r.getId();
    }

    @Override
    protected List<WorkRequest> fetchAll() {
        return api.getRequests();
    }

    @Override
    protected void create(WorkRequest r) {
        api.addRequest(r);
    }

    @Override
    protected void modify(WorkRequest r) {
        api.updateRequest(r);
    }

    @Override
    protected void remove(Integer id) {
        api.deleteRequest(id);
    }
}
