package com.todo;

public class ScoredToDoItem {
  private final ToDoItem todoItem;
  private final double score;
  ScoredToDoItem(ToDoItem todoItem, double score) {
    this.todoItem = todoItem;
    this.score = score;
  }

  public ToDoItem getTodoItem() {
    return todoItem;
  }

  public double getScore() {
    return score;
  }
}
