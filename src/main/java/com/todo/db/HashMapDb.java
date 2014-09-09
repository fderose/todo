package com.todo.db;

import java.util.Collection;
import java.util.Map;

public class HashMapDb<T> implements IDatabase<T> {

  private final Map<Integer, T> db;

  public HashMapDb(Map<Integer, T> map) {
    db = map;
  }


  @Override
  public void clear() {
    db.clear();
  }

  @Override
  public void put(Integer id, T item) {
    db.put(id, item);
  }

  @Override
  public T getById(Integer id) {
    return db.get(id);
  }

  @Override
  public Collection getAll() {
    return db.values();
  }

  @Override
  public void remove(Integer id) {
    db.remove(id);
  }
}
