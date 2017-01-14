package com.xiejun.storm.ai.topology;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.trident.Stream;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.tuple.Fields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiejun.storm.ai.model.Board;
import com.xiejun.storm.ai.model.GameState;
import com.xiejun.storm.ai.operators.ScoreFunction;
import com.xiejun.storm.ai.operators.ScoreUpdater;
import com.xiejun.storm.ai.operators.isEndGame;
import com.xiejun.storm.ai.spout.LocalQueueEmitter;
import com.xiejun.storm.ai.spout.LocalQueueSpout;

public class ScoringTopology {
    private static final Logger LOG = LoggerFactory.getLogger(ScoringTopology.class);

    public static StormTopology buildTopology() {
        LOG.info("Building topology.");
        TridentTopology topology = new TridentTopology();

        GameState exampleRecursiveState = GameState.playAtRandom(new Board(), "X");
        LOG.info("SIMULATED LEAF NODE : [" + exampleRecursiveState.getBoard() + "] w/ state [" + exampleRecursiveState + "]");

        // Scoring Queue / Spout
        LocalQueueEmitter<GameState> scoringSpoutEmitter = new LocalQueueEmitter<GameState>("ScoringQueue");
        scoringSpoutEmitter.enqueue(exampleRecursiveState);
        LocalQueueSpout<GameState> scoringSpout = new LocalQueueSpout<GameState>(scoringSpoutEmitter);

        Stream inputStream = topology.newStream("scoring", scoringSpout);

        inputStream.each(new Fields("gamestate"), new isEndGame())
                .each(new Fields("gamestate"),
                        new ScoreFunction(),
                        new Fields("board", "score", "player"))
                .each(new Fields("board", "score", "player"), new ScoreUpdater(), new Fields());
        return topology.build();
    }

    public static void main(String[] args) throws Exception {
        final Config conf = new Config();
        final LocalCluster cluster = new LocalCluster();

        LOG.info("Submitting topology.");
        cluster.submitTopology("scoringTopology", conf, ScoringTopology.buildTopology());
        LOG.info("Topology submitted.");
        Thread.sleep(600000);
    }
}
