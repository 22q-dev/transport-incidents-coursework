package ua.edu.nupp.kpp.transport.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.logging.Logger;

public class TransportClientApp extends Application {
    private static final Logger LOG = Logger.getLogger(TransportClientApp.class.getName());
    private final TransportApiClient api = new TransportApiClient();

    private final TableView<Route> routeTable = new TableView<>();
    private final TableView<Vehicle> vehicleTable = new TableView<>();
    private final TableView<Incident> incidentTable = new TableView<>();
    private final TableView<ResponseTask> taskTable = new TableView<>();

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane(
                buildRoutesTab(),
                buildVehiclesTab(),
                buildIncidentsTab(),
                buildTasksTab()
        );
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        stage.setTitle("ІС реєстрації інцидентів громадського транспорту (Варіант №20)");
        stage.setScene(new Scene(tabs, 1020, 660));
        stage.show();

        refreshAll();
    }

    private Tab buildRoutesTab() {
        addColumn(routeTable, "ID", "id", 60);
        addColumn(routeTable, "Код маршруту", "routeCode", 130);
        addColumn(routeTable, "Назва маршруту", "routeName", 380);
        addColumn(routeTable, "Важливість F2 (0..100)", "importanceScore", 170);

        TextField codeField = new TextField();
        TextField nameField = new TextField();
        TextField f2Field = new TextField();

        routeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, sel) -> {
            if (sel != null) {
                codeField.setText(sel.getRouteCode());
                nameField.setText(sel.getRouteName());
                f2Field.setText(String.valueOf(sel.getImportanceScore()));
            }
        });

        Button addBtn = new Button("Додати");
        addBtn.setOnAction(e -> executeSafely(() -> {
            api.addRoute(new Route(0, codeField.getText(), nameField.getText(), Double.parseDouble(f2Field.getText())));
            refreshAll();
        }));

        Button editBtn = new Button("Змінити");
        editBtn.setOnAction(e -> executeSafely(() -> {
            Route sel = requireSelected(routeTable);
            api.updateRoute(new Route(sel.getId(), codeField.getText(), nameField.getText(), Double.parseDouble(f2Field.getText())));
            refreshAll();
        }));

        Button delBtn = new Button("Видалити");
        delBtn.setOnAction(e -> executeSafely(() -> {
            Route sel = requireSelected(routeTable);
            api.deleteRoute(sel.getId());
            refreshAll();
        }));

        Button reloadBtn = new Button("Оновити");
        reloadBtn.setOnAction(e -> executeSafely(this::refreshAll));

        GridPane form = createForm(
                new String[]{"Код маршруту:", "Назва маршруту:", "Важливість F2 (0..100):"},
                new Control[]{codeField, nameField, f2Field}
        );
        HBox buttons = new HBox(10, addBtn, editBtn, delBtn, reloadBtn);
        return new Tab("Маршрути", createTabLayout(routeTable, new VBox(10, form, buttons)));
    }

    private Tab buildVehiclesTab() {
        addColumn(vehicleTable, "ID", "id", 60);
        addColumn(vehicleTable, "ID маршруту", "routeId", 110);
        addColumn(vehicleTable, "Бортовий номер", "boardNumber", 160);
        addColumn(vehicleTable, "Модель ТЗ", "modelName", 260);
        addColumn(vehicleTable, "Місткість (осіб)", "capacity", 150);

        TextField routeIdField = new TextField();
        TextField boardField = new TextField();
        TextField modelField = new TextField();
        TextField capacityField = new TextField();

        vehicleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, sel) -> {
            if (sel != null) {
                routeIdField.setText(String.valueOf(sel.getRouteId()));
                boardField.setText(sel.getBoardNumber());
                modelField.setText(sel.getModelName());
                capacityField.setText(String.valueOf(sel.getCapacity()));
            }
        });

        Button addBtn = new Button("Додати");
        addBtn.setOnAction(e -> executeSafely(() -> {
            api.addVehicle(new Vehicle(0, Integer.parseInt(routeIdField.getText()),
                    boardField.getText(), modelField.getText(), Integer.parseInt(capacityField.getText())));
            refreshVehicles();
        }));

        Button editBtn = new Button("Змінити");
        editBtn.setOnAction(e -> executeSafely(() -> {
            Vehicle sel = requireSelected(vehicleTable);
            api.updateVehicle(new Vehicle(sel.getId(), Integer.parseInt(routeIdField.getText()),
                    boardField.getText(), modelField.getText(), Integer.parseInt(capacityField.getText())));
            refreshAll();
        }));

        Button delBtn = new Button("Видалити");
        delBtn.setOnAction(e -> executeSafely(() -> {
            Vehicle sel = requireSelected(vehicleTable);
            api.deleteVehicle(sel.getId());
            refreshVehicles();
        }));

        Button reloadBtn = new Button("Оновити");
        reloadBtn.setOnAction(e -> executeSafely(this::refreshVehicles));

        Button csvBtn = new Button("Імпортувати CSV");
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(240);
        Label statusLabel = new Label("Очікування імпорту...");

        csvBtn.setOnAction(e -> {
            VehicleCsvImportTask task = new VehicleCsvImportTask(Path.of("sample-data", "vehicles.csv"), api);
            csvBtn.setDisable(true);
            progressBar.progressProperty().bind(task.progressProperty());
            statusLabel.textProperty().bind(task.messageProperty());

            task.setOnSucceeded(ev -> {
                progressBar.progressProperty().unbind();
                statusLabel.textProperty().unbind();
                csvBtn.setDisable(false);
                statusLabel.setText("Готово. Імпортовано записів: " + task.getValue());
                executeSafely(this::refreshVehicles);
            });

            task.setOnFailed(ev -> {
                progressBar.progressProperty().unbind();
                statusLabel.textProperty().unbind();
                csvBtn.setDisable(false);
                Throwable ex = task.getException();
                statusLabel.setText("Помилка CSV-імпорту");
                showError(ex != null ? ex.getMessage() : "Невідома помилка імпорту");
            });

            Thread bgThread = new Thread(task, "vehicle-csv-import-thread");
            bgThread.setDaemon(true);
            bgThread.start();
        });

        GridPane form = createForm(
                new String[]{"ID маршруту:", "Бортовий номер:", "Модель ТЗ:", "Місткість (осіб):"},
                new Control[]{routeIdField, boardField, modelField, capacityField}
        );
        HBox crudBox = new HBox(10, addBtn, editBtn, delBtn, reloadBtn);
        HBox csvBox = new HBox(12, csvBtn, progressBar, statusLabel);
        return new Tab("Транспортні засоби", createTabLayout(vehicleTable, new VBox(10, form, crudBox, csvBox)));
    }

    private Tab buildIncidentsTab() {
        addColumn(incidentTable, "ID", "id", 50);
        addColumn(incidentTable, "ID ТЗ", "vehicleId", 65);
        addColumn(incidentTable, "Опис інциденту", "description", 250);
        addColumn(incidentTable, "Пасажири", "affectedPassengers", 85);
        addColumn(incidentTable, "Стан", "state", 120);
        addColumn(incidentTable, "F1", "f1", 70);
        addColumn(incidentTable, "F2", "f2", 70);
        addColumn(incidentTable, "F3", "f3", 70);
        addColumn(incidentTable, "Показник I", "integralIndex", 100);

        TextField vehicleIdField = new TextField();
        TextField descField = new TextField();
        TextField passengersField = new TextField();
        TextField f3Field = new TextField();
        ComboBox<IncidentState> stateBox = new ComboBox<>(FXCollections.observableArrayList(IncidentState.values()));
        stateBox.setValue(IncidentState.CONFIRMED);

        incidentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, sel) -> {
            if (sel != null) {
                vehicleIdField.setText(String.valueOf(sel.getVehicleId()));
                descField.setText(sel.getDescription());
                passengersField.setText(String.valueOf(sel.getAffectedPassengers()));
                f3Field.setText(String.valueOf(sel.getDurationScore()));
                stateBox.setValue(sel.getState());
            }
        });

        Button addBtn = new Button("Додати");
        addBtn.setOnAction(e -> executeSafely(() -> {
            api.addIncident(new Incident(0, Integer.parseInt(vehicleIdField.getText()),
                    descField.getText(), Integer.parseInt(passengersField.getText()),
                    Double.parseDouble(f3Field.getText())));
            refreshIncidents();
        }));

        Button editBtn = new Button("Змінити");
        editBtn.setOnAction(e -> executeSafely(() -> {
            Incident sel = requireSelected(incidentTable);
            api.updateIncident(new Incident(sel.getId(), Integer.parseInt(vehicleIdField.getText()),
                    descField.getText(), Integer.parseInt(passengersField.getText()),
                    Double.parseDouble(f3Field.getText())));
            refreshIncidents();
        }));

        Button stateBtn = new Button("Змінити стан");
        stateBtn.setOnAction(e -> executeSafely(() -> {
            Incident sel = requireSelected(incidentTable);
            api.changeIncidentState(sel.getId(), stateBox.getValue());
            refreshIncidents();
        }));

        Button delBtn = new Button("Видалити");
        delBtn.setOnAction(e -> executeSafely(() -> {
            Incident sel = requireSelected(incidentTable);
            api.deleteIncident(sel.getId());
            refreshIncidents();
        }));

        Button reloadBtn = new Button("Оновити");
        reloadBtn.setOnAction(e -> executeSafely(this::refreshIncidents));

        GridPane form = createForm(
                new String[]{"ID ТЗ:", "Опис інциденту:", "Кількість пасажирів:", "Тривалість F3 (0..100):", "Новий стан:"},
                new Control[]{vehicleIdField, descField, passengersField, f3Field, stateBox}
        );
        HBox buttons = new HBox(10, addBtn, editBtn, stateBtn, delBtn, reloadBtn);
        return new Tab("Інциденти", createTabLayout(incidentTable, new VBox(10, form, buttons)));
    }

    private Tab buildTasksTab() {
        addColumn(taskTable, "ID", "id", 60);
        addColumn(taskTable, "ID інциденту", "incidentId", 110);
        addColumn(taskTable, "Підрозділ", "assignedUnit", 220);
        addColumn(taskTable, "Зміст завдання реагування", "actionPlan", 340);
        addColumn(taskTable, "Термін", "deadlineDate", 130);

        TextField incIdField = new TextField();
        TextField unitField = new TextField();
        TextField planField = new TextField();
        TextField dateField = new TextField();

        taskTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, sel) -> {
            if (sel != null) {
                incIdField.setText(String.valueOf(sel.getIncidentId()));
                unitField.setText(sel.getAssignedUnit());
                planField.setText(sel.getActionPlan());
                dateField.setText(sel.getDeadlineDate());
            }
        });

        Button addBtn = new Button("Додати");
        addBtn.setOnAction(e -> executeSafely(() -> {
            api.addTask(new ResponseTask(0, Integer.parseInt(incIdField.getText()),
                    unitField.getText(), planField.getText(), dateField.getText()));
            refreshTasks();
        }));

        Button editBtn = new Button("Змінити");
        editBtn.setOnAction(e -> executeSafely(() -> {
            ResponseTask sel = requireSelected(taskTable);
            api.updateTask(new ResponseTask(sel.getId(), Integer.parseInt(incIdField.getText()),
                    unitField.getText(), planField.getText(), dateField.getText()));
            refreshTasks();
        }));

        Button delBtn = new Button("Видалити");
        delBtn.setOnAction(e -> executeSafely(() -> {
            ResponseTask sel = requireSelected(taskTable);
            api.deleteTask(sel.getId());
            refreshTasks();
        }));

        Button reloadBtn = new Button("Оновити");
        reloadBtn.setOnAction(e -> executeSafely(this::refreshTasks));

        GridPane form = createForm(
                new String[]{"ID інциденту:", "Відповідальний підрозділ:", "Зміст завдання:", "Планова дата (YYYY-MM-DD):"},
                new Control[]{incIdField, unitField, planField, dateField}
        );
        HBox buttons = new HBox(10, addBtn, editBtn, delBtn, reloadBtn);
        return new Tab("Завдання реагування", createTabLayout(taskTable, new VBox(10, form, buttons)));
    }

    private void refreshAll() {
        executeSafely(() -> {
            refreshRoutes();
            refreshVehicles();
            refreshIncidents();
            refreshTasks();
        });
    }

    private void refreshRoutes() throws Exception {
        routeTable.setItems(FXCollections.observableArrayList(api.getRoutes()));
    }

    private void refreshVehicles() throws Exception {
        vehicleTable.setItems(FXCollections.observableArrayList(api.getVehicles()));
    }

    private void refreshIncidents() throws Exception {
        incidentTable.setItems(FXCollections.observableArrayList(api.getIncidents()));
    }

    private void refreshTasks() throws Exception {
        taskTable.setItems(FXCollections.observableArrayList(api.getTasks()));
    }

    private <T> void addColumn(TableView<T> table, String title, String property, int width) {
        TableColumn<T, Object> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setPrefWidth(width);
        table.getColumns().add(col);
    }

    private GridPane createForm(String[] labels, Control[] controls) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        for (int i = 0; i < labels.length; i++) {
            grid.add(new Label(labels[i]), 0, i);
            controls[i].setPrefWidth(320);
            grid.add(controls[i], 1, i);
        }
        return grid;
    }

    private BorderPane createTabLayout(TableView<?> table, VBox bottomPanel) {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(12));
        pane.setCenter(table);
        bottomPanel.setPadding(new Insets(12, 0, 0, 0));
        pane.setBottom(bottomPanel);
        return pane;
    }

    private <T> T requireSelected(TableView<T> table) {
        T sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            throw new IllegalArgumentException("Виберіть запис у таблиці");
        }
        return sel;
    }

    private void executeSafely(ThrowingAction action) {
        try {
            action.run();
        } catch (Exception e) {
            LOG.warning("Помилка операції: " + e.getMessage());
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Помилка");
        alert.setHeaderText("Не вдалося виконати операцію");
        alert.showAndWait();
    }

    @FunctionalInterface
    private interface ThrowingAction {
        void run() throws Exception;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
