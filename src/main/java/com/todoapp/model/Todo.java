package com.todoapp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Todo {
    private String task;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;

    public Todo(String task) {
        this.task = task;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
        this.dueDate = null; // No due date by default
    }

    public Todo(String task, LocalDateTime dueDate) {
        this.task = task;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
        this.dueDate = dueDate;
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
            return String.format("%s %s (Created: %s, Due: %s)", status, task, getFormattedDate(), getFormattedDueDate());
        } else {
            return String.format("%s %s (Created: %s)", status, task, getFormattedDate());
        }
    }
}
