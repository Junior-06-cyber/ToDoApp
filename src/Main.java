import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.*;
import javafx.scene.media.AudioClip;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {
    private TaskManager taskManager = new TaskManager();
    private VBox taskList = new VBox(10);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("My To-Do List");

        TextField taskInput = new TextField();
        taskInput.setPromptText("Enter a new task...");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Reminder Date");

        TextField timeInput = new TextField();
        timeInput.setPromptText("HH:mm");

        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            String text = taskInput.getText().trim();
            String timeText = timeInput.getText().trim();
            LocalDateTime reminderTime = null;

            if (!text.isEmpty() && datePicker.getValue() != null && !timeText.isEmpty()) {
                try {
                    reminderTime = LocalDateTime.parse(datePicker.getValue() + " " + timeText, formatter);
                } catch (Exception ex) {
                    showAlert("Invalid Time Format", "Please use HH:mm format.");
                    return;
                }
                Task task = new Task(text, false, reminderTime.format(formatter));
                taskManager.addTask(task); // addTask already saves
                displayTasks();
                taskInput.clear();
                timeInput.clear();
                datePicker.setValue(null);
            } else {
                showAlert("Missing Input", "Please fill all fields.");
            }
        });

        HBox inputBox = new HBox(10, taskInput, datePicker, timeInput, addButton);
        inputBox.setAlignment(Pos.CENTER);

        taskList.setPadding(new Insets(10));
        displayTasks();

        ScrollPane scrollPane = new ScrollPane(taskList);
        scrollPane.setFitToWidth(true);

        VBox layout = new VBox(10, inputBox, scrollPane);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 600, 600);
        scene.getStylesheets().add("styles.css");

        primaryStage.setScene(scene);
        primaryStage.show();

        startReminderChecker();
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

    public static void main(String[] args) {
        launch(args);
    }
}
