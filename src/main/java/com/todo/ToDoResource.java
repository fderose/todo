package com.todo;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Path("todo")
public class ToDoResource {

  private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);
  private static final Map<Integer, ToDoItem> db = new HashMap<>();

  public static void initialize() {
    try {
      ID_GENERATOR.set(1);
      db.clear();
      new Searchly().initialize();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /*
   Adds or updates a messge.
   It might be better if I divided adds and updates so that adds
   were handled by a POST and updates were handled by a PUT. Dunno.
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public ToDoItem putIt(ToDoItem todoItem) {
    if (todoItem.getId() < 0) {
      todoItem.setId(ID_GENERATOR.getAndIncrement());
    }
    db.put(todoItem.getId(), todoItem);
    new Searchly().addDocument(todoItem);
    return todoItem;
  }

  /*
   Delete by id.
   */
  @DELETE
  @Consumes(MediaType.APPLICATION_JSON)
  public Response deleteById(@QueryParam("id") int id) {
    db.remove(id);
    new Searchly().deleteDocument(id);
    return Response.ok().build();
  }

  /*
   Update an item so that it is marked as done and send a message to my iPhone.
   */
  @PUT
  @Path("/done")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public ToDoItem putDone(ToDoItem inputItem) {
    ToDoItem outputItem = putIt(inputItem);
    new Twilio().sendMessage("The following item is done: " + outputItem.toString());
    return outputItem;
  }

  /*
   Get item by id
   */
  @GET
  @Path("/byid")
  @Produces(MediaType.APPLICATION_JSON)
  public ToDoItem getById(@QueryParam("id") int id) {
    return db.get(id);
  }

  /*
   Get all items.
   */
  @GET
  @Path("/all")
  @Produces(MediaType.APPLICATION_JSON)
  public ArrayList<ToDoItem> getAll() {
    return new ArrayList(db.values());
  }

  /*
   Search for items by a search term.
   */
  @GET
  @Path("/search")
  @Produces(MediaType.APPLICATION_JSON)
  public ArrayList<ToDoItem> search(@QueryParam("term") String term) {
    return new Searchly().search(term, db);
  }
}
