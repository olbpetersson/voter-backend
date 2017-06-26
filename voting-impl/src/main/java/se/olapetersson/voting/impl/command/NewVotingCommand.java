package se.olapetersson.voting.impl.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NewVotingCommand implements VoteCommand {

    private final String votingName;
    private final String description;
    private final String votingOptionA;
    private final String votingOptionB;
    private final String jwtToken;

    @JsonCreator
    public NewVotingCommand(@JsonProperty("votingName") String votingName,
                            @JsonProperty("description") String description,
                            @JsonProperty("votingOptionA") String votingOptionA,
                            @JsonProperty("votingOptionB") String votingOptionB,
                            @JsonProperty("token") String token) {
        this.votingName = votingName;
        this.description = description;
        this.votingOptionA = votingOptionA;
        this.votingOptionB = votingOptionB;
        this.jwtToken = token;
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

    public String getJwtToken() {
        return jwtToken;
    }

    @Override
    public String toString() {
        return "NewVotingCommand{" +
                "votingName='" + votingName + '\'' +
                ", description='" + description + '\'' +
                ", votingOptionA='" + votingOptionA + '\'' +
                ", votingOptionB='" + votingOptionB + '\'' +
                ", jwtToken='" + jwtToken + '\'' +
                '}';
    }

    @Override
    public String getId() {
        return this.votingName;
    }
}
