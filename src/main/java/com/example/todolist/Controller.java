package com.example.todolist;

import com.example.todolist.datamodel.TodoData;
import com.example.todolist.datamodel.TodoItem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Controller {
    public BorderPane mainBorderPane;
    public Button createItems;

    @FXML
    private Label deadlineLabel;
    @FXML
    private TextArea itemDetailsTextArea;
    @FXML
    private ListView<TodoItem> todoListView;

    public void initialize(){
        ContextMenu listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        MenuItem editItem = new MenuItem("Edit");
        deleteMenuItem.setOnAction(e ->{
            TodoItem item = todoListView.getSelectionModel().getSelectedItem();
            deleteItem(item);
        });

        editItem.setOnAction(e -> itemEdit());

        listContextMenu.getItems().addAll(deleteMenuItem);
        listContextMenu.getItems().addAll(editItem);
        try {
            TodoData.getInstance().loadTodoItems();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        todoListView.getSelectionModel().selectedItemProperty().addListener((observableValue, todoItem, newValue) ->{
            if (newValue != null){
                TodoItem item = todoListView.getSelectionModel().getSelectedItem();
                itemDetailsTextArea.setText(item.getDetails());
                DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                deadlineLabel.setText(df.format(item.getDeadLine()));
            }
        });

        todoListView.setItems(TodoData.getInstance().getTodoItems());
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoListView.getSelectionModel().selectFirst();
        todoListView.setContextMenu(listContextMenu);

        createItems.setOnAction(e -> {
            todoListView.setItems(TodoData.getInstance().getTodoItems());
            todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            todoListView.getSelectionModel().selectFirst();
            showNewItemDialogue();

        });
    }

    @FXML
    public void itemEdit(){
        TodoItem item = todoListView.getSelectionModel().getSelectedItem();
        openDialog("Edit Todo Item",
                "Use this dialogue to Edit todo item",
                item);
    }

    @FXML
    public void showNewItemDialogue() {
        openDialog("Add New Todo Item",
                "Use this dialogue to create a new todo item",
                null);
    }


    private void openDialog(String title, String headerText, TodoItem item) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialogue.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DialogueController controller = fxmlLoader.getController();
            TodoItem newItem = controller.processResult();
            if (item != null && newItem != null) {
                item.setDetails(newItem.getDetails());
                item.setShortDescription(newItem.getShortDescription());
                item.setDeadLine(newItem.getDeadLine());
                TodoData.getInstance().deleteItem(item);
                todoListView.getSelectionModel().select(newItem);
            } else {
                todoListView.getSelectionModel().select(newItem);
            }
        }
    }

    private void deleteItem(TodoItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Todo Item");
        alert.setHeaderText("Delete item: "+item.getShortDescription());
        alert.setContentText("Are You Sure?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && (result.get() == ButtonType.OK)){
            TodoData.getInstance().deleteItem(item);
        }

    }

    public void handleKeyPressed(KeyEvent keyEvent) {
        TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null){
            if (keyEvent.getCode().equals(KeyCode.DELETE)){
                deleteItem(selectedItem);
            }
        }
    }
}