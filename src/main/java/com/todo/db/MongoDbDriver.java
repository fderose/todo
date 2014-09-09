package com.todo.db;

import com.mongodb.*;
import com.todo.ToDoItem;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

public class MongoDbDriver implements IDatabase<ToDoItem> {

  private final MongoClient mongoClient;
  private final DB db;
  private final DBCollection collection;

  MongoDbDriver() {
    MongoClient theMongoClient = null;
    try {
      theMongoClient = new MongoClient("localhost", 27017);
    } catch (UnknownHostException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    mongoClient = theMongoClient;
    db = theMongoClient.getDB("todoDb");
    collection = db.getCollection("todoItems");
  }

  @Override
  public void clear() {
    collection.remove(new BasicDBObject());
  }

  @Override
  public void put(Integer id, ToDoItem item) {
    BasicDBObject queryObject = new BasicDBObject();
    queryObject.put("id", item.getId());
    BasicDBObject document = new BasicDBObject();
    document.put("id", item.getId());
    document.put("title", item.getTitle());
    document.put("body", item.getBody());
    document.put("done", item.getDone());
    collection.update(queryObject, document, true, false);
  }

  private Collection<ToDoItem> doQuery(BasicDBObject document) {
    DBCursor dbCursor = collection.find(document);
    Collection<ToDoItem> items = new ArrayList<>();
    while (dbCursor.hasNext()) {
      items.add(new ToDoItem(dbCursor.next()));
    }
    return items;
  }

  @Override
  public ToDoItem getById(Integer id) {
    BasicDBObject document = new BasicDBObject();
    document.put("id", id);
    Collection<ToDoItem> items = doQuery(document);
    return items.size() == 1 ? items.iterator().next() : null;
  }

  @Override
  public Collection<ToDoItem> getAll() {
    BasicDBObject document = new BasicDBObject();
    return doQuery(document);
  }

  @Override
  public void remove(Integer id) {
    BasicDBObject document = new BasicDBObject();
    document.put("id", id);
    collection.remove(document);
  }

  @Override
  public void close() {
    mongoClient.close();
  }
}
