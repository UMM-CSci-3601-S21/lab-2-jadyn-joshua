package umm3601.todos;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import io.javalin.http.BadRequestResponse;

/**
 * A fake "database" of user info
 * <p>
 * Since we don't want to complicate this lab with a real database, we're going
 * to instead just read a bunch of user data from a specified JSON file, and
 * then provide various database-like methods that allow the `UserController` to
 * "query" the "database".
 */
public class TodosDatabase {

  private Todos[] allTodos;

  public TodosDatabase(String todosDataFile) throws IOException {
    Gson gson = new Gson();
    InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(todosDataFile));
    allTodos = gson.fromJson(reader, Todos[].class);
  }

  public int size() {
    return allTodos.length;
  }


  /**
   * Get an array of all the users satisfying the queries in the params.
   *
   * @param queryParams map of key-value pairs for the query
   * @return an array of all the users matching the given criteria
   */
  public Todos[] listTodos(Map<String, List<String>> queryParams) {
    Todos[] filteredTodos = allTodos;



    return filteredTodos;
  }





}





