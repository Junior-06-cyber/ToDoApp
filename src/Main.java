import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.media.AudioClip;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import java.util.Optional;

public class Main extends Application {
    private TaskManager taskManager = new TaskManager(getCurrentUser());
    private VBox taskList = new VBox(10);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private ListView<Task> listView;
    private ObservableList<Task> observableTasks;
    private String currentFilter = "Inbox";
    private String currentUser = "Guest";

    private void setCurrentUser(String username) {
        this.currentUser = username;
    }
    private String getCurrentUser() {
        return this.currentUser;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("To-Do APP");

        // Sidebar (VBox) setup
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(30, 10, 30, 10));
        sidebar.setStyle("-fx-background-color: #222831;");
        sidebar.setPrefWidth(140);
        sidebar.setMinWidth(60);

        // Remove Inbox tab, only show Today, Upcoming, Completed
        Button todayBtn = new Button("Today");
        Button upcomingBtn = new Button("Upcoming");
        Button completedBtn = new Button("Completed");
        for (Button b : new Button[]{todayBtn, upcomingBtn, completedBtn}) {
            b.setMaxWidth(Double.MAX_VALUE);
            b.getStyleClass().add("sidebar-btn");
        }
        sidebar.getChildren().addAll(todayBtn, upcomingBtn, completedBtn);

        // Add Task button in sidebar
        Button addSidebarBtn = new Button("+ Add Task");
        addSidebarBtn.getStyleClass().add("sidebar-add-btn");
        addSidebarBtn.setMaxWidth(Double.MAX_VALUE);
        addSidebarBtn.setOnAction(e -> showAddTaskDialog());
        sidebar.getChildren().add(0, addSidebarBtn);

        // Username label at the top of the sidebar
        Label userLabel = new Label("User: " + getCurrentUser());
        userLabel.getStyleClass().add("sidebar-user-label");
        sidebar.getChildren().add(0, userLabel);

        // Add Login/Register button to sidebar
        Button loginBtn = new Button("Login/Register");
        loginBtn.getStyleClass().add("sidebar-add-btn");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setOnAction(e -> {
            UserManager um = new UserManager();
            if (showLoginDialog(um)) {
                setCurrentUser(um.getLastLoggedInUser());
                userLabel.setText("User: " + getCurrentUser());
                taskManager.setUser(getCurrentUser());
                updateFilter(currentFilter);
            }
        });
        sidebar.getChildren().add(2, loginBtn); // Below Add Task button

        // Add Logout button to sidebar
        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("sidebar-add-btn");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setOnAction(e -> {
            setCurrentUser("Guest");
            userLabel.setText("User: Guest");
            taskManager.setUser("Guest");
            updateFilter(currentFilter);
        });
        sidebar.getChildren().add(3, logoutBtn); // Below Login/Register

        // Sidebar icons with PNG (robust resource loading)
        ImageView searchIcon = new ImageView(new Image(getClass().getResourceAsStream("/search.png")));
        searchIcon.setFitWidth(20);
        searchIcon.setFitHeight(20);
        Button searchBtn = new Button();
        searchBtn.setGraphic(searchIcon);
        searchBtn.getStyleClass().add("sidebar-icon-btn");
        searchBtn.setOnAction(e -> showSearchDialog());

        ImageView filterIcon = new ImageView(new Image(getClass().getResourceAsStream("/filter.png")));
        filterIcon.setFitWidth(20);
        filterIcon.setFitHeight(20);
        Button filterBtn = new Button();
        filterBtn.setGraphic(filterIcon);
        filterBtn.getStyleClass().add("sidebar-icon-btn");
        filterBtn.setOnAction(e -> showFilterDialog());

        HBox iconBar = new HBox(10, searchBtn, filterBtn);
        iconBar.setAlignment(Pos.CENTER);
        sidebar.getChildren().add(0, iconBar);

        // Main ListView
        observableTasks = FXCollections.observableArrayList(taskManager.getTasks());
        listView = new ListView<>(observableTasks);
        listView.setCellFactory(lv -> new TaskCell());
        listView.setStyle("-fx-background-color: #f9f9f9;");
        updateFilter("Inbox");

        // Floating Add Button
        Button addBtn = new Button("+");
        addBtn.getStyleClass().add("floating-btn");
        addBtn.setOnAction(e -> showAddTaskDialog());

        StackPane mainPane = new StackPane(listView, addBtn);
        StackPane.setAlignment(addBtn, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(addBtn, new Insets(0, 30, 30, 0));

        // Use SplitPane for resizable sidebar
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(sidebar, mainPane);
        splitPane.setDividerPositions(0.18); // Sidebar width ratio
        splitPane.setStyle("-fx-background-color: transparent;");

        BorderPane root = new BorderPane();
        root.setCenter(splitPane);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("styles.css");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Remove inboxBtn.setOnAction
        todayBtn.setOnAction(e -> updateFilter("Today"));
        upcomingBtn.setOnAction(e -> updateFilter("Upcoming"));
        completedBtn.setOnAction(e -> updateFilter("Completed"));
    }

    private void updateFilter(String filter) {
        currentFilter = filter;
        observableTasks.setAll(taskManager.getTasks().stream().filter(task -> {
            if (filter.equals("Today")) return !task.isCompleted() && isToday(task);
            if (filter.equals("Upcoming")) return !task.isCompleted() && isUpcoming(task);
            if (filter.equals("Completed")) return task.isCompleted();
            return true;
        }).toList());
    }

    private boolean isToday(Task task) {
        try {
            LocalDateTime due = LocalDateTime.parse(task.getReminderTime(), formatter);
            return due.toLocalDate().equals(LocalDateTime.now().toLocalDate());
        } catch (Exception e) { return false; }
    }
    private boolean isUpcoming(Task task) {
        try {
            LocalDateTime due = LocalDateTime.parse(task.getReminderTime(), formatter);
            return due.isAfter(LocalDateTime.now()) && !isToday(task);
        } catch (Exception e) { return false; }
    }

    private void showAddTaskDialog() {
        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Add Task");
        dialog.setHeaderText(null);
        TextField titleField = new TextField();
        titleField.setPromptText("Task title");
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Due date");
        TextField timeField = new TextField();
        timeField.setPromptText("HH:mm");
        VBox vbox = new VBox(10, titleField, datePicker, timeField);
        dialog.getDialogPane().setContent(vbox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String title = titleField.getText().trim();
                String time = timeField.getText().trim();
                if (!title.isEmpty() && datePicker.getValue() != null && !time.isEmpty()) {
                    try {
                        LocalDateTime dt = LocalDateTime.parse(datePicker.getValue() + " " + time, formatter);
                        return new Task(title, false, dt.format(formatter));
                    } catch (Exception ignored) {}
                }
            }
            return null;
        });
        dialog.showAndWait().ifPresent(task -> {
            taskManager.addTask(task);
            updateFilter(currentFilter);
        });
    }

    private void startReminderChecker() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                for (Task task : taskManager.getTasks()) {
                    if (!task.isCompleted() && !task.getReminderTime().isEmpty()) {
                        try {
                            LocalDateTime taskTime = LocalDateTime.parse(task.getReminderTime(), formatter);
                            if (taskTime.isBefore(LocalDateTime.now().plusSeconds(1)) &&
                                taskTime.isAfter(LocalDateTime.now().minusSeconds(5))) {
                                playAlarm();
                                sendNotification(task.getText());  // 🪟 Toast notification
                                taskManager.removeTask(task);
                                taskManager.saveTasks();

                            }
                        } catch (Exception ignored) {}
                    }
                }
            }
        }, 0, 5000);
    }

    private void playAlarm() {
        try {
            AudioClip sound = new AudioClip(new File("alarm.wav").toURI().toString());
            sound.play();
        } catch (Exception e) {
            System.err.println("Unable to play alarm sound: " + e.getMessage());
        }
    }

    private void sendNotification(String taskName) {
    try {
        String message = "Task Reminder: " + taskName + " is due now!";
        String command = "powershell -ExecutionPolicy Bypass -Command \"Import-Module BurntToast; New-BurntToastNotification -Text 'Reminder', '" + message + "'\"";
        Runtime.getRuntime().exec(command);
    } catch (Exception e) {
        System.err.println("Notification failed: " + e.getMessage());
    }
}



    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void displayTasks() {
        taskList.getChildren().clear();
        for (Task task : taskManager.getTasks()) {
            if (task.isExpired()) continue; // ⛔ Skip expired tasks
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            CheckBox checkBox = new CheckBox();
            checkBox.setSelected(task.isCompleted());
            checkBox.setOnAction(e -> {
                task.setCompleted(checkBox.isSelected());
                taskManager.saveTasks();
            });

            Label label = new Label(task.getText() + " [" + task.getReminderTime() + "]");
            label.setStyle(task.isCompleted() ? "-fx-strikethrough: true;" : "");

            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(e -> {
                taskManager.removeTask(task);
                displayTasks();
            });

            row.getChildren().addAll(checkBox, label, deleteButton);
            taskList.getChildren().add(row);
        }
    }

    // Custom ListCell for Task
    private class TaskCell extends ListCell<Task> {
        @Override
        protected void updateItem(Task task, boolean empty) {
            super.updateItem(task, empty);
            if (empty || task == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                CheckBox checkBox = new CheckBox();
                checkBox.setSelected(task.isCompleted());
                checkBox.setOnAction(e -> {
                    task.setCompleted(checkBox.isSelected());
                    taskManager.saveTasks();
                    updateFilter(currentFilter);
                });
                Label label = new Label(task.getText());
                if (task.isCompleted()) {
                    label.setStyle("-fx-strikethrough: true; -fx-opacity: 0.5;");
                }
                Label dueLabel = new Label(task.getReminderTime());
                dueLabel.setStyle("-fx-text-fill: #888;");
                row.getChildren().addAll(checkBox, label, dueLabel);
                setGraphic(row);
            }
        }
    }

    private void showSearchDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Task");
        dialog.setHeaderText("Enter task name to search:");
        dialog.setContentText("Task:");
        dialog.showAndWait().ifPresent(query -> {
            observableTasks.setAll(taskManager.getTasks().stream()
                .filter(task -> task.getText().toLowerCase().contains(query.toLowerCase()))
                .toList());
        });
    }

    private void showFilterDialog() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("All", "All", "Today", "Upcoming", "Completed");
        dialog.setTitle("Filter Tasks");
        dialog.setHeaderText("Select a category to filter:");
        dialog.setContentText("Category:");
        dialog.showAndWait().ifPresent(this::updateFilter);
    }

    // Login/Registration dialog
    private boolean showLoginDialog(UserManager userManager) {
        while (true) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Login or Register");
            dialog.setHeaderText("Please log in or register to continue");
            ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
            ButtonType registerButtonType = new ButtonType("Register", ButtonBar.ButtonData.OTHER);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, registerButtonType, ButtonType.CANCEL);
            TextField usernameField = new TextField();
            usernameField.setPromptText("Username");
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Password");
            VBox vbox = new VBox(10, usernameField, passwordField);
            dialog.getDialogPane().setContent(vbox);
            Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
            loginButton.addEventFilter(ActionEvent.ACTION, event -> {
                String user = usernameField.getText().trim();
                String pass = passwordField.getText();
                if (!userManager.authenticate(user, pass)) {
                    showAlert("Login Failed", "Invalid username or password.");
                    event.consume();
                }
            });
            dialog.setResultConverter(btn -> {
                if (btn == registerButtonType) {
                    String user = usernameField.getText().trim();
                    String pass = passwordField.getText();
                    if (user.isEmpty() || pass.isEmpty()) {
                        showAlert("Registration Failed", "Username and password required.");
                        return null;
                    }
                    if (!userManager.register(user, pass)) {
                        showAlert("Registration Failed", "Username already exists.");
                        return null;
                    }
                    showAlert("Registration Success", "You can now log in.");
                    return null; // Stay in dialog for login
                }
                return btn;
            });
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == loginButtonType) {
                return true;
            } else if (!result.isPresent() || result.get() == ButtonType.CANCEL) {
                return false;
            }
            // If registration, loop again for login
        }
    }

    // Helper to get the logged-in username (for demo, returns last entered username)
    private String getLoggedInUsername() {
        // You can store the username in a field after successful login for real use
        return "User";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
