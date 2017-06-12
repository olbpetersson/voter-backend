/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package se.olapetersson.tokenizer.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import se.olapetersson.voting.api.TokenizerService;

/**
 * The module that binds the StreamService so that it can be served.
 */
public class TokenizerModule extends AbstractModule implements ServiceGuiceSupport {

    /*private Environment environment;
    private Configuration configuration;
    private ConsulConfiguration consulConfiguration;

    @Inject
    public TokenizerModule(Environment environment, Configuration configuration) throws UnknownHostException {
        this.environment = environment;
        this.configuration = configuration;
        *//*this.consulConfiguration = new ConsulConfiguration(configuration);
        registerService();*//*
    }

    private void registerService() throws UnknownHostException {
        int servicePort = Integer.parseInt(configuration.getString("http.port"));
        String hostname = InetAddress.getLocalHost().getHostAddress();

        //Create a ConsulService(serviceName, serviceHostName, servicePort) and register it
        ConsulService consulService = new ConsulService("voting", hostname, servicePort);
        consulService.registerService(consulConfiguration.getAgentHostname(), consulConfiguration.getAgentPort());
    }*/

    @Override
    protected void configure() {
        bindServices(serviceBinding(TokenizerService.class, TokenizerServiceImpl.class));
    }
}
