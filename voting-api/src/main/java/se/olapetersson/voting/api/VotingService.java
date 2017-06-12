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

/**
 * The stream interface.
 * <p>
 * This describes everything that Lagom needs to know about how to serve and
 * consume the HelloStream service.
 */
public interface VotingService extends Service {

    ServiceCall<Source<JSONMessage, ?>, Source<VotingState, NotUsed>> voteStream();
    ServiceCall<NotUsed, String> fail();
    ServiceCall<NotUsed, String> readSide();
    ServiceCall<NotUsed, String> dynamicPath(String id);
    ServiceCall<String, String> createVoting(String id);
    ServiceCall<Source<JSONMessage, ?>, Source<VotingState, NotUsed>> dynamicStream(String id);

    @Override
    default Descriptor descriptor() {
        return named("voting").withCalls(
                Service.namedCall("voting", this::voteStream),
                Service.namedCall("fail", this::fail),
                Service.namedCall("read", this::readSide),
                Service.pathCall("/read/:id", this::dynamicPath),
                Service.pathCall("/read/:id", this::dynamicStream),
                Service.pathCall("/voting/:id", this::createVoting)
        ).withAutoAcl(true);
    }
}
