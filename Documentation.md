# JavaFX To-Do App with User Authentication and Alarms

## Overview
This is a modern, feature-rich To-Do List application built with JavaFX. It supports user authentication, per-user persistent task storage, reminders with alarms, and a beautiful, customizable UI. Users can use the app as a guest or log in/register for a personalized experience.

---

## Features
- **Modern JavaFX UI**: Responsive, attractive interface with a sidebar for navigation and task management.
- **User Authentication**: Register and log in with a username and password (passwords are securely hashed).
- **Guest Mode**: Use the app without logging in; guest tasks are stored separately.
- **Per-User Task Storage**: Each user (including guest) has their own task file (e.g., `tasks_guest.txt`, `tasks_john.txt`).
- **Task Scheduling**: Add tasks with due date and time.
- **Reminders & Alarms**: Receive audio and toast notifications when a task is due.
- **Task Filters**: View tasks by Today, Upcoming, or Completed.
- **Sidebar Customization**: Sizable sidebar, vibrant color scheme, and user status display.
- **Persistent Storage**: All data is stored locally in plain text files.

---

## Technologies Used
- **Java 8+**: Core language.
- **JavaFX**: UI framework for building the desktop app.
- **SHA-256**: For password hashing (in `UserManager`).
- **File I/O**: For reading/writing user and task data.
- **AudioClip**: For playing alarm sounds.
- **CSS**: For custom UI styling (`styles.css`).

---

## Project Structure
```
ToDoApp_With_Alarm/
│
├── ToDoApp/
│   ├── src/
│   │   ├── Main.java           // Main application and UI logic
│   │   ├── Task.java           // Task model
│   │   ├── TaskManager.java    // Handles per-user task storage
│   │   ├── UserManager.java    // Handles user registration/login
│   ├── resources/
│   │   ├── styles.css          // Custom CSS for UI
│   │   ├── search.png, filter.png // Sidebar icons
│   ├── alarm.wav               // Alarm sound
│   ├── fixed_run_todo_with_alarm.bat // Build/run script
│   ├── users.txt               // User credentials (hashed)
│   ├── tasks_guest.txt         // Guest tasks
│   ├── tasks_<username>.txt    // Per-user tasks
```

---

## Main Components
### Main.java
- JavaFX Application entry point.
- Handles UI layout, sidebar, task list, dialogs, and user switching.
- Loads and saves tasks via `TaskManager`.
- Shows user status and provides login/logout functionality.

### Task.java
- Represents a single to-do task.
- Fields: text, completed, reminderTime.
- Serialization/deserialization for file storage.

### TaskManager.java
- Manages tasks for the current user.
- Loads/saves tasks from/to `tasks_<username>.txt`.
- Supports switching users and updating the task list.

### UserManager.java
- Handles user registration and authentication.
- Stores credentials in `users.txt` (hashed passwords).
- Tracks the last logged-in user for UI updates.

### styles.css
- Custom CSS for a modern, vibrant UI.
- Styles sidebar, buttons, task list, and user label.

---

## How to Run
1. **Requirements:** Java 8+ and JavaFX SDK.
2. **Build/Run:** Use `fixed_run_todo_with_alarm.bat` to compile, copy resources, and launch the app.
3. **Resources:** Ensure `styles.css`, `alarm.wav`, and icon PNGs are in the correct folders.
4. **Usage:**
   - Use as Guest or log in/register for a personal task list.
   - Add, complete, and delete tasks.
   - Receive reminders for due tasks.

---

## Security Notes
- Passwords are hashed with SHA-256 before storage.
- All data is stored locally; for more security, consider encrypting files or using a database.

---

## Credits
- JavaFX, OpenJFX
- Icons and alarm sound: user-supplied or open source

---

## Customization
- Edit `styles.css` for color and layout changes.
- Replace `alarm.wav` or sidebar icons for a personalized look.
- Extend `TaskManager` and `UserManager` for more advanced features.

---

Enjoy your modern, secure, and customizable JavaFX To-Do app!
