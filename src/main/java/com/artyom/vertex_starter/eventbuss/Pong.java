package com.artyom.vertex_starter.eventbuss;

public class Pong {

  private int id;

  public Pong() {

  }
  public Pong(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  @Override
  public String toString() {
    return "Pong{" +
      "id=" + id +
      '}';
  }
}
