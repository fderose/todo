package com.todo;

import java.util.Map;

public class ToDoItem {


  private int id;
  private String title;
  private String body;
  private boolean done;

  public int getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getBody() {
    return body;
  }

  public boolean getDone() {
    return done;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setDone(boolean done) {
    this.done = done;
  }

  public ToDoItem() {
  }

  public ToDoItem(int id, String title, String body) {
    this.id = id;
    this.title = title;
    this.body = body;
    this.done = false;
  }

  public ToDoItem(Map<Object, Object> map) {
    id = (Integer) map.get("id");
    title = (String) map.get("title");
    body = (String) map.get("body");
    done = (boolean) map.get("done");
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Todo item:\n  id = ");
    sb.append(id);
    sb.append("\n  title = ");
    sb.append(title);
    sb.append("\n  body = ");
    sb.append(body);
    sb.append("\n  done = ");
    sb.append(done);
    return sb.toString();
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + id;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ToDoItem)) {
      return false;
    }
    ToDoItem other = (ToDoItem) obj;
    return id == other.id;
  }
}
