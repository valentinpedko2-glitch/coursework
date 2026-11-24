package ua.edu.nupp.kpp.green.client.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import ua.edu.nupp.kpp.green.client.api.ApiClient;

import java.util.List;

// Головне вікно: чотири вкладки за чотирма основними сутностями.
public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        ApiClient api = new ApiClient();
        List<CrudTab<?>> tabs = List.of(
                new TreesTab(api),
                new InspectionsTab(api),
                new WorkRequestsTab(api),
                new WorkAssignmentsTab(api));

        TabPane pane = new TabPane();
        pane.getTabs().addAll(tabs);
        // При переході на вкладку дані завантажуються із сервера заново.
        pane.getSelectionModel().selectedItemProperty().addListener((o, a, b) -> {
            if (b instanceof CrudTab<?> tab) {
                tab.reload();
            }
        });

        stage.setTitle("Догляд за міськими зеленими насадженнями");
        stage.setScene(new Scene(pane, 1150, 720));
        stage.show();
        tabs.get(0).reload();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
