package se.olapetersson.voting.impl;

import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import se.olapetersson.voting.impl.commands.VoteCommand;

import java.io.Serializable;
import java.util.UUID;

public class VoteRegisteredEvent implements VoteCommand, Serializable, AggregateEvent<VoteRegisteredEvent> {
    private int voteValue;
    private UUID uuid;
    private long userEpochId;

    private static int NUM_SHARDS =1 ;
    public static final AggregateEventShards<VoteRegisteredEvent> TAG_INSTANCE =
            AggregateEventTag.sharded(VoteRegisteredEvent.class, NUM_SHARDS);

    public VoteRegisteredEvent(){
        this.uuid = UUID.randomUUID();
        this.voteValue = 0;
    }
    public VoteRegisteredEvent(int voteValue, long userEpochId) {
        this();
        this.voteValue = voteValue;
        this.userEpochId = userEpochId;
    }

    public int getVoteValue() {
        return voteValue;
    }

    public long getUserEpochId() {
        return userEpochId;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public AggregateEventTagger<VoteRegisteredEvent> aggregateTag() {
        return TAG_INSTANCE;
    }
}
