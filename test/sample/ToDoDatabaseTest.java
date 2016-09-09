package sample;

import com.sun.tools.javac.comp.Todo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by BenLee on 9/8/16.
 */
public class ToDoDatabaseTest {

    static ToDoDatabase todoDatabase = null;

        @Before
        public void setUp() throws Exception {
            if(todoDatabase == null){
                todoDatabase = new ToDoDatabase();
                todoDatabase.init();
            }

        }

        @After
        public void tearDown() throws Exception {
        }

        @Test
        public void testInit() throws Exception {
            // test to make sure we can access the new database
            Connection conn = DriverManager.getConnection("jdbc:h2:./main");
            PreparedStatement todoQuery = conn.prepareStatement("SELECT * FROM todos");
            ResultSet results = todoQuery.executeQuery();
            assertNotNull(results);

        }

    @Test
    public void testInsertToDo() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        String todoText = "UnitTest-ToDo";

        todoDatabase.insertToDo(conn, todoText);

        // make sure we can retrieve the todo we just created
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM todos where text = ?");
        stmt.setString(1, todoText);
        ResultSet results = stmt.executeQuery();
        assertNotNull(results);
        // count the records in results to make sure we get what we expected
        int numResults = 0;
        while (results.next()) {
            numResults++;
        }

        assertEquals(1, numResults);

        todoDatabase.deleteToDo(conn, todoText);

        // make sure there are no more records for our test todo
        results = stmt.executeQuery();
        numResults = 0;
        while (results.next()) {
            numResults++;
        }
        assertEquals(0, numResults);
    }

    @Test
    public void testSelectAllToDos() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        String firstToDoText = "UnitTest-ToDo1";
        String secondToDoText = "UnitTest-ToDo2";

        todoDatabase.insertToDo(conn, firstToDoText);
        todoDatabase.insertToDo(conn, secondToDoText);

        ArrayList<ToDoItem> todos = todoDatabase.selectToDos(conn);
        System.out.println("Found " + todos.size() + " todos in the database");

        assertTrue("There should be at least 2 todos in the database (there are " +
                todos.size() + ")", todos.size() > 1);

        todoDatabase.deleteToDo(conn, firstToDoText);
        todoDatabase.deleteToDo(conn, secondToDoText);
    }

    @Test
    public void testToggleToDo() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        String unitTestTodoToggle = "UnitTestTodo-toggle";
        todoDatabase.insertToDo(conn, unitTestTodoToggle);
        ArrayList<ToDoItem> todosBefore = todoDatabase.selectToDos(conn);
        for(ToDoItem todo : todosBefore){
            if(todo.getText().equals(unitTestTodoToggle)){
                assertFalse(todo.isDone());
                todoDatabase.toggleToDo(conn, todo.getId());
            }
        }
        ArrayList<ToDoItem> todosAfter = todoDatabase.selectToDos(conn);
        for(ToDoItem todo : todosAfter){
            if(todo.getText().equals(unitTestTodoToggle)){
                assertTrue(todo.isDone());
                todoDatabase.deleteToDo(conn, todo.getId());
            }
        }

    }






}