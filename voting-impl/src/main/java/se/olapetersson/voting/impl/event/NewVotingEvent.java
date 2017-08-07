package se.olapetersson.voting.impl.event;

import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;

/**
 * Created by ola on 2017-06-11.
 */
public class NewVotingEvent implements VoteEvent, AggregateEvent<NewVotingEvent>{
    private final String votingName;
    private final String description;
    private final String votingOptionA;
    private final String votingOptionB;
    private final int closeAfter;
    private final String createdBy;

    private static int NUM_SHARDS = 1 ;
    public static final AggregateEventShards<NewVotingEvent> TAG_INSTANCE =
            AggregateEventTag.sharded(NewVotingEvent.class, NUM_SHARDS);

    public NewVotingEvent(String votingName, String description, String votingOptionA, String votingOptionB, int closeAfter, String createdBy) {
        this.votingName = votingName;
        this.description = description;
        this.votingOptionA = votingOptionA;
        this.votingOptionB = votingOptionB;
        this.closeAfter = closeAfter;
        this.createdBy = createdBy;
    }

    public String getVotingName() {
        return votingName;
    }

    public String getDescription() {
        return description;
    }

    public String getVotingOptionA() {
        return votingOptionA;
    }

    public String getVotingOptionB() {
        return votingOptionB;
    }

    public int getCloseAfter() {
        return closeAfter;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public AggregateEventTagger<NewVotingEvent> aggregateTag() {
        return TAG_INSTANCE;
    }
}
