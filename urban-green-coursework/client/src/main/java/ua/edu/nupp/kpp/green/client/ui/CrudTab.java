package ua.edu.nupp.kpp.green.client.ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ua.edu.nupp.kpp.green.client.api.ApiClient;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// Спільний каркас вкладки: таблиця, форма та кнопки CRUD. Конкретні вкладки лише описують поля.
// Усі дані завантажуються із сервера; TableView не є локальним сховищем.
abstract class CrudTab<T> extends Tab {
    private static final Logger LOG = Logger.getLogger(CrudTab.class.getName());

    protected final ApiClient api;
    protected final TableView<T> table = new TableView<>();
    protected final GridPane form = new GridPane();

    protected CrudTab(String title, ApiClient api) {
        super(title);
        this.api = api;
        setClosable(false);
    }

    // Викликається в кінці конструктора підкласу, коли його елементи форми вже створені.
    protected final void init() {
        buildColumns();
        form.setHgap(8);
        form.setVgap(6);
        buildForm();

        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");
        Button clear = new Button("Очистити форму");
        add.setOnAction(e -> run(() -> {
            create(readForm(null));
            clearForm();
            refreshTable();
        }));
        update.setOnAction(e -> run(() -> {
            modify(readForm(idOf(selected())));
            refreshTable();
        }));
        delete.setOnAction(e -> run(() -> {
            remove(idOf(selected()));
            clearForm();
            refreshTable();
        }));
        refresh.setOnAction(e -> reload());
        clear.setOnAction(e -> {
            table.getSelectionModel().clearSelection();
            clearForm();
        });
        table.getSelectionModel().selectedItemProperty().addListener((o, a, b) -> {
            if (b != null) {
                fillForm(b);
            }
        });

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        VBox.setVgrow(table, Priority.ALWAYS);
        root.getChildren().addAll(table, form, new HBox(8, add, update, delete, refresh, clear));
        addExtras(root);
        setContent(root);
    }

    // Додаткові елементи вкладки (зміна стану, імпорт CSV). За замовчуванням їх немає.
    protected void addExtras(VBox root) {
    }

    public void reload() {
        run(this::refreshTable);
    }

    private void refreshTable() {
        table.setItems(FXCollections.observableArrayList(fetchAll()));
    }

    protected T selected() {
        T item = table.getSelectionModel().getSelectedItem();
        if (item == null) {
            throw new IllegalArgumentException("Виберіть запис у таблиці");
        }
        return item;
    }

    // Виконує дію; будь-яку помилку логує та показує користувачу у вікні, а не у консолі.
    protected void run(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            showError(e);
        }
    }

    protected void showError(Throwable e) {
        LOG.log(Level.WARNING, "Помилка операції: " + e.getMessage());
        Alert alert = new Alert(Alert.AlertType.ERROR, String.valueOf(e.getMessage()));
        alert.setHeaderText("Операцію не виконано");
        alert.showAndWait();
    }

    protected void info(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    protected void column(String title, String property) {
        TableColumn<T, Object> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        table.getColumns().add(c);
    }

    protected void row(int index, String label, Node control) {
        form.add(new Label(label), 0, index);
        form.add(control, 1, index);
    }

    protected abstract void buildColumns();

    protected abstract void buildForm();

    protected abstract T readForm(Integer id);

    protected abstract void fillForm(T item);

    protected abstract void clearForm();

    protected abstract Integer idOf(T item);

    protected abstract List<T> fetchAll();

    protected abstract void create(T item);

    protected abstract void modify(T item);

    protected abstract void remove(Integer id);
}
