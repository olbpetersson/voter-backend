package se.olapetersson.voting.impl;

import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.javadsl.pubsub.PubSubRef;
import com.lightbend.lagom.javadsl.pubsub.PubSubRegistry;
import com.lightbend.lagom.javadsl.pubsub.TopicId;
import org.springframework.util.Assert;
import play.Logger;
import se.olapetersson.voting.impl.commands.FailCommand;
import se.olapetersson.voting.impl.commands.RegisterVoteCommand;
import se.olapetersson.voting.impl.commands.VoteCommand;
import se.olapetersson.voting.impl.commands.VoteStandingsCommand;
import se.olapetersson.voting.impl.event.CloseEvent;
import se.olapetersson.voting.impl.event.VoteEvent;
import se.olapetersson.voting.impl.event.VoteRegisteredEvent;

import javax.annotation.PostConstruct;
import java.util.Optional;

public class VotingEntity extends PersistentEntity<VoteCommand, VoteEvent, Integer> {

    private final PubSubRegistry pubSubRegistry;

    @Inject
    public VotingEntity(PubSubRegistry pubSubRegistry) {
        Logger.info("Persist id in the constructor: {}", this.entityId());

        this.pubSubRegistry = pubSubRegistry;
    }

    @PostConstruct
    public void setUp(){
        Logger.info("persistentid in the post {}", this.entityId());
    }


    @SuppressWarnings("unchecked")
    @Override
    public Behavior initialBehavior(Optional<Integer> snapshotState) {
        PubSubRef<Integer> pubSubRef = pubSubRegistry.refFor(TopicId.of(Integer.class, entityId()));

        BehaviorBuilder behaviorBuilder = newBehaviorBuilder(
                snapshotState.orElse(50)
        );
        // eventHandler(OpenLobby -> this.id = event.getId();
        behaviorBuilder.setCommandHandler(RegisterVoteCommand.class,
            (cmd, ctx) -> {
                Assert.isTrue(cmd.getVoteValue() > Integer.MIN_VALUE);
                Assert.isTrue(cmd.getEpochUuid() > Integer.MIN_VALUE);

                Persist persistenceResult;
                Logger.info("Got a registerVoteCommand with value {}", cmd.getVoteValue());
                if(cmd.getVoteValue() != -1 && cmd.getVoteValue() != 1){
                    ctx.invalidCommand("The voteStream was tampered with! Value was: " + cmd.getVoteValue() +"|");

                    persistenceResult = ctx.done();
                } else{
                    persistenceResult = ctx.thenPersist(new VoteRegisteredEvent(cmd.getVoteValue(), cmd.getEpochUuid()), evt ->
                        ctx.reply(state()));
                }

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
                Logger.info("Applying VoteRegisteredEvent with value {} and user {}", evt.getVoteValue(), evt.getUserEpochId());
                Logger.info("State will be updated to: {}", state() + evt.getVoteValue());
                final Integer newState = state() + evt.getVoteValue();
                pubSubRef.publish(newState);
                return newState;
            }
        );
        
        behaviorBuilder.setEventHandlerChangingBehavior(CloseEvent.class, evt -> {
                    BehaviorBuilder closedBehavior = newBehaviorBuilder(0);
                    behaviorBuilder.setEventHandler(VoteRegisteredEvent.class, disregard -> {
                        throw new RuntimeException();
                    });
                    return closedBehavior.build();
                }
        );

        return behaviorBuilder.build();
    }

}
