/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package se.olapetersson.voting.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import se.olapetersson.voting.api.VotingService;

/**
 * The module that binds the StreamService so that it can be served.
 */
public class VotingModule extends AbstractModule implements ServiceGuiceSupport {
  @Override
  protected void configure() {
    // Bind the StreamService service
    bindServices(serviceBinding(VotingService.class, VotingServiceImpl.class));
    // Bind the HelloService client
//    bindClient(HelloService.class);
  }
}
