import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.*;
import java.util.*;

public class UserManager {
    private static final String USER_FILE = "users.txt";
    private Map<String, String> users = new HashMap<>();
    private String lastLoggedInUser = null;

    public UserManager() {
        loadUsers();
        // DEBUG: Force a test user to verify file writing
        if (!users.containsKey("testuser")) {
            users.put("testuser", hash("testpass"));
            saveUsers();
        }
    }

    public boolean register(String username, String password) {
        if (users.containsKey(username)) return false;
        users.put(username, hash(password));
        saveUsers();
        return true;
    }

    public boolean authenticate(String username, String password) {
        boolean ok = users.containsKey(username) && users.get(username).equals(hash(password));
        if (ok) lastLoggedInUser = username;
        return ok;
    }

    public String getLastLoggedInUser() {
        return lastLoggedInUser;
    }

    private void loadUsers() {
        users.clear();
        File file = new File(USER_FILE);
        System.out.println("[UserManager] Loading users from: " + file.getAbsolutePath());
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) users.put(parts[0], parts[1]);
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    private void saveUsers() {
        File file = new File(USER_FILE);
        System.out.println("[UserManager] Saving users to: " + file.getAbsolutePath());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    private String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
