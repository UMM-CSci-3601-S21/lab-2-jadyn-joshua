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
 * Tests the logic of the UserController
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

    // Confirm that `json` was called with all the users.
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

    // Confirm that all the users passed to `json` have age 25.
    ArgumentCaptor<Todos[]> argument = ArgumentCaptor.forClass(Todos[].class);
    verify(ctx).json(argument.capture());
    assertEquals(10, argument.getValue().length);
  }



  @Test
  public void GET_to_request_status_complete_todos() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("status", Arrays.asList(new String[] { "complete" }));

    when(ctx.queryParamMap()).thenReturn(queryParams);
    TodosController.getTodos(ctx);

    // Confirm that all the users passed to `json` have a completed status.
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

    // Confirm that all the users passed to `json` have a incomplete status.
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

    // Confirm that all the users passed to `json` have a body that contains "cillum"
    ArgumentCaptor<Todos[]> argument = ArgumentCaptor.forClass(Todos[].class);
    verify(ctx).json(argument.capture());
    for (Todos todos : argument.getValue()) {
      assertEquals(true, todos.body.contains("cillum"));
    }
  }


}
