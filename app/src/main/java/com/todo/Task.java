package com.todo;

public class Task {
    private String title;
    private String description;
    private boolean completed;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.completed = false;
    }

    public Task(String title, String description, boolean completed) {
        this.title = title;
        this.description = description;
        this.completed = completed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    // For showing in the list
    @Override
    public String toString() {
        return (completed ? "[✓] " : "[ ] ") + title;
    }

    // ----- File storage helpers -----

    // Simple format: title|description|completed
    public String toFileString() {
        String safeTitle = title.replace("|", " ");
        String safeDesc = description.replace("|", " ");
        return safeTitle + "|" + safeDesc + "|" + completed;
    }

    public static Task fromFileString(String line) {
        String[] parts = line.split("\\|", 3);
        if (parts.length < 3) {
            return null;
        }
        String title = parts[0];
        String desc = parts[1];
        boolean completed = Boolean.parseBoolean(parts[2]);
        return new Task(title, desc, completed);
    }
}
