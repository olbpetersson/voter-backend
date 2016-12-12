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

import java.util.Optional;

public class VotingEntity extends PersistentEntity<VoteCommand, VoteRegisteredEvent, Integer> {

    private final PubSubRef pubSubRef;

    @Inject
    public VotingEntity(PubSubRegistry pubSubRegistry) {
        this.pubSubRef = pubSubRegistry.refFor(TopicId.of(Integer.class, "myId"));
    }

    @Override
    public Behavior initialBehavior(Optional<Integer> snapshotState) {

        BehaviorBuilder behaviorBuilder = newBehaviorBuilder(
                snapshotState.orElse(50)
        );
        behaviorBuilder.setCommandHandler(RegisterVoteCommand.class,
            (cmd, ctx) -> {

                Assert.isTrue(cmd.getVoteValue() > Integer.MIN_VALUE);
                Assert.isTrue(cmd.getEpochUuid() > Integer.MIN_VALUE);

                Persist persistenceResult;
                Logger.info("Got a registerVoteCommand with value {}", cmd.getVoteValue());
                if(cmd.getVoteValue() != -1 && cmd.getVoteValue() != 1){
                    ctx.invalidCommand("The voteStream was tampered with! Value was: " + cmd.getVoteValue() +"|");
                    pubSubRef.publish("BANNED!");
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
                pubSubRef.publish(state());
                ctx.reply(state());
            }
        );


        behaviorBuilder.setEventHandler(VoteRegisteredEvent.class, evt ->{
            Logger.info("Applying VoteRegisteredEvent with value {} and user {}", evt.getVoteValue(), evt.getUserEpochId());
            Logger.info("State will be updated to: {}", state() + evt.getVoteValue());
            final Integer newState = state() + evt.getVoteValue();
            pubSubRef.publish(newState);
            return newState;
        }
        );

        return behaviorBuilder.build();
    }

}
