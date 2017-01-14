package com.xiejun.storm.ai.topology;

import java.util.ArrayList;

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
import com.xiejun.storm.ai.operators.GenerateBoards;
import com.xiejun.storm.ai.operators.isEndGame;
import com.xiejun.storm.ai.spout.LocalQueueEmitter;
import com.xiejun.storm.ai.spout.LocalQueueSpout;
import com.xiejun.storm.ai.spout.LocalQueuerFunction;

public class RecursiveTopology {
    private static final Logger LOG = LoggerFactory.getLogger(RecursiveTopology.class);

    public static StormTopology buildTopology() {
        LOG.info("Building topology.");
        TridentTopology topology = new TridentTopology();

        // Work Queue / Spout
        LocalQueueEmitter<GameState> workSpoutEmitter = new LocalQueueEmitter<GameState>("WorkQueue");
        LocalQueueSpout<GameState> workSpout = new LocalQueueSpout<GameState>(workSpoutEmitter);
        GameState initialState = new GameState(new Board(), new ArrayList<Board>(), "X");
        workSpoutEmitter.enqueue(initialState);

        // Scoring Queue / Spout
        LocalQueueEmitter<GameState> scoringSpoutEmitter = new LocalQueueEmitter<GameState>("ScoringQueue");

        Stream inputStream = topology.newStream("gamestate", workSpout);

        inputStream.each(new Fields("gamestate"), new isEndGame())
                .each(new Fields("gamestate"),
                        new LocalQueuerFunction<GameState>(scoringSpoutEmitter),
                        new Fields(""));

        inputStream.each(new Fields("gamestate"), new GenerateBoards(), new Fields("children"))
                .each(new Fields("children"),
                        new LocalQueuerFunction<GameState>(workSpoutEmitter),
                        new Fields());

        return topology.build();
    }

    public static void main(String[] args) throws Exception {
        final Config conf = new Config();
        final LocalCluster cluster = new LocalCluster();

        LOG.info("Submitting topology.");
        cluster.submitTopology("recursiveTopology", conf, RecursiveTopology.buildTopology());
        LOG.info("Topology submitted.");
        Thread.sleep(600000);
    }
}
