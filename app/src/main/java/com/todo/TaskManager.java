package com.todo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private final List<Task> tasks = new ArrayList<>();
    private final Path storageFile;

    public TaskManager(String fileName) {
        this.storageFile = Path.of(fileName);
    }

    public List<Task> getAllTasks() {
        return tasks;
    }

    public List<Task> getCompletedTasks() {
        List<Task> completed = new ArrayList<>();
        for (Task t : tasks) {
            if (t.isCompleted()) {
                completed.add(t);
            }
        }
        return completed;
    }

    public void addTask(String title, String description) {
        tasks.add(new Task(title, description));
        save();
    }

    public void updateTask(int index, String newTitle, String newDescription) {
        if (index >= 0 && index < tasks.size()) {
            Task t = tasks.get(index);
            t.setTitle(newTitle);
            t.setDescription(newDescription);
            save();
        }
    }

    public void deleteTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
            save();
        }
    }

    public void markCompleted(int index, boolean completed) {
        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).setCompleted(completed);
            save();
        }
    }

    // ----- File I/O -----

    public void load() {
        tasks.clear();
        if (!Files.exists(storageFile)) {
            return; // nothing to load yet
        }
        try {
            List<String> lines = Files.readAllLines(storageFile);
            for (String line : lines) {
                Task t = Task.fromFileString(line);
                if (t != null) {
                    tasks.add(t);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not load tasks: " + e.getMessage());
        }
    }

    public void save() {
        List<String> lines = new ArrayList<>();
        for (Task t : tasks) {
            lines.add(t.toFileString());
        }
        try {
            Files.write(storageFile, lines);
        } catch (IOException e) {
            System.err.println("Could not save tasks: " + e.getMessage());
        }
    }
}
