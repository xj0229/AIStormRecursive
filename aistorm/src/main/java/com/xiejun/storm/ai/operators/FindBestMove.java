package com.xiejun.storm.ai.operators;

import org.apache.storm.trident.operation.BaseAggregator;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Values;

import com.esotericsoftware.minlog.Log;
import com.xiejun.storm.ai.model.BestMove;
import com.xiejun.storm.ai.model.Board;

public class FindBestMove extends BaseAggregator<BestMove> {
    private static final long serialVersionUID = 1L;

    @Override
    public BestMove init(Object batchId, TridentCollector collector) {
        Log.info("Batch Id = [" + batchId + "]");
        return new BestMove();
    }

    @Override
    public void aggregate(BestMove currentBestMove, TridentTuple tuple, TridentCollector collector) {
        Board board = (Board) tuple.get(0);
        Integer score = tuple.getInteger(1);
        if (score > currentBestMove.score) {
            currentBestMove.score = score;
            currentBestMove.bestMove = board;
        }
    }

    @Override
    public void complete(BestMove bestMove, TridentCollector collector) {
        collector.emit(new Values(bestMove));
    }

}
