package se.olapetersson.voting.impl.command;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;
import se.olapetersson.voting.api.VotingState;

import java.io.Serializable;

public interface VoteCommand extends Jsonable, Serializable, PersistentEntity.ReplyType<VotingState> {
    String getId();
}
