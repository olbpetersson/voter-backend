package se.olapetersson.voting.impl.event;

import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;

import java.util.UUID;

public class VoteRegisteredEvent implements VoteEvent, AggregateEvent<VoteRegisteredEvent> {
    private String voteName;
    //Eventually used for a user connection later
    private long userEpochId;

    private static int NUM_SHARDS = 1 ;
    public static final AggregateEventShards<VoteRegisteredEvent> TAG_INSTANCE =
            AggregateEventTag.sharded(VoteRegisteredEvent.class, NUM_SHARDS);

    public VoteRegisteredEvent(String voteName, long userEpochId) {
        this.voteName = voteName;
        this.userEpochId = userEpochId;
    }

    public String getVoteName() {
        return voteName;
    }

    public long getUserEpochId() {
        return userEpochId;
    }

    @Override
    public AggregateEventTagger<VoteRegisteredEvent> aggregateTag() {
        return TAG_INSTANCE;
    }
}
