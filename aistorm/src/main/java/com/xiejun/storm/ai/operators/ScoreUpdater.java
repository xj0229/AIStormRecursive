package com.xiejun.storm.ai.operators;

import java.util.HashMap;
import java.util.Map;

import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.tuple.TridentTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xiejun.storm.ai.model.Board;

public class ScoreUpdater extends BaseFunction {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(ScoreUpdater.class);
    private static final Map<String, Integer> scores = new HashMap<String, Integer>();
    private static final String MUTEX = "MUTEX";

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        Board board = (Board) tuple.get(0);
        int score = tuple.getInteger(1);
        String player = tuple.getString(2);
        String key = board.toKey();
        LOG.debug("Got (" + board.toKey() + ") => [" + score + "] for [" + player + "]");

        // Always compute things from X's perspective
        // We'll flip things when we interpret it if it is O's turn.
        synchronized (MUTEX) {
            Integer currentScore = scores.get(key);
            if (currentScore == null || (player.equals("X") && score > currentScore)) {
                updateScore(board, score);
            } else if (player.equals("O") && score > currentScore) {
                updateScore(board, score);
            }
        }
    }

    public void updateScore(Board board, Integer score) {
        scores.put(board.toKey(), score);
        LOG.debug("Updating [" + board.toString() + "]=>[" + score + "]");
    }

}