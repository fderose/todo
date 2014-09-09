package com.todo.db;

import com.todo.ToDoItem;

import java.util.HashMap;

public class DatabaseManager {
  enum enumDbType {
    hashmap,
    mongo
  }

  private static void error() {
    throw new RuntimeException("System property \"dbType\" must be set to one of \"hashmap\" or \"mongo\"");
  }

  public static IDatabase getDatabaseInstance() {
    String dbType = System.getProperty("dbType");
    if (dbType == null) {
      error();
    }
    dbType = dbType.toLowerCase();
    IDatabase db = null;
    if (dbType.equals(enumDbType.hashmap.name())) {
      db = new HashMapDbDriver<>(new HashMap<Integer, ToDoItem>());
    } else if (dbType.equals(enumDbType.mongo.name())) {
      db = new MongoDbDriver();
    } else {
      error();
    }
    return db;
  }
}
