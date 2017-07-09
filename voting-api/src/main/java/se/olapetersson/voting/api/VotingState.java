package se.olapetersson.voting.api;

import java.util.UUID;

public class VotingState  {

    private final String votingName;
    private final String description;
    private final VotingOption votingOptionA;
    private final VotingOption votingOptionB;
    private final String createdBy;
    private final int closeAfter;
    private final UUID uuid;

    private VotingState(String votingName, String description, VotingOption votingOptionA, VotingOption votingOptionB, String createdBy, int closeAfter) {
        this.votingName = votingName;
        this.description = description;
        this.votingOptionA = votingOptionA;
        this.votingOptionB = votingOptionB;
        this.createdBy = createdBy;
        this.closeAfter = closeAfter;
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

    public static VotingState createVotingState(String votingName, String description, VotingOption votingOptionA, VotingOption votingOptionB,
                                                int closeAfter, String createdBy) {
        return new VotingState(votingName, description, votingOptionA, votingOptionB, createdBy, closeAfter);
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getDescription() {
        return description;
    }

    public int getCloseAfter() {
        return closeAfter;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public String toString() {
        return "VotingState{" +
                "votingName='" + votingName + '\'' +
                ", description='" + description + '\'' +
                ", votingOptionA=" + votingOptionA +
                ", votingOptionB=" + votingOptionB +
                ", createdBy='" + createdBy + '\'' +
                ", closeAfter=" + closeAfter +
                ", uuid=" + uuid +
                '}';
    }
}
