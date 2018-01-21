package com.consulner;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.consulner.service.stats.StatsService;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class Handler implements RequestHandler<Void, Void> {

  private static final Logger LOG = Logger.getLogger(Handler.class);

  /* use minimum required dependencies for spring DI
    spring-core
    spring-beans
    spring-context
    or just not DI framework (as here).
  */

  @Override
  public Void handleRequest(Void aVoid, Context context) {
    BasicConfigurator.configure();
    new StatsService().updateStats();
    return null;
  }
}
