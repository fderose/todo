package com.todo;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.*;

import static org.junit.Assert.*;

public class ToDoTest {

  private HttpServer server;
  private WebTarget target;

  @Before
  public void setUp() throws Exception {
    server = Main.startServer();
    Client c = ClientBuilder.newClient().register(JacksonJsonProvider.class);
    target = c.target(Main.BASE_URI);
    ToDoResource.initialize();
  }

  @After
  public void tearDown() throws Exception {
    server.stop();
  }

  // Helper functions
  private void checkStatus(Response response) {
    if (response.getStatus() != 200) {
      System.out.println(response.getStatusInfo().getReasonPhrase());
    }
    assertEquals(response.getStatusInfo().getReasonPhrase() + " - ", 200, response.getStatus());
  }

  private ToDoItem putOne(ToDoItem todoItem, String path) {
    Entity<ToDoItem> entity = Entity.json(todoItem);
    Response response = target.path(path).request().put(entity);
    checkStatus(response);
    return response.readEntity(ToDoItem.class);
  }

  private ToDoItem putOne(ToDoItem todoItem) {
    return putOne(todoItem, "todo");
  }

  private void testPutOne(String title, String body) {
    ToDoItem inputItem = new ToDoItem(-1, title, body);
    ToDoItem outputItem = putOne(inputItem);
    assertEquals(inputItem.getTitle(), outputItem.getTitle());
    assertEquals(inputItem.getBody(), outputItem.getBody());
    assertEquals(outputItem.getDone(), false);
    assertTrue(outputItem.getId() > 0);
  }

  private void compareInputSetToOutputSet(Set<ToDoItem> inputSet, Set<ToDoItem> outputSet) {
    assertEquals(inputSet.size(), outputSet.size());
    for (ToDoItem outputItem : outputSet) {
      inputSet.remove(outputItem);
    }
    assertEquals(inputSet.size(), 0);
  }

  /**
   * Test that we can add items
   */
  @Test
  public void testPutIt() {
    testPutOne("Clean", "Clean the dang house!");
    testPutOne("Pay", "Pay all the bills!");
    testPutOne("Walk", "Walk the dog!");
  }

  /**
   * Test that we can retrieve an item by id
   */
  @Test
  public void testGetById() {
    int id = putOne(new ToDoItem(-1, "Lawn", "Mow the lawn.")).getId();
    Response response = target.path("todo/byid").queryParam("id", id).request().get(Response.class);
    checkStatus(response);
    assertEquals(id, response.readEntity(ToDoItem.class).getId());
  }

  /**
   * Test that we can retrieve all items
   */
  @Test
  public void testGetAll() {
    Set<ToDoItem> inputSet = new HashSet<>();
    inputSet.add(putOne(new ToDoItem(-1, "Lawn", "Mow the lawn.")));
    inputSet.add(putOne(new ToDoItem(-1, "Clean", "Clean the dang house!")));
    inputSet.add(putOne(new ToDoItem(-1, "Pay", "Pay all the bills!")));
    Response response = target.path("todo/all").request().get(Response.class);
    checkStatus(response);
    List<Map<String, Object>> items = response.readEntity(ArrayList.class);
    Set<ToDoItem> outputSet = new HashSet<>();
    for (Map<String, Object> map : items) {
      int id = (Integer) map.get("id");
      String title = (String) map.get("title");
      String body = (String) map.get("body");
      ToDoItem item = new ToDoItem(id, title, body);
      outputSet.add(item);
    }
    compareInputSetToOutputSet(inputSet, outputSet);
  }

  /**
   * Test that we can search for items by a single search term
   */
  @Test
  public void search() {
    Set<ToDoItem> inputSet = new HashSet<>();
    inputSet.add(putOne(new ToDoItem(-1, "Lawn", "Mow the lawn.")));
    inputSet.add(putOne(new ToDoItem(-1, "Weed", "Weed the lawn!")));
    // Give index time to be updated
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Response response = target.path("todo/search").queryParam("term", "lawn").request().get(Response.class);
    assertEquals(response.getStatus(), 200);
    List<Map<String, Object>> items = response.readEntity(ArrayList.class);
    Set<ToDoItem> outputSet = new HashSet<>();
    for (Map<String, Object> map : items) {
      int id = (Integer) map.get("id");
      String title = (String) map.get("title");
      String body = (String) map.get("body");
      ToDoItem item = new ToDoItem(id, title, body);
      outputSet.add(item);
    }
    compareInputSetToOutputSet(inputSet, outputSet);
  }

  /**
   * Test that we can set an item to done
   */
  @Test
  public void testSetDone() {
    int fingerprint = new Random().nextInt();
    System.out.println(String.format("Running testSetDone with fingerprint %s", String.valueOf(fingerprint)));
    ToDoItem item1 = putOne(new ToDoItem(-1, "Lawn", String.format("Mow the lawn. (%s)", String.valueOf(fingerprint))));
    item1.setDone(true);
    putOne(item1, "todo/done");
    Response response = target.path("todo/byid").queryParam("id", item1.getId()).request().get(Response.class);
    checkStatus(response);
    ToDoItem item2 = response.readEntity(ToDoItem.class);
    assertTrue(item1.equals(item2));
    assertTrue(item2.getDone());
  }

  /**
   * Test that we can delete an item
   */
  @Test
  public void testDelete() {
    ToDoItem outputItem = putOne(new ToDoItem(-1, "Lawn", "Mow the lawn."));
    Response response = target.path("todo").queryParam("id", outputItem.getId()).request().delete();
    checkStatus(response);
    response = target.path("todo/byid").queryParam("id", outputItem.getId()).request().get(Response.class);
    checkStatus(response);
    assertNull(response.readEntity(ToDoItem.class));
  }
}
