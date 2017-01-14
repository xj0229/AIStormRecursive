package com.xiejun.storm.ai.operators;

import java.util.ArrayList;
import java.util.List;

import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.tuple.TridentTuple;

import com.esotericsoftware.minlog.Log;
import com.xiejun.storm.ai.model.Board;
import com.xiejun.storm.ai.model.GameState;

public class ArgsFunction extends BaseFunction {
    private static final long serialVersionUID = 1L;

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        String args = tuple.getString(0);
        Log.info("Executing DRPC w/ args = [" + args + "]");
        Board board = new Board(args);
        GameState gameState = new GameState(board, new ArrayList<Board>(), "X");
        Log.info("Emitting [" + gameState + "]");

        List<Object> values = new ArrayList<Object>();
        values.add(gameState);
        collector.emit(values);
    }
}