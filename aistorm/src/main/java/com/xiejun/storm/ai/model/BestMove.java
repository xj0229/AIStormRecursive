package com.xiejun.storm.ai.model;

public class BestMove {
    public Board bestMove;
    public Integer score = Integer.MIN_VALUE;

    public String toString() {
        return bestMove.toString() + "[" + score + "]";
    }
}
