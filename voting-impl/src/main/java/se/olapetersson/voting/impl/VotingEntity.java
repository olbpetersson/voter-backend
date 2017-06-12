package se.olapetersson.voting.impl;

import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.javadsl.pubsub.PubSubRef;
import com.lightbend.lagom.javadsl.pubsub.PubSubRegistry;
import com.lightbend.lagom.javadsl.pubsub.TopicId;
import org.springframework.util.Assert;
import play.Logger;
import se.olapetersson.voting.api.TokenizerService;
import se.olapetersson.voting.api.VotingOption;
import se.olapetersson.voting.api.VotingState;
import se.olapetersson.voting.impl.command.*;
import se.olapetersson.voting.impl.event.CloseEvent;
import se.olapetersson.voting.impl.event.NewVotingEvent;
import se.olapetersson.voting.impl.event.VoteEvent;
import se.olapetersson.voting.impl.event.VoteRegisteredEvent;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

public class VotingEntity extends PersistentEntity<VoteCommand, VoteEvent, VotingState> {

    @Inject
    TokenizerService tokenService;

    private final PubSubRegistry pubSubRegistry;
    private PubSubRef pubSubRef;

    @Inject
    public VotingEntity(PubSubRegistry pubSubRegistry) {
        Logger.info("Persist id in the constructor: {} {} ", this.entityId(), pubSubRegistry);

        this.pubSubRegistry = pubSubRegistry;
    }

    @PostConstruct
    public void setUp(){
        Logger.info("persistentid in the post {}", this.entityId());
    }


    @SuppressWarnings("unchecked")
    @Override
    public Behavior initialBehavior(Optional<VotingState> snapshotState) {
        Logger.info("setting the initial behavior wohoo");
        pubSubRef = pubSubRegistry.refFor(TopicId.of(VotingState.class, entityId()));

        return initBehavior(snapshotState).build();
    }

    private BehaviorBuilder initBehavior(Optional<VotingState> snapshotState) {
        Logger.info("Creating a voting...");
        BehaviorBuilder behaviorBuilder = newBehaviorBuilder(snapshotState.orElse(null));
        behaviorBuilder.setCommandHandler(NewVotingCommand.class, (cmd, ctx) -> {
            Logger.info("monkey");
            CompletionStage<String> firstFuture = tokenService.validateToken().invoke(cmd.getJwtToken());
            try {
                return firstFuture.thenApply(userName -> {
                    Assert.hasText(userName, "Attempted to created a game with an invalid token");
                    return ctx.thenPersist(new NewVotingEvent(cmd.getVotingName(), cmd.getVotingOptionA(),
                                    cmd.getVotingOptionB(), userName), evt -> {
                                Logger.info("starting a voting... {} ", snapshotState.orElse(null));
                                pubSubRef.publish(state());
                                ctx.reply(state());
                            }
                    );
                }).toCompletableFuture().get();
            } catch (InterruptedException|ExecutionException e) {
                e.printStackTrace();
                return ctx.done();
            }
        });

        behaviorBuilder.setEventHandlerChangingBehavior(NewVotingEvent.class, evt ->
                openedBehavior(Optional.of(
                        VotingState.createVotingState(evt.getVotingName(), VotingOption.create(evt.getVotingOptionA()),
                                VotingOption.create(evt.getVotingOptionB()), evt.getCreatedBy())
                )));

        return behaviorBuilder;
    }


    private Behavior openedBehavior(Optional<VotingState> snapshotState) {
        //"Should" never be a nullpointer, using for debugging now
        Logger.info("Opened up a voting with name '{}'", snapshotState.orElse(null).getVotingName());
        BehaviorBuilder behaviorBuilder = newBehaviorBuilder(
                snapshotState.orElse(null)
        );

        behaviorBuilder.setCommandHandler(RegisterVoteCommand.class,
                (cmd, ctx) -> {
                    Assert.isTrue(cmd.getVotingName().trim().equalsIgnoreCase(state().getVotingName()),
                            "Tried to vote with an invalid votingname");

                    Persist persistenceResult;
                    Logger.info("Got a registerVoteCommand with value {}", cmd.getVotingOption());
                    if(cmd.getVotingOption().equalsIgnoreCase(state().getVotingOptionA().getVotingName()) ||
                            cmd.getVotingOption().equalsIgnoreCase(state().getVotingOptionB().getVotingName())){
                        persistenceResult = ctx.thenPersist(new VoteRegisteredEvent(cmd.getVotingName(), System.currentTimeMillis()), evt ->
                                ctx.reply(state()));
                    } else{
                        ctx.invalidCommand("The voteStream was tampered with! Value was: " + cmd);

                        persistenceResult = ctx.done();
                    }
                    pubSubRef.publish(state());
                    return persistenceResult;
                }
        );

        behaviorBuilder.setCommandHandler(FailCommand.class, (cmd, ctx) ->
                {
                    Logger.error("FAILING THE ENTITY");
                    throw new RuntimeException("aouch");
                }
        );

        behaviorBuilder.setReadOnlyCommandHandler(VoteStandingsCommand.class, (cmd, ctx) ->{
                    Logger.info("Persist id: {}", this.entityId());
                    Logger.info("this is the hashcode: " + this.hashCode());
                    pubSubRef.publish(state());
                    ctx.reply(state());
                }

        );

        behaviorBuilder.setEventHandler(VoteRegisteredEvent.class, evt -> {
                    Logger.info("Applying VoteRegisteredEvent with value {} and user {}", evt.getVoteName(), evt.getUserEpochId());
                    Logger.info("State will be updated to: {}", state());
                    VotingOption votingOptionA = state().getVotingOptionA();
                    VotingOption votingOptionB = state().getVotingOptionB();
                    if (evt.getVoteName().equalsIgnoreCase(state().getVotingOptionA().getVotingName())) {
                        votingOptionA.addVote();
                    } else {
                        votingOptionB.addVote();
                    }
                    final VotingState newState = VotingState.createVotingState(state().getVotingName(),
                            votingOptionA,
                            votingOptionB,
                            state().getCreatedBy());
                    pubSubRef.publish(newState);
                    return newState;
                }
        );

        behaviorBuilder.setEventHandlerChangingBehavior(CloseEvent.class, evt -> {
                    BehaviorBuilder closedBehavior = newBehaviorBuilder(null);
                    behaviorBuilder.setEventHandler(VoteRegisteredEvent.class, disregard -> {
                        throw new RuntimeException();
                    });
                    return closedBehavior.build();
                }
        );

        return behaviorBuilder.build();
    }
}
