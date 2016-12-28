/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package se.olapetersson.voting.impl;

import akka.NotUsed;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import com.lightbend.lagom.javadsl.pubsub.PubSubRef;
import com.lightbend.lagom.javadsl.pubsub.PubSubRegistry;
import com.lightbend.lagom.javadsl.pubsub.TopicId;
import org.springframework.util.Assert;
import play.libs.Json;
import se.olapetersson.voting.api.JSONMessage;
import se.olapetersson.voting.api.VotingService;
import se.olapetersson.voting.impl.commands.FailCommand;
import se.olapetersson.voting.impl.commands.RegisterVoteCommand;
import se.olapetersson.voting.impl.commands.VoteCommand;
import se.olapetersson.voting.impl.commands.VoteStandingsCommand;

import static java.util.concurrent.CompletableFuture.completedFuture;

public class VotingServiceImpl implements VotingService {

    private final PersistentEntityRef ref;
    private final PubSubRef<Integer> pubSubRef;
    private final CassandraSession cassandraSession;
    private Materializer materializer;

    @Inject
    public VotingServiceImpl(PersistentEntityRegistry persistentEntities, PubSubRegistry pubSubRegistry,
                             Materializer materializer, CassandraSession cassandraSession,
                             ReadSide readSide) {
        this.materializer = materializer;
        this.cassandraSession = cassandraSession;

        persistentEntities.register(VotingEntity.class);
        readSide.register(VoteEventProcessor.class);
        this.ref = persistentEntities.refFor(VotingEntity.class, "myId");
        this.pubSubRef = pubSubRegistry.refFor(TopicId.of(Integer.class, "myId"));
    }

    @Override
    public ServiceCall<Source<JSONMessage, ?>, Source<Integer, NotUsed>> voteStream() {
        return inputStream -> {
            inputStream.runForeach(input -> ref.ask(getCommand(input)), materializer);
            return completedFuture(pubSubRef.subscriber());
        };
    }

    private VoteCommand getCommand(JSONMessage input) {
        Assert.notNull(input.getType(), "must define a type");
        Assert.notNull(input.getPayload(), "must define a a payload");

        switch (input.getType()) {
            case "RegisterVoteCommand":
                return Json.fromJson(input.getPayload(), RegisterVoteCommand.class);
            case "VoteStandingsCommand":
                return Json.fromJson(input.getPayload(), VoteStandingsCommand.class);
            default:
                return new FailCommand();
        }
    }

    @Override
    public ServiceCall<NotUsed, String> fail() {
        return req -> ref.ask(new FailCommand()).thenApply(r -> "Crashed");
    }


    @Override
    public ServiceCall<NotUsed, String> readSide() {
        return req -> cassandraSession.selectOne("SELECT Count(*) FROM participants")
                      .thenApply(row ->
                        row.isPresent() ? "" + row.get().getLong("count") : "No participants");

    }
}
