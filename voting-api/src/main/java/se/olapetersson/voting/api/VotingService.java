/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package se.olapetersson.voting.api;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;

import java.util.List;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.namedCall;

/**
 * The stream interface.
 * <p>
 * This describes everything that Lagom needs to know about how to serve and
 * consume the HelloStream service.
 */
public interface VotingService extends Service {

    ServiceCall<Source<JSONMessage, ?>, Source<Integer, NotUsed>> vote();
    ServiceCall<NotUsed, String> restVote();
    ServiceCall<NotUsed, String> getVoteStandings();
    ServiceCall<NotUsed, String> fail();
    ServiceCall<JSONMessage, String> jsonPay();
    ServiceCall<NotUsed, String> readSide();
    @Override
    default Descriptor descriptor() {
        return named("voting").withCalls(
                Service.namedCall("voting", this::vote),
                Service.namedCall("vote", this::restVote),
                Service.namedCall("fail", this::fail),
                Service.namedCall("voting", this::getVoteStandings),
                Service.namedCall("json", this::jsonPay),
                Service.namedCall("read", this::readSide)
        ).withAutoAcl(true);
    }
}
