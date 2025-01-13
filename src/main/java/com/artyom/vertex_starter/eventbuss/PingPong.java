package com.artyom.vertex_starter.eventbuss;

import static com.artyom.vertex_starter.eventbuss.PingPong.PingVerticle.ADDRESS;

import com.artyom.vertex_starter.messageCodec.LocalMessageCodec;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingPong {
  private static final Logger LOG = LoggerFactory.getLogger(PingPong.class);

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.deployVerticle(new PingVerticle(), errorHandler());
    vertx.deployVerticle(new PongVerticle(), errorHandler());
  }

  private static Handler<AsyncResult<String>> errorHandler() {
    return event -> {
      if (event.failed()) {
        LOG.error("err", event.cause());
      }
    };
  }

  static class PingVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(PingVerticle.class);
    static final String ADDRESS = PingVerticle.class.getName();

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      var eventBus = vertx.eventBus();
      final Ping message = new Ping("my.request.message", true);
      LOG.info("Sending: {}", message);

      // Register only once
      eventBus.registerDefaultCodec(Ping.class, new LocalMessageCodec<>(Ping.class));
      eventBus.<Pong>request(ADDRESS, message, event -> {
        if (event.failed()) {
          LOG.error("Sending {} failed", ADDRESS, event.cause());
          return;
        }
        LOG.info("Received response: {}", event.result().body());
      });

      startPromise.complete();
    }

  }

  static class PongVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(PongVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      var eventBus = vertx.eventBus();
      // Register only once
      eventBus.registerDefaultCodec(Pong.class, new LocalMessageCodec<>(Pong.class));
      eventBus.consumer(ADDRESS, message -> {
        LOG.info("Received message: {}", message.body());
        message.reply(new Pong(0));
      }).exceptionHandler(t -> {
        LOG.error("Received exception from PongVerticle", t);
      });

      startPromise.complete();
    }
  }
}
