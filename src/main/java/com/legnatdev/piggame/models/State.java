package com.legnatdev.piggame.models;

import org.springframework.web.socket.WebSocketSession;

public class State {
    private WebSocketSession player1;
    private WebSocketSession player2;
    private int score1;
    private int score2;

    public State() {
        this.player1 = null;
        this.player2 = null;
        this.score1 = 0;
        this.score2 = 0;
    }

    public WebSocketSession player1() {
        return player1;
    }

    public WebSocketSession player2() {
        return player2;
    }

    public int score1() {
        return score1;
    }

    public int score2() {
        return score2;
    }

    public void setPlayer1(WebSocketSession player1) {
        this.player1 = player1;
    }

    public void setPlayer2(WebSocketSession player2) {
        this.player2 = player2;
    }

    public void setScore1(int score1) {
        this.score1 = score1;
    }

    public void setScore2(int score2) {
        this.score2 = score2;
    }
}