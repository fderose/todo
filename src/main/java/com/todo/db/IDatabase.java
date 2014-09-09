package com.todo.db;

import java.util.Collection;

public interface IDatabase<T> {
  void clear();

  void put(Integer id, T item);

  T getById(Integer id);

  Collection<T> getAll();

  void remove(Integer id);

  void close();

}
