package se.olapetersson.voting.impl.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lightbend.lagom.serialization.CompressedJsonable;
import org.springframework.util.Assert;

public class RegisterVoteCommand implements VoteCommand, CompressedJsonable{

    private final String votingName;
    private final String votingOption;

    //Serialization constructor

    @JsonCreator
    public RegisterVoteCommand(
            @JsonProperty("votingName") String votingName,
            @JsonProperty("votingOption") String votingOption) {
        Assert.hasText(votingName, "A vote has to be registered for a voting");
        Assert.hasText(votingOption, "A vote has to be cast on an option");
        this.votingName = votingName;
        this.votingOption = votingOption;
    }

    public String getVotingName() {
        return votingName;
    }

    public String getVotingOption() {
        return votingOption;
    }

    @Override
    public String getId() {
        return votingName;
    }
}