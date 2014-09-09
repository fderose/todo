package com.todo.db;

import java.util.Collection;

public class MongoDbDriver<T> implements IDatabase<T> {
  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void put(Integer id, T item) {
    throw new UnsupportedOperationException();
  }

  @Override
  public T getById(Integer id) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<T> getAll() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void remove(Integer id) {
    throw new UnsupportedOperationException();
  }
}
