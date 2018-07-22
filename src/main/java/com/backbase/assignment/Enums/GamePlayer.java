package com.backbase.assignment.Enums;

public enum GamePlayer {
    DOWN("player1"),
    UP("player2"),
    NO_PLAYER("gameOver");

    private final String name;

    GamePlayer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
