package com.backbase.assignment.Entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "game")
public class Game implements Serializable {

    private static final long serialVersionUID = -3009157732242241606L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "game_id")
    private String gameId;

    @Column(name = "status")
    private String status;

    @Column(name = "pits")
    private String pitJson;

    @Column(name = "current_player")
    private String currentPlayer;

    public Game(String gameId, String status, String pitJson, String currentPlayer) {
        this.gameId = gameId;
        this.status = status;
        this.pitJson = pitJson;
        this.currentPlayer = currentPlayer;
    }

    public Game() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getPitJson() {
        return pitJson;
    }

    public void setPitJson(String pitJson) {
        this.pitJson = pitJson;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", gameId='" + gameId + '\'' +
                ", status='" + status + '\'' +
                ", pitJson='" + pitJson + '\'' +
                ", currentPlayer='" + currentPlayer + '\'' +
                '}';
    }
}
