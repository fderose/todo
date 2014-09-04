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
  private ToDoItem putOne(ToDoItem todoItem, String path) {
    Entity<ToDoItem> entity = Entity.json(todoItem);
    Response response = target.path(path).request().put(entity);
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
    ToDoItem outputItem = target.path("todo/byid").queryParam("id", id).request().get(ToDoItem.class);
    assertEquals(id, outputItem.getId());
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
    List<Map<String, Object>> items = target.path("todo/all").request().get(ArrayList.class);
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
    List<Map<String, Object>> items = target.path("todo/search").queryParam("term", "lawn").request().get(ArrayList.class);
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
    ToDoItem outputItem = putOne(new ToDoItem(-1, "Lawn", String.format("Mow the lawn. (%s)", String.valueOf(fingerprint))));
    outputItem.setDone(true);
    putOne(outputItem, "todo/done");
    outputItem = target.path("todo/byid").queryParam("id", outputItem.getId()).request().get(ToDoItem.class);
    assertEquals(outputItem.getDone(), true);
  }

  /**
   * Test that we can delete an item
   */
  @Test
  public void testDelete() {
    ToDoItem outputItem = putOne(new ToDoItem(-1, "Lawn", "Mow the lawn."));
    Response response = target.path("todo").queryParam("id", outputItem.getId()).request().delete();
    assertEquals(response.getStatus(), 200);
    outputItem = target.path("todo/byid").queryParam("id", outputItem.getId()).request().get(ToDoItem.class);
    assertNull(outputItem);
  }
}
