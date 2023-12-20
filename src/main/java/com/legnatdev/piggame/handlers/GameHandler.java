package com.legnatdev.piggame.handlers;

import com.legnatdev.piggame.models.State;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class GameHandler implements WebSocketHandler {

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private static final State state = new State();
    private static final int WINNING_SCORE = 100;
    private static boolean player1Turn = true;
    private static boolean player2Turn = false;
    private static boolean player1Reset;
    private static boolean player2Reset;
    private static int currentScore = 0;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        if (state.player1() == null) {
            state.setPlayer1(session);
            session.sendMessage(new TextMessage("{'player': " + 1 + ",'currentScore':" + 0 + "}"));
        } else if (state.player2() == null) {
            state.setPlayer2(session);
            session.sendMessage(new TextMessage("{'player': " + 2 + ",'currentScore':" + 0 + "}"));
        }
        sessions.add(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        int roll = (int) (Math.random() * 6) + 1;
        String player = "";
        if (session == state.player1()) {
            player = "1";
        } else if (session == state.player2()) {
            player = "2";
        }
        if (message.getPayload().equals("roll")) {
            if (!validateTurn(session)) {
                return;
            }

            if (roll == 1) {
                currentScore = 0;
                swapTurns();
            } else {
                currentScore += roll;
            }

            String json = "{'player': " + player + ", 'roll':" + roll + ",'player1Score':" + state.score1() + ",'currentScore':" + currentScore + ", 'player2Score':" + state.score2() + ", " + "'action':" + 0 + "}";

            state.player1().sendMessage(new TextMessage(json));
            state.player2().sendMessage(new TextMessage(json));
        }

        if (message.getPayload().equals("hold")) {
            if (session == state.player1()) {
                state.setScore1(state.score1() + currentScore);
                swapTurns();
            } else if (session == state.player2()) {
                state.setScore2(state.score2() + currentScore);
                swapTurns();
            }

            String json = "{'player': " + player + ", 'roll':" + roll + ",'player1Score':" + state.score1() + ",'currentScore':" + currentScore + ", 'player2Score':" + state.score2() + ", 'action':" + 1 + "}";

            state.player1().sendMessage(new TextMessage(json));
            state.player2().sendMessage(new TextMessage(json));

            currentScore = 0;
        }

        if (message.getPayload().equals("reset")) {
            if (session == state.player1()) {
                player1Reset = true;
            } else if (session == state.player2()) {
                player2Reset = true;
            }

            if (player1Reset && player2Reset) {
                String json = "{'player': " + player + ", 'roll':" + roll + ",'player1Score':" + state.score1() + ",'currentScore':" + currentScore + ", 'player2Score':" + state.score2() + ", 'action':" + 2 + "}";
                state.player1().sendMessage(new TextMessage(json));
                state.player2().sendMessage(new TextMessage(json));
                state.setScore1(0);
                state.setScore2(0);
                currentScore = 0;
                player1Reset = false;
                player2Reset = false;
                player1Turn = true;
                player2Turn = false;
            }
        }
    }

    public boolean validateTurn(WebSocketSession session) {
        if (session == state.player1() && !player1Turn) {
            return false;
        } else return session != state.player2() || player2Turn;
    }

    public void swapTurns() {
        if (player1Turn) {
            player1Turn = false;
            player2Turn = true;
        } else {
            player1Turn = true;
            player2Turn = false;
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.out.println("Error: " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }
}
