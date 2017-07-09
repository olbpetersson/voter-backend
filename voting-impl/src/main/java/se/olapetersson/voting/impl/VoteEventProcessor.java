/*
package se.olapetersson.voting.impl;

import akka.Done;
import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.PSequence;
import play.Logger;
import se.olapetersson.voting.impl.event.VoteRegisteredEvent;

import java.util.List;
import java.util.concurrent.CompletionStage;

import static com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide.completedStatement;

public class VoteEventProcessor extends ReadSideProcessor<VoteRegisteredEvent>{

    private final CassandraSession session;
    private final CassandraReadSide readSide;
    private PreparedStatement writeEvent;

    @Inject
    public VoteEventProcessor(CassandraSession session, CassandraReadSide readSide) {
        this.session = session;
        this.readSide = readSide;
    }

    @Override
    public ReadSideHandler<VoteRegisteredEvent> buildHandler() {
        return readSide.<VoteRegisteredEvent>builder("vote_offset")
                .setGlobalPrepare(this::prepareCreateTable)
                .setPrepare((ignored) -> prepareWriteVote())
                .setEventHandler(VoteRegisteredEvent.class, this::processVoteRegistered)
                .build();
    }

    private CompletionStage<List<BoundStatement>> processVoteRegistered(VoteRegisteredEvent voteRegisteredEvent){
        Logger.info("Processing an event for readside");
        BatchStatement batchStatement = new BatchStatement();
        BoundStatement bindWriteEvent = writeEvent.bind();
        bindWriteEvent.setLong("user", voteRegisteredEvent.getUserEpochId());
        batchStatement.add(bindWriteEvent);
        return completedStatement(bindWriteEvent);
    }

    private CompletionStage<Done> prepareWriteVote() {
       return session.prepare(
                  "INSERT INTO participants (user) " +
                       "VALUES (?)"
                )
               .thenApply(ps -> {
                   setWriteEvent(ps);
                   return Done.getInstance();
               });
    }

    private CompletionStage<Done> prepareCreateTable() {
        Logger.info("Creating voteStream table");
        return session.executeCreateTable(
                "CREATE TABLE IF NOT EXISTS participants("
                + "user bigint,"
                + "PRIMARY KEY (user) )");
    }

    @Override
    public PSequence<AggregateEventTag<VoteRegisteredEvent>> aggregateTags() {
        return VoteRegisteredEvent.TAG_INSTANCE.allTags();
    }

    public void setWriteEvent(PreparedStatement writeEvent) {
        this.writeEvent = writeEvent;
    }
}
*/
