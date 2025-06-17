package com.todoapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.todoapp.model.Todo;
import com.todoapp.database.TodoDAO;
import com.todoapp.database.DatabaseManager;

public class TodoApp extends Application {

    private ObservableList<Todo> todoList;
    private ObservableList<Todo> filteredTodoList;
    private ListView<Todo> listView;
    private TextField taskInput;
    private DatePicker dueDatePicker;
    private ComboBox<String> categoryComboBox;
    private ComboBox<String> filterComboBox;
    private ObservableList<String> categories;

    private TodoDAO todoDAO;

    @Override
    public void start(Stage primaryStage) {
        // Initialize database before any operations
        DatabaseManager.initializeDatabase();
        todoDAO = new TodoDAO();
        List<Todo> loadedTodos = todoDAO.getAllTodos();
        todoList = FXCollections.observableArrayList(loadedTodos);
        filteredTodoList = FXCollections.observableArrayList();

        categories = FXCollections.observableArrayList(
                "All Categories", "Uncategorized", "Work", "Personal", "Shopping", "Health", "Education"
        );

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

        dueDatePicker = new DatePicker();
        dueDatePicker.setPromptText("Set due date (optional)");
        dueDatePicker.setPrefHeight(40);

        categoryComboBox = new ComboBox<>(categories);
        categoryComboBox.getItems().remove("All Categories");
        categoryComboBox.setPromptText("Select category");
        categoryComboBox.setValue("Uncategorized");
        categoryComboBox.setPrefHeight(40);
        categoryComboBox.setEditable(true);

        filterComboBox = new ComboBox<>(categories);
        filterComboBox.setValue("All Categories");
        filterComboBox.setPrefHeight(40);
        filterComboBox.setOnAction(e -> filterTodosByCategory());

        filteredTodoList.setAll(todoList);

        listView = new ListView<>(filteredTodoList);
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

        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER);
        filterBox.getChildren().addAll(new Label("Filter by Category:"), filterComboBox);
        HBox.setHgrow(filterComboBox, Priority.ALWAYS);

        VBox inputSection = new VBox(10);
        inputSection.setAlignment(Pos.CENTER);

        HBox taskInputBox = new HBox(10);
        taskInputBox.setAlignment(Pos.CENTER);
        Button addButton = new Button("Add Task");
        addButton.setOnAction(e -> addTodo());
        addButton.setPrefHeight(40);
        taskInputBox.getChildren().addAll(taskInput, addButton);
        HBox.setHgrow(taskInput, Priority.ALWAYS);

        HBox dueDateBox = new HBox(10);
        dueDateBox.setAlignment(Pos.CENTER);
        dueDateBox.getChildren().addAll(new Label("Due Date:"), dueDatePicker);
        HBox.setHgrow(dueDatePicker, Priority.ALWAYS);

        HBox categoryBox = new HBox(10);
        categoryBox.setAlignment(Pos.CENTER);
        categoryBox.getChildren().addAll(new Label("Category:"), categoryComboBox);
        HBox.setHgrow(categoryComboBox, Priority.ALWAYS);

        inputSection.getChildren().addAll(taskInputBox, dueDateBox, categoryBox);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button completeButton = new Button("Mark Complete");
        Button setDueDateButton = new Button("Set Due Date");
        Button setCategoryButton = new Button("Set Category");
        Button deleteButton = new Button("Delete");
        Button clearAllButton = new Button("Clear All");

        completeButton.setOnAction(e -> toggleSelectedTodo());
        setDueDateButton.setOnAction(e -> setDueDateForSelectedTodo());
        setCategoryButton.setOnAction(e -> setCategoryForSelectedTodo());
        deleteButton.setOnAction(e -> deleteSelectedTodo());
        clearAllButton.setOnAction(e -> clearAllTodos());

        buttonBox.getChildren().addAll(completeButton, setDueDateButton, setCategoryButton, deleteButton, clearAllButton);

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.getChildren().addAll(titleLabel, filterBox, inputSection, listView, buttonBox,
                new Label("Double-click a task to toggle completion"));

        return root;
    }

    private void addTodo() {
        String task = taskInput.getText().trim();
        if (!task.isEmpty()) {
            String category = categoryComboBox.getValue();
            if (category == null || category.trim().isEmpty()) {
                category = "Uncategorized";
            }
            if (!categories.contains(category)) {
                categories.add(category);
            }

            Todo newTodo;
            LocalDate selectedDate = dueDatePicker.getValue();
            if (selectedDate != null) {
                LocalDateTime dueDateTime = LocalDateTime.of(selectedDate, LocalTime.of(23, 59, 59));
                newTodo = new Todo(task, dueDateTime, category);
            } else {
                newTodo = new Todo(task, category);
            }

            todoDAO.insertTodo(newTodo);
            todoList.add(newTodo);
            filterTodosByCategory();
            taskInput.clear();
            dueDatePicker.setValue(null);
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
        todoDAO.updateTodo(todo);
        filterTodosByCategory();
    }

    private void deleteSelectedTodo() {
        Todo selected = listView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            todoDAO.deleteTodo(selected.getId());
            todoList.remove(selected);
            filterTodosByCategory();
        }
    }

    private void clearAllTodos() {
        if (!todoList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Clear All Tasks");
            alert.setHeaderText("Are you sure?");
            alert.setContentText("This will delete all tasks. This action cannot be undone.");
            if (alert.showAndWait().get() == ButtonType.OK) {
                todoDAO.deleteAll();
                todoList.clear();
                filterTodosByCategory();
            }
        }
    }

    private void setDueDateForSelectedTodo() {
        Todo selected = listView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Dialog<LocalDate> dialog = new Dialog<>();
            dialog.setTitle("Set Due Date");
            dialog.setHeaderText("Set a due date for: " + selected.getTask());

            dialog.getDialogPane().getButtonTypes().addAll(
                    new ButtonType("Set", ButtonBar.ButtonData.OK_DONE),
                    new ButtonType("Clear Due Date", ButtonBar.ButtonData.LEFT),
                    ButtonType.CANCEL
            );

            DatePicker datePicker = new DatePicker();
            if (selected.hasDueDate()) {
                datePicker.setValue(selected.getDueDate().toLocalDate());
            }

            dialog.getDialogPane().setContent(new VBox(10, datePicker));

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    return datePicker.getValue();
                } else if (dialogButton.getButtonData() == ButtonBar.ButtonData.LEFT) {
                    return null;
                }
                return null;
            });

            dialog.showAndWait().ifPresent(date -> {
                if (date == null) {
                    selected.setDueDate(null);
                } else {
                    selected.setDueDate(LocalDateTime.of(date, LocalTime.of(23, 59, 59)));
                }
                todoDAO.updateTodo(selected);
                filterTodosByCategory();
            });
        }
    }

    private void setCategoryForSelectedTodo() {
        Todo selected = listView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Set Category");
            dialog.setHeaderText("Set a category for: " + selected.getTask());

            dialog.getDialogPane().getButtonTypes().addAll(
                    new ButtonType("Set", ButtonBar.ButtonData.OK_DONE),
                    ButtonType.CANCEL
            );

            ComboBox<String> categoryPicker = new ComboBox<>(categories);
            categoryPicker.setEditable(true);
            categoryPicker.setValue(selected.getCategory());
            dialog.getDialogPane().setContent(new VBox(10, categoryPicker));

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    return categoryPicker.getValue();
                }
                return null;
            });

            dialog.showAndWait().ifPresent(category -> {
                if (category != null && !category.trim().isEmpty()) {
                    if (!categories.contains(category)) {
                        categories.add(category);
                    }
                    selected.setCategory(category);
                    todoDAO.updateTodo(selected);
                    filterTodosByCategory();
                }
            });
        }
    }

    private void filterTodosByCategory() {
        String selectedCategory = filterComboBox.getValue();
        filteredTodoList.clear();

        if (selectedCategory == null || selectedCategory.equals("All Categories")) {
            filteredTodoList.addAll(todoList);
        } else {
            for (Todo todo : todoList) {
                if (todo.getCategory().equals(selectedCategory)) {
                    filteredTodoList.add(todo);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
