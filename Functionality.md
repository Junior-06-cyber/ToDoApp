Upgrade this JavaFX To-Do List app to look and behave more like the Todoist app:

UI Goals:
- Use a clean, modern layout with a sidebar for navigation and a main task panel.
- Sidebar should include filters: "Inbox", "Today", "Upcoming", and "Completed".
- Main panel should list tasks with checkboxes, titles, and optional due dates.
- Add a "+" floating button in the bottom-right corner for adding new tasks.
- Add support for scheduling tasks (date/time picker).
- Completed tasks should appear faded with strikethrough styling.

Technical Goals:
- Store all tasks locally in a file (`tasks.txt` or JSON).
- Use JavaFX `VBox`, `HBox`, `ListView`, and `BorderPane` to structure layout.
- Persist data using TaskManager class.
- Animate transitions between views if possible.
- Separate UI logic from data handling (MVC pattern preferred).

Ensure the UI is visually attractive: clean fonts, padding, spacing, modern colors.

You can use custom CSS (`styles.css`) for styling if needed.
