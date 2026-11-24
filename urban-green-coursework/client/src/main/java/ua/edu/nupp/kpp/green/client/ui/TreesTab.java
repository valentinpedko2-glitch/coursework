package ua.edu.nupp.kpp.green.client.ui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ua.edu.nupp.kpp.green.client.api.ApiClient;
import ua.edu.nupp.kpp.green.client.csv.ImportTreesTask;
import ua.edu.nupp.kpp.green.client.csv.TreeCsvImporter;
import ua.edu.nupp.kpp.green.client.model.Tree;

import java.io.File;
import java.util.List;

// Вкладка «Дерева»: CRUD реєстру та фоновий CSV-імпорт з індикатором прогресу.
class TreesTab extends CrudTab<Tree> {
    private final TextField code = new TextField();
    private final TextField species = new TextField();
    private final TextField location = new TextField();
    private final TextField year = new TextField();
    private final TextField pedestrians = new TextField();

    private final Button importButton = new Button("Імпортувати CSV…");
    private final Button cancelButton = new Button("Скасувати імпорт");
    private final ProgressBar progress = new ProgressBar(0);
    private final Label status = new Label("Імпорт не запускався");

    TreesTab(ApiClient api) {
        super("Дерева", api);
        init();
    }

    @Override
    protected void buildColumns() {
        column("ID", "id");
        column("Код", "code");
        column("Порода", "species");
        column("Місцезнаходження", "location");
        column("Рік посадки", "plantingYear");
        column("Пішоходів/год", "pedestriansPerHour");
    }

    @Override
    protected void buildForm() {
        row(0, "Код", code);
        row(1, "Порода", species);
        row(2, "Місцезнаходження", location);
        row(3, "Рік посадки", year);
        row(4, "Пішоходів за годину", pedestrians);
    }

    @Override
    protected void addExtras(VBox root) {
        progress.setPrefWidth(260);
        cancelButton.setDisable(true);
        importButton.setOnAction(e -> startImport());
        root.getChildren().add(new HBox(10, importButton, cancelButton, progress, status));
    }

    @Override
    protected Tree readForm(Integer id) {
        Tree t = new Tree();
        t.setId(id);
        t.setCode(Fields.text(code, "Код"));
        t.setSpecies(Fields.text(species, "Порода"));
        t.setLocation(Fields.text(location, "Місцезнаходження"));
        t.setPlantingYear(Fields.integer(year, "Рік посадки", 1800, 2100));
        t.setPedestriansPerHour(Fields.integer(pedestrians, "Пішоходів за годину", 0, 100000));
        return t;
    }

    @Override
    protected void fillForm(Tree t) {
        code.setText(t.getCode());
        species.setText(t.getSpecies());
        location.setText(t.getLocation());
        year.setText(String.valueOf(t.getPlantingYear()));
        pedestrians.setText(String.valueOf(t.getPedestriansPerHour()));
    }

    @Override
    protected void clearForm() {
        code.clear();
        species.clear();
        location.clear();
        year.clear();
        pedestrians.clear();
    }

    @Override
    protected Integer idOf(Tree t) {
        return t.getId();
    }

    @Override
    protected List<Tree> fetchAll() {
        return api.getTrees();
    }

    @Override
    protected void create(Tree t) {
        api.addTree(t);
    }

    @Override
    protected void modify(Tree t) {
        api.updateTree(t);
    }

    @Override
    protected void remove(Integer id) {
        api.deleteTree(id);
    }

    // ---- Фоновий CSV-імпорт ----
    private void startImport() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Оберіть CSV-файл реєстру дерев");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File sampleDir = new File("sample-data");
        if (sampleDir.isDirectory()) {
            chooser.setInitialDirectory(sampleDir);
        }
        File file = chooser.showOpenDialog(getTabPane().getScene().getWindow());
        if (file == null) {
            return;
        }

        ImportTreesTask task = new ImportTreesTask(file.toPath(), api::addTree);
        progress.progressProperty().bind(task.progressProperty());
        status.textProperty().bind(task.messageProperty());
        importButton.setDisable(true);
        cancelButton.setDisable(false);
        cancelButton.setOnAction(e -> task.cancel());

        task.setOnSucceeded(e -> {
            finishImport();
            TreeCsvImporter.Result r = task.getValue();
            status.setText("Готово. Імпортовано: " + r.imported() + ", помилок: " + r.failed());
            reload();
            info(summary(r));
        });
        task.setOnFailed(e -> {
            finishImport();
            status.setText("Імпорт перервано через помилку");
            showError(task.getException());
        });
        task.setOnCancelled(e -> {
            finishImport();
            status.setText("Імпорт скасовано");
            reload();
        });

        Thread worker = new Thread(task, "csv-import");
        worker.setDaemon(true);
        worker.start();
    }

    private void finishImport() {
        progress.progressProperty().unbind();
        status.textProperty().unbind();
        importButton.setDisable(false);
        cancelButton.setDisable(true);
    }

    private String summary(TreeCsvImporter.Result r) {
        StringBuilder sb = new StringBuilder();
        sb.append("Імпортовано: ").append(r.imported()).append("\nПомилок: ").append(r.failed());
        int shown = Math.min(r.errors().size(), 8);
        for (int i = 0; i < shown; i++) {
            sb.append("\n• ").append(r.errors().get(i));
        }
        if (r.errors().size() > shown) {
            sb.append("\n… та ще ").append(r.errors().size() - shown);
        }
        return sb.toString();
    }
}
