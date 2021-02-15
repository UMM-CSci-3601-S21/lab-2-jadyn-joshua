package umm3601.todos;

import io.javalin.http.Context;


/**
 * Controller that manages requests for info about todos.
 */
public class TodosController {

  private TodosDatabase database;

  /**
   * Construct a controller for todos.
   * <p>
   * This loads the "database" of todo info from a JSON file and stores that
   * internally so that (subsets of) users can be returned in response to
   * requests.
   *
   * @param database the `Database` containing todos data
   */
  public TodosController(TodosDatabase database) {
    this.database = database;
  }



  /**
   * Get a JSON response with a list of all the todos in the "database".
   *
   * @param ctx a Javalin HTTP context
   */
  public void getTodos(Context ctx) {
    Todos[] todos = database.listTodos(ctx.queryParamMap());
    ctx.json(todos);
  }

}
