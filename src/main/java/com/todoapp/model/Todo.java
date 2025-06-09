package com.todoapp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Todo {
    private String task;
    private boolean completed;
    private LocalDateTime createdAt;
    
    public Todo(String task) {
        this.task = task;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
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
    
    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
        return createdAt.format(formatter);
    }
    
    @Override
    public String toString() {
        String status = completed ? "✓" : "○";
        return String.format("%s %s (Created: %s)", status, task, getFormattedDate());
    }
}