
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task implements Serializable {
    private String text;
    private boolean completed;
    private String reminderTime;

    public Task(String text, boolean completed, String reminderTime) {
        this.text = text;
        this.completed = completed;
        this.reminderTime = reminderTime;
    }
    
    public boolean isExpired() {
    if (reminderTime == null || reminderTime.isEmpty()) return false;
    try {
        LocalDateTime due = LocalDateTime.parse(reminderTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return due.isBefore(LocalDateTime.now());
    } catch (Exception e) {
        return false;
    }
}

    public String getText() { return text; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean c) { completed = c; }
    public String getReminderTime() { return reminderTime; }

    @Override
    public String toString() {
        return text + "::" + completed + "::" + reminderTime;
    }

    public static Task fromString(String line) {
        String[] parts = line.split("::");
        String reminder = parts.length > 2 ? parts[2] : "";
        return new Task(parts[0], Boolean.parseBoolean(parts[1]), reminder);
    }
}
