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
    public static List<Todo> getAllTodos() {
        List<Todo> todos = new ArrayList<>();
        String sql = "SELECT * FROM todos";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Todo todo = new Todo(rs.getString("task"),
                        rs.getString("category"));
                todo.setCompleted(rs.getBoolean("completed"));
                todo.setId(rs.getInt("id"));
                todo.setDueDate(rs.getString("due_date") != null ?
                        LocalDateTime.parse(rs.getString("due_date"), formatter) : null);
                todos.add(todo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return todos;
    }

    public static void updateTodo(Todo todo) {
        String sql = "UPDATE todos SET task=?, completed=?, due_date=?, category=? WHERE id=?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, todo.getTask());
            stmt.setBoolean(2, todo.isCompleted());
            stmt.setString(3, todo.getDueDate() != null ? todo.getDueDate().format(formatter) : null);
            stmt.setString(4, todo.getCategory());
            stmt.setInt(5, todo.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteTodo(int id) {
        String sql = "DELETE FROM todos WHERE id=?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAll() {
        String sql = "DELETE FROM todos";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
