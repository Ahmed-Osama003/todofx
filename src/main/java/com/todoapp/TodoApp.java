package com.todoapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.todoapp.model.Todo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TodoApp extends Application {
    
    private ObservableList<Todo> todoList;
    private ListView<Todo> listView;
    private TextField taskInput;
    
    @Override
    public void start(Stage primaryStage) {
        todoList = FXCollections.observableArrayList();
        
        createComponents();
        
        VBox root = createLayout();
        
        Scene scene = new Scene(root, 500, 600);
        
        primaryStage.setTitle("JavaFX Todo App");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    private void createComponents() {
        taskInput = new TextField();
        taskInput.setPromptText("Enter a new task...");
        taskInput.setPrefHeight(40);
        
        listView = new ListView<>(todoList);
        listView.setPrefHeight(400);
        
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Todo selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    toggleTodoCompletion(selected);
                }
            }
        });
        
        taskInput.setOnAction(e -> addTodo());
    }
    
    private VBox createLayout() {
        Label titleLabel = new Label("My Todo List");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER);
        
        Button addButton = new Button("Add Task");
        addButton.setOnAction(e -> addTodo());
        addButton.setPrefHeight(40);
        
        inputBox.getChildren().addAll(taskInput, addButton);
        HBox.setHgrow(taskInput, Priority.ALWAYS);
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button completeButton = new Button("Mark Complete");
        Button deleteButton = new Button("Delete");
        Button clearAllButton = new Button("Clear All");
        
        completeButton.setOnAction(e -> toggleSelectedTodo());
        deleteButton.setOnAction(e -> deleteSelectedTodo());
        clearAllButton.setOnAction(e -> clearAllTodos());
        
        buttonBox.getChildren().addAll(completeButton, deleteButton, clearAllButton);
        
        Label statusLabel = new Label("Double-click a task to toggle completion");
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.getChildren().addAll(titleLabel, inputBox, listView, buttonBox, statusLabel);
        
        return root;
    }
    
    private void addTodo() {
        String task = taskInput.getText().trim();
        if (!task.isEmpty()) {
            Todo newTodo = new Todo(task);
            todoList.add(newTodo);
            taskInput.clear();
            taskInput.requestFocus();
        }
    }
    
    private void toggleSelectedTodo() {
        Todo selected = listView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            toggleTodoCompletion(selected);
        }
    }
    
    private void toggleTodoCompletion(Todo todo) {
        todo.setCompleted(!todo.isCompleted());
        listView.refresh(); // Refresh to update display
    }
    
    private void deleteSelectedTodo() {
        Todo selected = listView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            todoList.remove(selected);
        }
    }
    
    private void clearAllTodos() {
        if (!todoList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Clear All Tasks");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("This will delete all tasks. This action cannot be undone.");
            
            if (alert.showAndWait().get() == ButtonType.OK) {
                todoList.clear();
            }
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}