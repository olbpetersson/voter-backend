package se.olapetersson.voting.impl.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VoteStandingsCommand implements VoteCommand {
    private final String votingName;

    private VoteStandingsCommand(String votingName) {
        this.votingName = votingName;
    }

    @JsonCreator
    public static VoteStandingsCommand create(@JsonProperty("votingName") String votingName) {
        return new VoteStandingsCommand(votingName);
    }

    @Override
    public String getId() {
        return votingName;
    }
}
