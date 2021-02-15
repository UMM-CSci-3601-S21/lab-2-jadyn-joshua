package umm3601.todos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import io.javalin.core.validation.Validator;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;


import umm3601.Server;

/**
 * Tests the logic of the TodosController
 *
 * @throws IOException
 */
public class TodosControllerSpec {

  private Context ctx = mock(Context.class);

  private TodosController TodosController;
  private static TodosDatabase db;

  @BeforeEach
  public void setUp() throws IOException {
    ctx.clearCookieStore();

    db = new TodosDatabase(Server.TODOS_DATA_FILE);
    TodosController = new TodosController(db);
  }

  @Test
  public void GET_to_request_all_todos() throws IOException {
    // Call the method on the mock controller
    TodosController.getTodos(ctx);

    // Confirm that `json` was called with all the todos.
    ArgumentCaptor<Todos[]> argument = ArgumentCaptor.forClass(Todos[].class);
    verify(ctx).json(argument.capture());
    assertEquals(db.size(), argument.getValue().length);
    //test
  }



  @Test
  public void GET_to_request_limit_10_todos() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("limit", Arrays.asList(new String[] { "10" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);
    TodosController.getTodos(ctx);

    // Confirm that there were only 10 todos passed to `json`
    ArgumentCaptor<Todos[]> argument = ArgumentCaptor.forClass(Todos[].class);
    verify(ctx).json(argument.capture());
    assertEquals(10, argument.getValue().length);
  }


  /**
   * Test that if the user sends a request with an illegal value in
   * the limit field (i.e., something that can't be parsed to a number)
   * we get a reasonable error code back.
   */
  @Test
  public void GET_to_request_todos_with_illegal_limit() {
    // We'll set the requested "limit" to be a string ("banana")
    // that can't be parsed to a number.
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("limit", Arrays.asList(new String[] { "banana" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);
    // This should now throw a `BadRequestResponse` exception because
    // our request has an limit that can't be parsed to a number.
    Assertions.assertThrows(BadRequestResponse.class, () -> {
      TodosController.getTodos(ctx);
    });
  }



  @Test
  public void GET_to_request_status_complete_todos() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("status", Arrays.asList(new String[] { "complete" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);
    TodosController.getTodos(ctx);

    // Confirm that all the todos passed to `json` have a completed status.
    ArgumentCaptor<Todos[]> argument = ArgumentCaptor.forClass(Todos[].class);
    verify(ctx).json(argument.capture());
    for (Todos todos : argument.getValue()) {
      assertEquals(true, todos.status);
    }
  }

  @Test
  public void GET_to_request_status_incomplete_todos() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("status", Arrays.asList(new String[] { "incomplete" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);
    TodosController.getTodos(ctx);

    // Confirm that all the todos passed to `json` have a incomplete status.
    ArgumentCaptor<Todos[]> argument = ArgumentCaptor.forClass(Todos[].class);
    verify(ctx).json(argument.capture());
    for (Todos todos : argument.getValue()) {
      assertEquals(false, todos.status);
    }
  }


  @Test
  public void GET_to_request_body_contains_todos() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("contains", Arrays.asList(new String[] { "cillum" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);
    TodosController.getTodos(ctx);

    // Confirm that all the todos passed to `json` have a body that contains "cillum"
    ArgumentCaptor<Todos[]> argument = ArgumentCaptor.forClass(Todos[].class);
    verify(ctx).json(argument.capture());
    for (Todos todos : argument.getValue()) {
      assertEquals(true, todos.body.contains("cillum"));
    }
  }

  @Test
  public void GET_to_request_owner_todos() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("owner", Arrays.asList(new String[] { "Fry" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);
    TodosController.getTodos(ctx);

    // Confirm that all the todos passed to `json` have the owner "Fry"
    ArgumentCaptor<Todos[]> argument = ArgumentCaptor.forClass(Todos[].class);
    verify(ctx).json(argument.capture());
    for (Todos todos : argument.getValue()) {
      assertEquals(true, todos.owner.equals("Fry"));
    }
  }

  @Test
  public void GET_to_request_category_todos() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("category", Arrays.asList(new String[] { "groceries" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);
    TodosController.getTodos(ctx);

    // Confirm that all the todos passed to `json` have the category "groceries"
    ArgumentCaptor<Todos[]> argument = ArgumentCaptor.forClass(Todos[].class);
    verify(ctx).json(argument.capture());
    for (Todos todos : argument.getValue()) {
      assertEquals(true, todos.category.equals("groceries"));
    }
  }


  @Test
  public void GET_to_request_sort_body_todos() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("orderBy", Arrays.asList(new String[] { "body" }));

    Todos[] sortedTodos = db.listTodos(queryParams);
    //Confirm that the todos are sorted alphabetically by their body
    for(int i = 0; i < db.size() - 1; i++){
      assertEquals(true, sortedTodos[i].body.compareTo(sortedTodos[i+1].body) < 0);
    }
  }

  @Test
  public void GET_to_request_sort_owner_todos() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("orderBy", Arrays.asList(new String[] { "owner" }));

    Todos[] sortedTodos = db.listTodos(queryParams);
    //Confirm that the todos are sorted alphabetically by their owner
    for(int i = 0; i < db.size() - 1; i++){
      assertEquals(true, sortedTodos[i].owner.compareTo(sortedTodos[i+1].owner) <= 0);
    }
  }

  @Test
  public void GET_to_request_sort_status_todos() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("orderBy", Arrays.asList(new String[] { "status" }));

    Todos[] sortedTodos = db.listTodos(queryParams);

    for(int i = 0; i < db.size() - 1; i++){

      Boolean statusT1 = sortedTodos[i].status;
      String stringStatT1 = statusT1.toString();
      Boolean statusT2 = sortedTodos[i+1].status;
      String stringStatT2 = statusT2.toString();
      //Confirm that the todos are sorted alphabetically by their status
      assertEquals(true, stringStatT1.compareTo(stringStatT2) <= 0);
    }
  }

  @Test
  public void GET_to_request_sort_category_todos() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("orderBy", Arrays.asList(new String[] { "category" }));

    Todos[] sortedTodos = db.listTodos(queryParams);
    //Confirm that the todos are sorted alphabetically by their category
    for(int i = 0; i < db.size() - 1; i++){
      assertEquals(true, sortedTodos[i].category.compareTo(sortedTodos[i+1].category) <= 0);
    }
  }


  @Test
  public void GET_to_sort_body_filter_owner_category_status_contains_limit_todos() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();

    queryParams.put("orderBy", Arrays.asList(new String[] { "body" }));
    queryParams.put("owner", Arrays.asList(new String[] { "Fry" }));
    queryParams.put("category", Arrays.asList(new String[] { "video games" }));
    queryParams.put("status", Arrays.asList(new String[] { "complete" }));
    queryParams.put("contains", Arrays.asList(new String[] { "nulla" }));
    queryParams.put("limit", Arrays.asList(new String[] { "3" }));


    Todos[] sortedTodos = db.listTodos(queryParams);
    //Confirm that the todos are sorted alphabetically by their body, have the owner Fry, are completed, and that there are only 3 todos
    for(int i = 0; i < sortedTodos.length - 1; i++){
      assertEquals(true, sortedTodos[i].body.compareTo(sortedTodos[i+1].body) < 0);
      assertEquals(true, sortedTodos[i].owner.equals("Fry"));
      assertEquals(true, sortedTodos[i].category.equals("video games"));
      assertEquals(true, sortedTodos[i].status);
      assertEquals(true, sortedTodos[i].category.equals("video games"));
      assertEquals(true, sortedTodos.length == 3);
    }
  }


  @Test
  public void GET_to_request_todo_with_existent_id() throws IOException {
    when(ctx.pathParam("id", String.class)).thenReturn(new Validator<String>("58895985a22c04e761776d54", "", "id"));
    TodosController.getTodo(ctx);
    verify(ctx).status(201);
  }

  @Test
  public void GET_to_request_user_with_nonexistent_id() throws IOException {
    when(ctx.pathParam("id", String.class)).thenReturn(new Validator<String>("nonexistent", "", "id"));
    Assertions.assertThrows(NotFoundResponse.class, () -> {
      TodosController.getTodo(ctx);
    });
  }


}
