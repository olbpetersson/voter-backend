package se.olapetersson.voting.api;

import java.util.UUID;

public class VotingState  {

    private final String votingName;
    private VotingOption votingOptionA;
    private VotingOption votingOptionB;
    private String createdBy;
    private UUID uuid;

    private VotingState(String votingName, VotingOption votingOptionA, VotingOption votingOptionB, String createdBy) {
        this.votingName = votingName;
        this.votingOptionA = votingOptionA;
        this.votingOptionB = votingOptionB;
        this.createdBy = createdBy;
        this.uuid = UUID.randomUUID();
    }

    public String getVotingName() {
        return votingName;
    }

    public VotingOption getVotingOptionA() {
        return votingOptionA;
    }

    public VotingOption getVotingOptionB() {
        return votingOptionB;
    }

    public static VotingState createVotingState(String votingName, VotingOption votingOptionA, VotingOption votingOptionB,
                                                String createdBy) {
        return new VotingState(votingName, votingOptionA, votingOptionB, createdBy);
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "VotingState{" +
                "votingName='" + votingName + '\'' +
                ", votingOptionA=" + votingOptionA +
                ", votingOptionB=" + votingOptionB +
                '}';
    }

    public String getCreatedBy() {
        return createdBy;
    }
}
