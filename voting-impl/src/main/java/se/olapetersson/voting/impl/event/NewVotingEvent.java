package se.olapetersson.voting.impl.event;

/**
 * Created by ola on 2017-06-11.
 */
public class NewVotingEvent implements VoteEvent{
    private final String votingName;
    private final String votingOptionA;
    private final String votingOptionB;
    private final String createdBy;

    public NewVotingEvent(String votingName, String votingOptionA, String votingOptionB, String createdBy) {

        this.votingName = votingName;
        this.votingOptionA = votingOptionA;
        this.votingOptionB = votingOptionB;
        this.createdBy = createdBy;
    }

    public String getVotingName() {
        return votingName;
    }

    public String getVotingOptionA() {
        return votingOptionA;
    }

    public String getVotingOptionB() {
        return votingOptionB;
    }

    public String getCreatedBy() {
        return createdBy;
    }
}
