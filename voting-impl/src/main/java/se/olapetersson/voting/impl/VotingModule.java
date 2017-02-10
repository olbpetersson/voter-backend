/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package se.olapetersson.voting.impl;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.api.ConfigurationServiceLocator;
import com.lightbend.lagom.javadsl.api.ServiceLocator;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import play.Configuration;
import play.Environment;
import se.olapetersson.voting.api.VotingService;

/**
 * The module that binds the StreamService so that it can be served.
 */
public class VotingModule extends AbstractModule implements ServiceGuiceSupport {

    private Environment environment;
    private Configuration configuration;

    @Inject
    public VotingModule(Environment environment, Configuration configuration) {
        this.environment = environment;
        this.configuration = configuration;
    }

    @Override
    protected void configure() {
        // Bind the StreamService service
        bindServices(serviceBinding(VotingService.class, VotingServiceImpl.class));
//        bind(ServiceLocator.class).to(ConfigurationServiceLocator.class);
        // Bind the HelloService client
//    bindClient(HelloService.class);
    }
}
