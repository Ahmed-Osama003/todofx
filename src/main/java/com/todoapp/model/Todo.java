package com.todoapp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Todo {
    private String task;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
    private String category;

    public Todo(String task) {
        this.task = task;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
        this.dueDate = null; // No due date by default
        this.category = "Uncategorized"; // Default category
    }

    public Todo(String task, LocalDateTime dueDate) {
        this.task = task;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
        this.dueDate = dueDate;
        this.category = "Uncategorized"; // Default category
    }

    public Todo(String task, LocalDateTime dueDate, String category) {
        this.task = task;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
        this.dueDate = dueDate;
        this.category = category;
    }

    public Todo(String task, String category) {
        this.task = task;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
        this.dueDate = null;
        this.category = category;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
        return createdAt.format(formatter);
    }

    public String getFormattedDueDate() {
        if (dueDate == null) {
            return "No due date";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
        return dueDate.format(formatter);
    }

    public boolean hasDueDate() {
        return dueDate != null;
    }

    @Override
    public String toString() {
        String status = completed ? "✓" : "○";
        if (hasDueDate()) {
            return String.format("%s [%s] %s (Created: %s, Due: %s)", status, category, task, getFormattedDate(), getFormattedDueDate());
        } else {
            return String.format("%s [%s] %s (Created: %s)", status, category, task, getFormattedDate());
        }
    }
}
