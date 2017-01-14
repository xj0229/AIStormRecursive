package com.xiejun.storm.ai.model;

public class Player {
    public static String next(String current) {
        if (current.equals("X")) return "O";
        else return "X";
    }
}
