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
      new Twilio();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /*
   Adds or updates an item.
   It might be better if I divided adds and updates so that adds
   were handled by a POST and updates were handled by a PUT. Dunno.
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response putIt(ToDoItem item) {
    if (item.getId() < 0) {
      item.setId(ID_GENERATOR.getAndIncrement());
    }
    db.put(item.getId(), item);
    new Searchly().addDocument(item);
    return Response.ok().entity(item).build();
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
   Update an item so that it is marked as done and send a message to a phone.
   */
  @PUT
  @Path("/done")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response putDone(ToDoItem item1) {
    Response response = putIt(item1);
    new Twilio().sendMessage("The following item is done:\n " + item1);
    return response;
  }

  /*
   Get item by id
   */
  @GET
  @Path("/byid")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getById(@QueryParam("id") int id) {
    return Response.ok().entity(db.get(id)).build();
  }

  /*
   Get all items.
   */
  @GET
  @Path("/all")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAll() {
    return Response.ok().entity(new ArrayList(db.values())).build();
  }

  /*
   Search for items by a search term.
   */
  @GET
  @Path("/search")
  @Produces(MediaType.APPLICATION_JSON)
  public Response search(@QueryParam("term") String term, @QueryParam("boost") double boost) {
    return Response.ok().entity(new Searchly().search(term, boost, db)).build();
  }
}
