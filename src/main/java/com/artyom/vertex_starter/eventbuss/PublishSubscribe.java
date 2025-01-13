package com.artyom.vertex_starter.eventbuss;

import java.time.Duration;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublishSubscribe {
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new Publish());
    vertx.deployVerticle(new Subscribe1());
    vertx.deployVerticle(Subscribe2.class.getName(), new DeploymentOptions().setInstances(2));
  }

  public static class Publish extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      vertx.setPeriodic(Duration.ofSeconds(5).toMillis(), l -> {
        vertx.eventBus().publish(Publish.class.getName(), "A message for everyone!");
      });
    }
  }

  public static class Subscribe1 extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(Subscribe1.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      vertx.eventBus().<String>consumer(Publish.class.getName(), event -> {
        LOG.info("Received event: {}", event.body());
      });
    }
  }

  public static class Subscribe2 extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(Subscribe2.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      vertx.eventBus().<String>consumer(Publish.class.getName(), event -> {
        LOG.info("Received event: {}", event.body());
      });
    }
  }
}
