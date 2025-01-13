package com.artyom.vertex_starter.eventloops;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerExample extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(WorkerExample.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new WorkerExample());
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.deployVerticle(new WorkerVerticle(),
                         new DeploymentOptions()
                           .setWorker(true)
                           .setWorkerPoolSize(1)
                           .setWorkerPoolName("worker-pool-1")
    );
    startPromise.complete();
    vertx.executeBlocking(event -> {
      LOG.info("Executing blocking Code");
      try {
        Thread.sleep(5000);
        event.complete();
      } catch (InterruptedException e) {
        LOG.error("Interrupted while executing blocking Code", e);
        event.fail(e);
      }
    }, result -> {
      if (result.succeeded()) {
        LOG.info("Blocking call Done");
      }
      else {
        LOG.error("Blocking call Failed", result.cause());
      }
    });
  }
}
