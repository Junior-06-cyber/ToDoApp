import java.io.*;
import java.util.*;

public class TaskManager {
    private static String getProjectRoot() {
        try {
            String path = TaskManager.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File dir = new File(path).getParentFile().getParentFile();
            return dir.getAbsolutePath();
        } catch (Exception e) {
            return System.getProperty("user.dir");
        }
    }
    private final String FILE_NAME = getProjectRoot() + File.separator + "tasks.txt";
    private List<Task> tasks = new ArrayList<>();

    public TaskManager() {
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
        File file = new File(FILE_NAME);
        System.out.println("Saving tasks to: " + file.getAbsolutePath()); // Debug output
        System.out.println("Number of tasks to save: " + tasks.size());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Task task : tasks) {
                System.out.println("Saving task: " + task.toString());
                writer.write(task.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
            javax.swing.JOptionPane.showMessageDialog(null, "Error saving tasks: " + e.getMessage());
        }
    }

    public void loadTasks() {
        tasks.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                tasks.add(Task.fromString(line));
            }
        } catch (IOException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
        }
    }
}
