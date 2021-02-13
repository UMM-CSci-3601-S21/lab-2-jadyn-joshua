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

     // Filter limit if defined
     if (queryParams.containsKey("limit")) {
      String limitParam = queryParams.get("limit").get(0);
      try {
        int targetLimit = Integer.parseInt(limitParam);
        filteredTodos = filterTodosByLimit(filteredTodos, targetLimit);
      } catch (NumberFormatException e) {
        throw new BadRequestResponse("Specified limit '" + limitParam + "' can't be parsed to an integer");
      }
    }

    return filteredTodos;
  }

/**
   * Get an array of a certain number of todos based
   *
   * @param todos     the list of todos to filter by limit
   * @param targetLimit the number we want to limit the todos by
   * @return an array of all the todos limited by the target limit
   */
  public Todos[] filterTodosByLimit(Todos[] todos, int targetLimit) {
    return Arrays.copyOfRange(todos, 0, targetLimit);
  }



}





