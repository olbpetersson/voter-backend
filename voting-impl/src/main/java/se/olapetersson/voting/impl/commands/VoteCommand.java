package se.olapetersson.voting.impl.commands;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;

import java.io.Serializable;

public interface VoteCommand extends Jsonable, Serializable, PersistentEntity.ReplyType<Integer> {
}
