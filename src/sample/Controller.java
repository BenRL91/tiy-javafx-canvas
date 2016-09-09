package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import jodd.json.JsonParser;
import jodd.json.JsonSerializer;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller implements Initializable {
    @FXML
    ListView todoList;

    @FXML
    TextField todoText;

    ObservableList<ToDoItem> todoItems = FXCollections.observableArrayList();
    ArrayList<ToDoItem> savableList = new ArrayList<ToDoItem>();
    String fileName = "todos.json";

    public String username;
    private boolean deletable = false;

    ToDoDatabase myTodoDatabase = new ToDoDatabase();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

//        System.out.print("Please enter your name: ");
//        Scanner inputScanner = new Scanner(System.in);
//        username = inputScanner.nextLine();
//
//        if (username != null && !username.isEmpty()) {
//            fileName = username + ".json";
//        }
//
//        System.out.println("Checking existing list ...");
//        ToDoItemList retrievedList = retrieveList();
//        if (retrievedList != null) {
//            for (ToDoItem item : retrievedList.todoItems) {
//                todoItems.add(item);
//            }
//        }

//        todoList.setItems(todoItems);
        try {
            myTodoDatabase.init();
            for (ToDoItem item : myTodoDatabase.selectToDos(myTodoDatabase.getMyConn())) {
                todoItems.add(item);
            }
            todoList.setItems(todoItems);

            System.out.println(todoItems.toString());

        } catch (SQLException SQLEx) {
            SQLEx.printStackTrace();
        }
    }

    public void saveToDoList() {
//        if (todoItems != null && todoItems.size() > 0) {
//            System.out.println("Saving " + todoItems.size() + " items in the list");
//            savableList = new ArrayList<ToDoItem>(todoItems);
//            System.out.println("There are " + savableList.size() + " items in my savable list");
//            saveList();
//        } else {
//            System.out.println("No items in the ToDo List");
//        }
    }

    public void addItem() {
        System.out.println("Adding item ...");

//        todoItems.add(new ToDoItem(todoText.getText()));
        try {
            todoList.setItems(null);
            myTodoDatabase.insertToDo(myTodoDatabase.getMyConn(), todoText.getText());
            todoList.setItems(todoItems);
            System.out.println(todoItems.toString());
            todoItems.clear();
            todoList.setItems(todoItems);
            for (ToDoItem item : myTodoDatabase.selectToDos(myTodoDatabase.getMyConn())) {
                todoItems.add(item);
            }
            todoList.setItems(todoItems);
            todoText.setText("");

        } catch (SQLException SQLEx) {
            SQLEx.printStackTrace();
        }

    }

    public void removeItem() {
        ToDoItem todoItem = (ToDoItem) todoList.getSelectionModel().getSelectedItem();
        System.out.println("Removing " + todoItem.text + " ...");
        todoItems.remove(todoItem);
    }

    public void toggleOrDeleteItem() {
        if (deletable) {
            System.out.println("Deleting item ...");
            ToDoItem deleteableItem = (ToDoItem) todoList.getSelectionModel().getSelectedItem();
            if (deleteableItem != null) {
                try {
                    myTodoDatabase.deleteToDo(myTodoDatabase.getMyConn(), deleteableItem.getId());
                        todoItems.remove(deleteableItem);
                        System.out.println(todoItems.toString());
                    } catch (SQLException SQLEx) {
                        SQLEx.printStackTrace();
                    }
            }
        } else {
                System.out.println("Toggling item ...");
                ToDoItem todoItem = (ToDoItem) todoList.getSelectionModel().getSelectedItem();
                if (todoItem != null) {
                    todoItem.isDone = !todoItem.isDone;
                    todoList.setItems(null);
                    try {
                        myTodoDatabase.toggleToDo(myTodoDatabase.getMyConn(), todoItem.getId());
                        todoList.setItems(todoItems);
                    } catch (SQLException SQLEx) {
                        SQLEx.printStackTrace();
                    }
                }
            }
    }

    public void saveList() {
        try {

            // write JSON
            JsonSerializer jsonSerializer = new JsonSerializer().deep(true);
            String jsonString = jsonSerializer.serialize(new ToDoItemList(todoItems));

            System.out.println("JSON = ");
            System.out.println(jsonString);

            File sampleFile = new File(fileName);
            FileWriter jsonWriter = new FileWriter(sampleFile);
            jsonWriter.write(jsonString);
            jsonWriter.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public ToDoItemList retrieveList () throws SQLException {
//        try {
//
//            Scanner fileScanner = new Scanner(new File(fileName));
//            fileScanner.useDelimiter("\\Z"); // read the input until the "end of the input" delimiter
//            String fileContents = fileScanner.next();
//            JsonParser ToDoItemParser = new JsonParser();
//
//            ToDoItemList theListContainer = ToDoItemParser.parse(fileContents, ToDoItemList.class);
//            System.out.println("==============================================");
//            System.out.println("        Restored previous ToDoItem");
//            System.out.println("==============================================");
//            return theListContainer;
//        } catch (IOException ioException) {
//            // if we can't find the file or run into an issue restoring the object
//            // from the file, just return null, so the caller knows to create an object from scratch
//            return null;
//        }
//    }
            return new ToDoItemList(myTodoDatabase.selectToDos(myTodoDatabase.getMyConn()));

    }


    public void deleteTodo(ActionEvent actionEvent) {
        deletable = !deletable;
    }
}
