package ua.edu.nupp.kpp.green.client.ui;

import javafx.scene.control.TextField;
import ua.edu.nupp.kpp.green.client.api.ApiClient;
import ua.edu.nupp.kpp.green.client.model.Inspection;

import java.util.List;

class InspectionsTab extends CrudTab<Inspection> {
    private final TextField treeId = new TextField();
    private final TextField date = new TextField();
    private final TextField health = new TextField();
    private final TextField defects = new TextField();

    InspectionsTab(ApiClient api) {
        super("Огляди", api);
        init();
    }

    @Override
    protected void buildColumns() {
        column("ID", "id");
        column("ID дерева", "treeId");
        column("Дата", "inspectionDate");
        column("Стан (0-100)", "healthScore");
        column("Дефекти", "defects");
    }

    @Override
    protected void buildForm() {
        date.setPromptText("рррр-мм-дд");
        row(0, "ID дерева", treeId);
        row(1, "Дата огляду", date);
        row(2, "Оцінка стану 0-100 (100 - здорове)", health);
        row(3, "Дефекти", defects);
    }

    @Override
    protected Inspection readForm(Integer id) {
        Inspection i = new Inspection();
        i.setId(id);
        i.setTreeId(Fields.integer(treeId, "ID дерева", 1, Integer.MAX_VALUE));
        i.setInspectionDate(Fields.date(date, "Дата огляду"));
        i.setHealthScore(Fields.integer(health, "Оцінка стану", 0, 100));
        i.setDefects(Fields.optional(defects));
        return i;
    }

    @Override
    protected void fillForm(Inspection i) {
        treeId.setText(String.valueOf(i.getTreeId()));
        date.setText(i.getInspectionDate());
        health.setText(String.valueOf(i.getHealthScore()));
        defects.setText(i.getDefects() == null ? "" : i.getDefects());
    }

    @Override
    protected void clearForm() {
        treeId.clear();
        date.clear();
        health.clear();
        defects.clear();
    }

    @Override
    protected Integer idOf(Inspection i) {
        return i.getId();
    }

    @Override
    protected List<Inspection> fetchAll() {
        return api.getInspections();
    }

    @Override
    protected void create(Inspection i) {
        api.addInspection(i);
    }

    @Override
    protected void modify(Inspection i) {
        api.updateInspection(i);
    }

    @Override
    protected void remove(Integer id) {
        api.deleteInspection(id);
    }
}
