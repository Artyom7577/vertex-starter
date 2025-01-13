package com.artyom.vertex_starter.eventbuss;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestResponseExampleJson {

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.deployVerticle(new RequestVerticle());
    vertx.deployVerticle(new ResponseVerticle());
  }

  static class RequestVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(RequestVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      EventBus eventBus = vertx.eventBus();
      //address kara lini class i anun@
      // event -@ erb vor response galisa
      final JsonObject message = new JsonObject().put("message", "my.request.message");
      LOG.info("Sending: {}", message);
      eventBus.<JsonObject>request("my.request.address", message, event -> {
        LOG.info("Received response: {}", event.result().body());
      });
    }

  }

  static class ResponseVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(ResponseVerticle.class);


    @Override
    public void start(Promise<Void> startPromise) throws Exception {
      startPromise.complete();
      vertx.eventBus().consumer("my.request.address", message -> {
        LOG.info("Received message: {}", message.body());
        message.reply("Received your message. Thanks!");
      });
    }

  }
}
