import java.io.*;
import java.util.*;

public class TaskManager {
    private String fileName;
    private List<Task> tasks = new ArrayList<>();

    public TaskManager(String username) {
        if (username == null || username.isEmpty()) username = "guest";
        this.fileName = "tasks_" + username.toLowerCase() + ".txt";
        loadTasks();
    }

    public void setUser(String username) {
        this.fileName = "tasks_" + username.toLowerCase() + ".txt";
        loadTasks();
    }

    public void addTask(Task task) {
        tasks.add(task);
        saveTasks();
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        saveTasks();
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void saveTasks() {
        File file = new File(fileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Task task : tasks) {
                writer.write(task.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    public void loadTasks() {
        tasks.clear();
        File file = new File(fileName);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                tasks.add(Task.fromString(line));
            }
        } catch (IOException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
        }
    }
}
