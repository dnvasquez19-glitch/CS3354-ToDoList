package com.todo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainUI extends JFrame {
    private final TaskManager taskManager;
    private final DefaultListModel<Task> listModel;
    private final JList<Task> taskList;
    private final JTextField titleField;
    private final JTextArea descriptionArea;

    public MainUI() {
        super("CS3354 To-Do List");

        // where tasks are stored
        this.taskManager = new TaskManager("tasks.txt");
        this.taskManager.load();

        this.listModel = new DefaultListModel<>();
        for (Task t : taskManager.getAllTasks()) {
            listModel.addElement(t);
        }

        this.taskList = new JList<>(listModel);
        this.taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.titleField = new JTextField();
        this.descriptionArea = new JTextArea(4, 20);

        buildLayout();
        attachEvents();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setSize(650, 400);
        setLocationRelativeTo(null); // center on screen
    }

    private void buildLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left: task list
        JScrollPane listScroll = new JScrollPane(taskList);
        mainPanel.add(listScroll, BorderLayout.CENTER);

        // Right: form + buttons
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        formPanel.add(descScroll, gbc);

        rightPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 5, 5));

        JButton addButton = new JButton("Add Task");
        JButton updateButton = new JButton("Update Task");
        JButton completeButton = new JButton("Toggle Complete");
        JButton deleteButton = new JButton("Delete Task");
        JButton showCompletedButton = new JButton("Show Completed");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(completeButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(showCompletedButton); // NEW BUTTON

        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(rightPanel, BorderLayout.EAST);
        setContentPane(mainPanel);

        // Event handlers
        addButton.addActionListener(e -> onAdd());
        updateButton.addActionListener(e -> onUpdate());
        completeButton.addActionListener(e -> onToggleComplete());
        deleteButton.addActionListener(e -> onDelete());
        showCompletedButton.addActionListener(e -> onShowCompleted()); // NEW HANDLER
    }

    private void attachEvents() {
        // Load selected task into fields when clicked
        taskList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Task selected = taskList.getSelectedValue();
                if (selected != null) {
                    titleField.setText(selected.getTitle());
                    descriptionArea.setText(selected.getDescription());
                }
            }
        });

        // Save on close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                taskManager.save();
                dispose();
                System.exit(0);
            }
        });
    }

    private void onAdd() {
        String title = titleField.getText().trim();
        String desc = descriptionArea.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        taskManager.addTask(title, desc);
        refreshList();
        clearFields();
    }

    private void onUpdate() {
        int index = taskList.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Select a task to update.",
                    "Update Task", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String title = titleField.getText().trim();
        String desc = descriptionArea.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        taskManager.updateTask(index, title, desc);
        refreshList();
    }

    private void onToggleComplete() {
        int index = taskList.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Select a task first.",
                    "Toggle Complete", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Task t = taskList.getSelectedValue();
        boolean newStatus = !t.isCompleted();
        taskManager.markCompleted(index, newStatus);
        refreshList();
    }

    private void onDelete() {
        int index = taskList.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Select a task to delete.",
                    "Delete Task", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete selected task?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            taskManager.deleteTask(index);
            refreshList();
            clearFields();
        }
    }

    // ⭐ NEW METHOD — Show Completed Tasks ⭐
    private void onShowCompleted() {
        java.util.List<Task> completed = taskManager.getCompletedTasks();

        if (completed.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No completed tasks yet.",
                    "Completed Tasks",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DefaultListModel<String> completedListModel = new DefaultListModel<>();
        for (Task t : completed) {
            completedListModel.addElement(t.getTitle() + " - " + t.getDescription());
        }

        JList<String> list = new JList<>(completedListModel);
        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(300, 200));

        JOptionPane.showMessageDialog(this, scroll, "Completed Tasks", JOptionPane.PLAIN_MESSAGE);
    }

    private void refreshList() {
        listModel.clear();
        for (Task t : taskManager.getAllTasks()) {
            listModel.addElement(t);
        }
    }

    private void clearFields() {
        titleField.setText("");
        descriptionArea.setText("");
        taskList.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainUI ui = new MainUI();
            ui.setVisible(true);
        });
    }
}
