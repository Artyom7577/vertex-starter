package com.artyom.vertex_starter.eventloops;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(WorkerVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    LOG.info("Deployed as worker verticle.");
    startPromise.complete();
    Thread.sleep(5000);
    LOG.info("Blocking Operation Done!");
  }
}
