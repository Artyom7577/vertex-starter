package com.artyom.vertex_starter.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;

public class VerticleAA extends AbstractVerticle {
  @Override
  public void start(final Promise<Void> startPromise) throws Exception {
    System.out.println("Start " + this.getClass().getName());
    startPromise.complete();
  }

  @Override
  public void stop(final Promise<Void> stopPromise) throws Exception {
    System.out.println("Stop " + this.getClass().getName());
    stopPromise.complete();
  }
}
