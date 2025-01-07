package com.artyom.vertex_starter.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class VerticleA extends AbstractVerticle {
  @Override
  public void start(final Promise<Void> startPromise) throws Exception {
    System.out.println("Start " + this.getClass().getName());
    vertx.deployVerticle(new VerticleAA(), event -> {
      System.out.println("Deployed " + VerticleAA.class.getName());
      vertx.undeploy(event.result());
    });
    vertx.deployVerticle(new VerticleAB(), event -> {
      System.out.println("Deployed " + VerticleAB.class.getName());
      // here dont need undeploy
    });
    startPromise.complete();
  }
}
