/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package se.olapetersson.voting.api;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.namedCall;

/**
 * The stream interface.
 * <p>
 * This describes everything that Lagom needs to know about how to serve and
 * consume the HelloStream service.
 */
public interface VotingService extends Service {

    ServiceCall<Source<JSONMessage, ?>, Source<Integer, NotUsed>> voteStream();
    ServiceCall<NotUsed, String> fail();
    ServiceCall<NotUsed, String> readSide();

    @Override
    default Descriptor descriptor() {
        return named("voting").withCalls(
                Service.namedCall("voting", this::voteStream),
                Service.namedCall("fail", this::fail),
                Service.namedCall("read", this::readSide)
        ).withAutoAcl(true);
    }
}
