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
import play.Logger;
import play.libs.Json;
import se.olapetersson.voting.api.JSONMessage;
import se.olapetersson.voting.api.TokenizerService;
import se.olapetersson.voting.api.VotingService;
import se.olapetersson.voting.impl.command.*;
import se.olapetersson.voting.api.VotingState;

import static java.util.concurrent.CompletableFuture.completedFuture;

public class VotingServiceImpl implements VotingService {

   /* private final PersistentEntityRef ref;
    private final PubSubRef pubSubRef;*/
    private final PubSubRegistry pubSubRegistry;
    private final CassandraSession cassandraSession;
    private Materializer materializer;
    private final PersistentEntityRegistry persistentEntityRegistry;

    @Inject
    private TokenizerService tokenizerService;

    @Inject
    public VotingServiceImpl(PersistentEntityRegistry persistentEntities, PubSubRegistry pubSubRegistry,
                             Materializer materializer, CassandraSession cassandraSession,
                             ReadSide readSide) {
        this.materializer = materializer;
        this.cassandraSession = cassandraSession;

        this.persistentEntityRegistry = persistentEntities;
        this.persistentEntityRegistry.register(VotingEntity.class);
        this.pubSubRegistry = pubSubRegistry;
    //    readSide.register(VoteEventProcessor.class);

     //   this.ref = persistentEntities.refFor(VotingEntity.class, "myId");
     //   this.pubSubRef = pubSubRegistry.refFor(TopicId.of(VotingState.class, "myId"));
    }

    @Override
    public ServiceCall<Source<JSONMessage, ?>, Source<VotingState, NotUsed>> voteStream() {
        Logger.info("in the static stream ");
        return inputStream -> {
            final String[] id = new String[1];
            inputStream.runForeach(input ->  {
                VoteCommand command = getCommand(input);
                id[0] = command.getId();
                Logger.info("setting command id to {}", command.getId());
                PersistentEntityRef ref = persistentEntityRegistry.refFor(VotingEntity.class, command.getId());
                ref.ask(command);
            }, materializer);
            //NewVotingCommand d = new NewVotingCommand("a", "b", "c", "apa");
            PubSubRef pubSubRef = pubSubRegistry.refFor(TopicId.of(VotingState.class, id[0]));
            Logger.info("telling subscribers to id {}", id[0]);
            return completedFuture(pubSubRef.subscriber());
        };
    }

    @Override
    public ServiceCall<NotUsed, String> fail() {
        return req -> {
            throw new RuntimeException();
        };
                /*ref.ask(new FailCommand()).thenApply(r -> "Crashed");*/
    }

    @Override
    public ServiceCall<NotUsed, String> readSide() {
        return req -> cassandraSession.selectOne("SELECT Count(*) FROM participants")
                      .thenApply(row ->
                        row.isPresent() ? "" + row.get().getLong("count") : "No participants");
    }


    @Override
    public ServiceCall<NotUsed, String> dynamicPath(String id) {
        return req -> {
            Logger.info("this is dynamicPath: {}", id);
            PersistentEntityRef<VoteCommand> ref = persistentEntityRegistry.refFor(VotingEntity.class, id);
            return ref.ask(VoteStandingsCommand.create(id)).thenApply(resp -> resp + " was retorno");
        };
    }

    @Override
    public ServiceCall<JSONMessage, VotingState> createVoting(String id) {
        return jsonMessage ->
        {
            NewVotingCommand newVotingCommand = Json.fromJson(jsonMessage.getPayload(), NewVotingCommand.class);
            String jwtToken = newVotingCommand.getJwtToken();
            return tokenizerService.validateToken().invoke(jwtToken).thenComposeAsync(userName -> {
                        PersistentEntityRef<VoteCommand> ref = persistentEntityRegistry.refFor(VotingEntity.class, id);
                        return ref.ask(newVotingCommand).thenApply(resp -> resp);
                    }
            );
        };
    }

    @Override
    public ServiceCall<Source<JSONMessage, ?>, Source<VotingState, NotUsed>> dynamicStream(String id) {
        Logger.info("in the dynamic stream ");
        return inputStream -> {
            inputStream.runForeach(input ->  {
                VoteCommand command = getCommand(input);
                Logger.info("setting command id to {}", id);
                PersistentEntityRef ref = persistentEntityRegistry.refFor(VotingEntity.class, id);
                ref.ask(command);
            }, materializer);
            //NewVotingCommand d = new NewVotingCommand("a", "b", "c", "apa");
            PubSubRef pubSubRef = pubSubRegistry.refFor(TopicId.of(VotingState.class, id));
            Logger.info("telling subscribers to id {}", id);
            return completedFuture(pubSubRef.subscriber());
        };
    }

    private VoteCommand getCommand(JSONMessage input) {
        Assert.notNull(input.getType(), "must define a type");
        Assert.notNull(input.getPayload(), "must define a a payload");
        Logger.info("in {} {}", input.getPayload().getClass(), input.getPayload());
        switch (input.getType()) {
            case "NewVotingCommand":
                NewVotingCommand newVotingCommand = null;
                try {
                    newVotingCommand = Json.fromJson(input.getPayload(), NewVotingCommand.class);
                } catch (Exception e) {
                    Logger.info("apa {} ", e);
                }
                Logger.info("returning {}", newVotingCommand);
                return newVotingCommand;
                case "RegisterVoteCommand":
                    Logger.info("returning {} ", "RegisterVoteCommand");
                    return Json.fromJson(input.getPayload(), RegisterVoteCommand.class);
                case "VoteStandingsCommand":
                    Logger.info("returning {} ", "VoteStandingsCommand");
                    return Json.fromJson(input.getPayload(), VoteStandingsCommand.class);
            default:
                Logger.info("returning a failcommands");
                return new FailCommand();
        }
    }
}
