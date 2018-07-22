package com.backbase.assignment.Enums;

public enum GameStatus {
    CREATED("created"),
    IN_PROGRESS("inProgress"),
    COMPLETED("completed");


    private final String name;

    GameStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
