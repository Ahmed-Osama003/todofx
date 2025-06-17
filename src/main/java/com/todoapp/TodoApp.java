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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TodoApp extends Application {

    private ObservableList<Todo> todoList;
    private ObservableList<Todo> filteredTodoList;
    private ListView<Todo> listView;
    private TextField taskInput;
    private DatePicker dueDatePicker;
    private ComboBox<String> categoryComboBox;
    private ComboBox<String> filterComboBox;
    private ObservableList<String> categories;

    @Override
    public void start(Stage primaryStage) {
        todoList = FXCollections.observableArrayList();
        filteredTodoList = FXCollections.observableArrayList();

        // Initialize categories with some default values
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

        // Setup category selection for new tasks
        categoryComboBox = new ComboBox<>(categories);
        categoryComboBox.getItems().remove("All Categories"); // Remove "All Categories" from task creation
        categoryComboBox.setPromptText("Select category");
        categoryComboBox.setValue("Uncategorized");
        categoryComboBox.setPrefHeight(40);
        categoryComboBox.setEditable(true); // Allow users to add custom categories

        // Setup category filter
        filterComboBox = new ComboBox<>(categories);
        filterComboBox.setValue("All Categories");
        filterComboBox.setPrefHeight(40);
        filterComboBox.setOnAction(e -> filterTodosByCategory());

        // Initialize filtered list with all todos
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

        // Filter section
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER);

        Label filterLabel = new Label("Filter by Category:");
        filterLabel.setPrefHeight(30);
        filterLabel.setAlignment(Pos.CENTER_LEFT);

        filterBox.getChildren().addAll(filterLabel, filterComboBox);
        HBox.setHgrow(filterComboBox, Priority.ALWAYS);

        // Task input section
        VBox inputSection = new VBox(10);
        inputSection.setAlignment(Pos.CENTER);

        HBox taskInputBox = new HBox(10);
        taskInputBox.setAlignment(Pos.CENTER);

        Button addButton = new Button("Add Task");
        addButton.setOnAction(e -> addTodo());
        addButton.setPrefHeight(40);

        taskInputBox.getChildren().addAll(taskInput, addButton);
        HBox.setHgrow(taskInput, Priority.ALWAYS);

        // Due date section
        HBox dueDateBox = new HBox(10);
        dueDateBox.setAlignment(Pos.CENTER);

        Label dueDateLabel = new Label("Due Date:");
        dueDateLabel.setPrefHeight(40);
        dueDateLabel.setAlignment(Pos.CENTER_LEFT);

        dueDateBox.getChildren().addAll(dueDateLabel, dueDatePicker);
        HBox.setHgrow(dueDatePicker, Priority.ALWAYS);

        // Category section
        HBox categoryBox = new HBox(10);
        categoryBox.setAlignment(Pos.CENTER);

        Label categoryLabel = new Label("Category:");
        categoryLabel.setPrefHeight(40);
        categoryLabel.setAlignment(Pos.CENTER_LEFT);

        categoryBox.getChildren().addAll(categoryLabel, categoryComboBox);
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

        Label statusLabel = new Label("Double-click a task to toggle completion");
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);
        root.getChildren().addAll(titleLabel, filterBox, inputSection, listView, buttonBox, statusLabel);

        return root;
    }

    private void addTodo() {
        String task = taskInput.getText().trim();
        if (!task.isEmpty()) {
            Todo newTodo;

            // Get the selected category
            String category = categoryComboBox.getValue();
            if (category == null || category.trim().isEmpty()) {
                category = "Uncategorized";
            }

            // Add the category to the list if it's not already there
            if (!categories.contains(category)) {
                categories.add(category);
            }

            // Check if a due date was selected
            LocalDate selectedDate = dueDatePicker.getValue();
            if (selectedDate != null) {
                // Convert LocalDate to LocalDateTime (set time to end of day: 23:59:59)
                LocalDateTime dueDateTime = LocalDateTime.of(selectedDate, LocalTime.of(23, 59, 59));
                newTodo = new Todo(task, dueDateTime, category);
            } else {
                newTodo = new Todo(task, category);
            }

            todoList.add(newTodo);

            // Apply filter to update the view
            filterTodosByCategory();

            taskInput.clear();
            dueDatePicker.setValue(null); // Clear the date picker
            // Don't reset the category - keep it for the next task
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
        filterTodosByCategory(); // Apply filter to update the view
    }

    private void deleteSelectedTodo() {
        Todo selected = listView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            todoList.remove(selected);
            filterTodosByCategory(); // Apply filter to update the view
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
                filterTodosByCategory(); // Apply filter to update the view
            }
        }
    }

    private void setDueDateForSelectedTodo() {
        Todo selected = listView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Create a dialog for setting the due date
            Dialog<LocalDate> dialog = new Dialog<>();
            dialog.setTitle("Set Due Date");
            dialog.setHeaderText("Set a due date for: " + selected.getTask());

            // Set up the buttons
            ButtonType setButtonType = new ButtonType("Set", ButtonBar.ButtonData.OK_DONE);
            ButtonType clearButtonType = new ButtonType("Clear Due Date", ButtonBar.ButtonData.LEFT);
            ButtonType cancelButtonType = ButtonType.CANCEL;
            dialog.getDialogPane().getButtonTypes().addAll(setButtonType, clearButtonType, cancelButtonType);

            // Create the date picker
            DatePicker datePicker = new DatePicker();
            if (selected.hasDueDate()) {
                datePicker.setValue(selected.getDueDate().toLocalDate());
            }

            // Set up the dialog content
            VBox content = new VBox(10);
            content.getChildren().add(datePicker);
            dialog.getDialogPane().setContent(content);

            // Convert the result
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == setButtonType) {
                    return datePicker.getValue();
                } else if (dialogButton == clearButtonType) {
                    return null;
                }
                return null;
            });

            // Show the dialog and process the result
            dialog.showAndWait().ifPresent(date -> {
                if (date == null) {
                    // Clear the due date
                    selected.setDueDate(null);
                } else {
                    // Set the due date (end of day)
                    LocalDateTime dueDateTime = LocalDateTime.of(date, LocalTime.of(23, 59, 59));
                    selected.setDueDate(dueDateTime);
                }
                filterTodosByCategory(); // Apply filter to update the view
            });
        }
    }

    private void filterTodosByCategory() {
        String selectedCategory = filterComboBox.getValue();

        // Clear the filtered list
        filteredTodoList.clear();

        if (selectedCategory == null || selectedCategory.equals("All Categories")) {
            // Show all tasks
            filteredTodoList.addAll(todoList);
        } else {
            // Filter tasks by the selected category
            for (Todo todo : todoList) {
                if (todo.getCategory().equals(selectedCategory)) {
                    filteredTodoList.add(todo);
                }
            }
        }
    }

    private void setCategoryForSelectedTodo() {
        Todo selected = listView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Create a dialog for setting the category
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Set Category");
            dialog.setHeaderText("Set a category for: " + selected.getTask());

            // Set up the buttons
            ButtonType setButtonType = new ButtonType("Set", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButtonType = ButtonType.CANCEL;
            dialog.getDialogPane().getButtonTypes().addAll(setButtonType, cancelButtonType);

            // Create the category combo box
            ComboBox<String> categoryPicker = new ComboBox<>(categories);
            categoryPicker.setEditable(true);
            categoryPicker.setValue(selected.getCategory());

            // Set up the dialog content
            VBox content = new VBox(10);
            content.getChildren().add(categoryPicker);
            dialog.getDialogPane().setContent(content);

            // Convert the result
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == setButtonType) {
                    return categoryPicker.getValue();
                }
                return null;
            });

            // Show the dialog and process the result
            dialog.showAndWait().ifPresent(category -> {
                if (category != null && !category.trim().isEmpty()) {
                    // Add the category to the list if it's not already there
                    if (!categories.contains(category)) {
                        categories.add(category);
                    }

                    // Set the category
                    selected.setCategory(category);
                    filterTodosByCategory(); // Apply filter to update the view
                }
            });
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
