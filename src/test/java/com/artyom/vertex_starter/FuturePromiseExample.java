package com.artyom.vertex_starter;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(VertxExtension.class)
public class FuturePromiseExample {

  private static final Logger LOG = LoggerFactory.getLogger(FuturePromiseExample.class);

  @Test
  void promiseSuccess(Vertx vertx, VertxTestContext context) {
    Promise<String> promise = Promise.promise();

    LOG.info("Start");
    vertx.setTimer(500, event -> {
      promise.complete("Hello World!");
      LOG.info("Success");
    });
    Future<String> future = promise.future();
    future.onSuccess(result -> {
      LOG.info("Result: {}", result);
      context.completeNow();
    }).onFailure(context::failNow);
  }

  @Test
  void promiseFailure(Vertx vertx, VertxTestContext context) {
    Promise<String> promise = Promise.promise();

    LOG.info("Start");
    vertx.setTimer(500, event -> {
      promise.fail(new RuntimeException("Failure!"));
      LOG.info("Timer Done");
    });

    Future<String> future = promise.future();
    future.onSuccess(result -> {
      LOG.info("Result: {}", result);
      context.completeNow();
    }).onFailure(err -> {
      LOG.error("Error: {}", err.getMessage());
      context.completeNow();
    });

    LOG.info("Finish");
  }

  @Test
  void futureMap(Vertx vertx, VertxTestContext context) {
    Promise<String> promise = Promise.promise();

    LOG.info("Start");
    vertx.setTimer(500, event -> {
      promise.complete("Success");
      LOG.info("Timer Done");
    });
    Future<String> future = promise.future();
    future
      .map(s -> {
        LOG.info("Map String to JsonObject");
        return new JsonObject()
          .put("key", s);
      })
      .map(jsonObject -> new JsonArray().add(jsonObject))
      .onSuccess(result -> {
        LOG.info("Result: {} of type {}", result, result.getClass().getSimpleName());
        context.completeNow();
      })
      .onFailure(context::failNow);
  }

  //multipleTasks to be together
  @Test
  void futureCoordination(Vertx vertx, VertxTestContext context) {
    vertx.createHttpServer()
      .requestHandler(event -> {
        LOG.info("{}", event);
      })
      .listen(10_000)
      .compose(httpServer -> {
        LOG.info("Another task");
        return Future.succeededFuture(httpServer);
      })
      .compose(httpServer -> {
        LOG.info("Even more");
        return Future.succeededFuture(httpServer);
      })
      .onFailure(context::failNow)
      .onSuccess(event -> {
        LOG.info("Started server on port {}", event.actualPort());
        context.completeNow();
      });
  }

  @Test
  void futureComposition(Vertx vertx, VertxTestContext context) {
    var one = Promise.<Void>promise();
    var two = Promise.<Void>promise();
    var three = Promise.<Void>promise();


    var futureOne = one.future();
    var futureTwo = two.future();
    var futureThree = three.future();

    Future.all(futureOne, futureTwo, futureThree)
      .onFailure(context::failNow)
      .onSuccess(result -> {
        LOG.info("Success");
        context.completeNow();
      });

    //Complete futures

    vertx.setTimer(500, id -> {
      one.complete();
      two.complete();
      three.fail("Three Failed");
    });

    //Future.any // at least one should success
    //Future.all // all should success
    //Future.join // al waiting to return result than same as all
  }
}
