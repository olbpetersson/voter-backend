package se.olapetersson.voting.impl.commands;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;

public class UnknownCommand implements VoteCommand, CompressedJsonable{
}
