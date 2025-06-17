package com.todoapp.database;

import com.todoapp.model.Todo;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TodoDAO {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static void insertTodo(Todo todo) {
        String sql = "INSERT INTO todos (task, completed, created_at, due_date, category) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, todo.getTask());
            stmt.setBoolean(2, todo.isCompleted());
            stmt.setString(3, todo.getCreatedAt().format(formatter));
            stmt.setString(4, todo.getDueDate() != null ? todo.getDueDate().format(formatter) : null);
            stmt.setString(5, todo.getCategory());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
