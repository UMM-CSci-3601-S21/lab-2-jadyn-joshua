package umm3601.todos;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Comparator;


import com.google.gson.Gson;

import io.javalin.http.BadRequestResponse;

/**
 * A fake "database" of todos info
 * <p>
 * Since we don't want to complicate this lab with a real database, we're going
 * to instead just read a bunch of todos data from a specified JSON file, and
 * then provide various database-like methods that allow the `TodosController` to
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
   * Get the single todo specified by the given ID. Return `null` if there is no
   * todo with that ID.
   *
   * @param id the ID of the desired todo
   * @return the todo with the given ID, or null if there is no todo with that ID
   */
  public Todos getTodo(String id) {
    return Arrays.stream(allTodos).filter(x -> x._id.equals(id)).findFirst().orElse(null);
  }


  /**
   * Get an array of all the todos satisfying the queries in the params.
   *
   * @param queryParams map of key-value pairs for the query
   * @return an array of all the todos matching the given criteria
   */
  public Todos[] listTodos(Map<String, List<String>> queryParams) {
    Todos[] filteredTodos = allTodos;

    // Filter todos by status if defined
    if (queryParams.containsKey("status")) {
      String statusParam = queryParams.get("status").get(0);
      if( "complete".equals(statusParam)) {
        filteredTodos = filterTodosByStatus(filteredTodos, true);
      }
      else {
        filteredTodos = filterTodosByStatus(filteredTodos, false);
      }
    }

    // Filter todos by what their body contains if defined
    if (queryParams.containsKey("contains")) {
      String containsParam = queryParams.get("contains").get(0);
      filteredTodos = filterTodosByBody(filteredTodos, containsParam);
    }

    // Filter todos by owner if defined
    if (queryParams.containsKey("owner")) {
      String ownerParam = queryParams.get("owner").get(0);
      filteredTodos = filterTodosByOwner(filteredTodos, ownerParam);
    }


    // Filter todos by category if defined
    if (queryParams.containsKey("category")) {
      String categoryParam = queryParams.get("category").get(0);
      filteredTodos = filterTodosByCategory(filteredTodos, categoryParam);
    }

    // Sort todos by a specific attribute if defined
    if (queryParams.containsKey("orderBy")) {
      String orderByParam = queryParams.get("orderBy").get(0);
      filteredTodos = sortTodos(filteredTodos, orderByParam);
    }

    // Filter todos by limit if defined
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
   * Get an array of a certain number of todos based on limit
   *
   * @param todos    the list of todos to filter by limit
   * @param targetLimit the number we want to limit the todos by
   * @return an array of all the todos limited by the target limit
   */
  public Todos[] filterTodosByLimit(Todos[] todos, int targetLimit) {
    return Arrays.copyOfRange(todos, 0, targetLimit);
  }


  /**
   * Get an array of a todos based on status
   *
   * @param todos     the list of todos to filter by status
   * @param targetStatus the status we want to filter the todos by
   * @return an array of all the todos filtered by the target status.
   */
  public Todos[] filterTodosByStatus(Todos[] todos, boolean targetStatus) {
    return Arrays.stream(todos).filter(x -> x.status == targetStatus).toArray(Todos[]::new);
  }


   /**
   * Get an array of a todos based on a string in their bodies
   *
   * @param todos     the list of todos to filter by what their body contains
   * @param targetString the string we want to look for in the bodies
   * @return an array of all the todos filtered by what their body contains
   */
  public Todos[] filterTodosByBody(Todos[] todos, String targetString) {
    return Arrays.stream(todos).filter(x -> x.body.contains(targetString) == true).toArray(Todos[]::new);
  }

  /**
   * Get an array of a todos based on their owner
   *
   * @param todos     the list of todos to filter by their owner
   * @param targetOwner the owner we want to filter the todos by
   * @return an array of all the todos filtered by their owner
   */
  public Todos[] filterTodosByOwner(Todos[] todos, String targetOwner) {
    return Arrays.stream(todos).filter(x -> x.owner.equals(targetOwner)).toArray(Todos[]::new);
  }

  /**
   * Get an array of a todos based on their category
   *
   * @param todos     the list of todos to filter by their category
   * @param targetCategory the category we want to filter the todos by
   * @return an array of all the todos filtered by their category
   */
  public Todos[] filterTodosByCategory(Todos[] todos, String targetCategory) {
    return Arrays.stream(todos).filter(x -> x.category.equals(targetCategory)).toArray(Todos[]::new);
  }

/**
   * Get an array of a todos sorted based on a particular attribute
   *
   * @param todos     the list of todos to be sorted by their attribute
   * @param attribute the attribute we want to sort the todos by
   * @return an array of all the todos sorted by a particular attribute
   */
  public Todos[] sortTodos(Todos[] todos, String attribute) {

    if("owner".equals(attribute))
      Arrays.sort(todos, new Comparator<Todos>(){
        public int compare(Todos t1, Todos t2) {
          return t1.owner.compareTo(t2.owner);
       }
      } );


    if("category".equals(attribute))
      Arrays.sort(todos, new Comparator<Todos>(){
        public int compare(Todos t1, Todos t2) {
          return t1.category.compareTo(t2.category);
       }
      } );


    if("body".equals(attribute))
      Arrays.sort(todos, new Comparator<Todos>(){
        public int compare(Todos t1, Todos t2) {
          return t1.body.compareTo(t2.body);
       }
      } );

    if("status".equals(attribute))
      Arrays.sort(todos, new Comparator<Todos>(){
        public int compare(Todos t1, Todos t2) {

         Boolean statusT1 = t1.status;
         String stringStatT1 = statusT1.toString();

         Boolean statusT2 = t2.status;
         String stringStatT2 = statusT2.toString();

         return stringStatT1.compareTo(stringStatT2);
       }
      } );

    return todos;
  }



}





