package se.olapetersson.voting.impl.commands;

import com.lightbend.lagom.serialization.CompressedJsonable;
import org.springframework.util.Assert;

public class RegisterVoteCommand implements VoteCommand, CompressedJsonable{

    private final int voteValue;
    private final long epochUuid;

    //Serialization constructor
    public RegisterVoteCommand() {
        Assert.notNull(this.getEpochUuid());
        Assert.notNull(this.getVoteValue());
        epochUuid = Integer.MIN_VALUE;
        this.voteValue = Integer.MIN_VALUE;
    }

    public RegisterVoteCommand(int voteValue, long epochUuid) {
        this.epochUuid = epochUuid;
        this.voteValue = voteValue;
    }

    public long getEpochUuid() {
        return epochUuid;
    }

    public int getVoteValue() {
        return voteValue;
    }
}