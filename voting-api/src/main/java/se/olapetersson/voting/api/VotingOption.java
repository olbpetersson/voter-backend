package se.olapetersson.voting.api;

import org.springframework.util.Assert;

/**
 * Created by ola on 2017-06-11.
 */
public class VotingOption {

    private int nrOfVotes;
    private final String votingName;

    private VotingOption(String votingName) {
        Assert.hasText(votingName, "A voting name needs to be set");
        this.nrOfVotes = 0;
        this.votingName = votingName;
    }

    public void addVote() {
        this.nrOfVotes++;
    }

    public int getNrOfVotes() {
        return nrOfVotes;
    }

    public String getVotingName() {
        return votingName;
    }

    public static VotingOption create(String votingName) {
        return new VotingOption(votingName);
    }

    @Override
    public String toString() {
        return "VotingOption{" +
                "nrOfVotes=" + nrOfVotes +
                ", votingName='" + votingName + '\'' +
                '}';
    }
}
