package com.artyom.vertex_starter.eventbuss;

import java.awt.*;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PointToPoint {
  public static void main(String[] args) {
    var vertex = Vertx.vertx();
    vertex.deployVerticle(new Sender());
    vertex.deployVerticle(new Receiver());
  }


  static class Sender extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      vertx.setPeriodic(1000, id -> {
        // Send a message every second
        vertx.eventBus().send(Sender.class.getName(), "Sending a message...");
      });
    }
  }

  static class Receiver extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(PointToPoint.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      vertx.eventBus().<String>consumer(Sender.class.getName(), event -> {
        LOG.info("Received: {}", event.body());
      });
    }
  }
}
