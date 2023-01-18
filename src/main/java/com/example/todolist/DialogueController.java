package com.example.todolist;

import com.example.todolist.datamodel.TodoData;
import com.example.todolist.datamodel.TodoItem;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogueController {

    public TextField shortDescriptionField;
    public TextArea detailsArea;
    public DatePicker deadlinePicker;

    @FXML
    public TodoItem processResult(){
        String shortDescription = shortDescriptionField.getText().trim();
        String details = detailsArea.getText().trim();
        LocalDate deadlineValue = deadlinePicker.getValue();
        if (shortDescription.isEmpty() || details.isEmpty() || deadlineValue == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Please Input all the fields and add date!");
            alert.show();
            return null;
        }
        TodoItem newItem = new TodoItem(shortDescription, details, deadlineValue);
        TodoData.getInstance().addTodoItem(newItem);
        return newItem;
    }

}
