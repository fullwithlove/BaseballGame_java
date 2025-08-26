package server;

import server.PlayerHandler;
import common.JudgeUtil;

public class GameRoom {
    private PlayerHandler playerA;
    private PlayerHandler playerB;
    private String secretA;
    private String secretB;
    private PlayerHandler currentTurn;

    private int attemptA = 0, attemptB = 0;
    private boolean solvedA = false, solvedB = false;

    public void setPlayer(PlayerHandler player, boolean isFirst) {
        if (isFirst) {
            playerA = player;
            currentTurn = playerA;
        } else {
            playerB = player;
        }
    }

    public boolean readyToStart() {
        return (playerA != null && playerB != null && secretA != null && secretB != null);
    }

    public void startGame() {
        playerA.sendMessage("START");
        playerB.sendMessage("START");

        currentTurn.sendMessage("YOURTURN");
    }

    public void setSecret(PlayerHandler player, String secret) {
        if (player == playerA)  secretA = secret;

        else if (player == playerB) secretB = secret;
    }

    public String getOpponentSecret(PlayerHandler player) {
        return player == playerA ? secretB : secretA;
    }

    public PlayerHandler getOpponent(PlayerHandler player) {
        return player == playerA ? playerB : playerA;
    }

    public synchronized void handleGuess(PlayerHandler p, String guess) {

        if (p != currentTurn) {                // 턴이 아니면 무시
            p.sendMessage("NOT_YOUR_TURN");
            return;
        }

        // 판정 
        String answer = getOpponentSecret(p);
        String result = JudgeUtil.judge(answer, guess);

        if (p == playerA) attemptA++; else attemptB++;

        //결과 전송 
        p.sendMessage("RESULT|" + guess + "|" + result);
        getOpponent(p).sendMessage("ENEMY|" + guess + "|" + result);

        // 3S 여부 저장
        if (result.startsWith("3S")) {
            if (p == playerA) solvedA = true;
            else               solvedB = true;
        }
        
        // 결과 예: "2S 0B"
        if (result.startsWith("2S")) {
            getOpponent(p).sendMessage("ALERT|2S");
        }
 
        // 승패 / 무승부 판정
        if (attemptA == attemptB) {
            if (solvedA && solvedB) {            // 둘 다 맞춤 → 무승부
                playerA.sendMessage("DRAW");
                playerB.sendMessage("DRAW");
                return;
            } else if (solvedA) {                // A만 맞춤
                playerA.sendMessage("WIN");
                playerB.sendMessage("LOSE");
                return;
            } else if (solvedB) {                // B만 맞춤
                playerB.sendMessage("WIN");
                playerA.sendMessage("LOSE");
                return;
            }
        }

        // ⑤ 게임 계속 -> 턴 교대
        switchTurn();
        currentTurn.sendMessage("YOURTURN");
    }

    private void switchTurn() {
        currentTurn = (currentTurn == playerA) ? playerB : playerA;
    }

    public void playerDisconnected(PlayerHandler disconnectedPlayer) {
        PlayerHandler opponent = getOpponent(disconnectedPlayer);

        if (opponent != null) {
            opponent.sendMessage("DISCONNECTION");
        }

        playerA = null;
        playerB = null;
        secretA = null;
        secretB = null;
        currentTurn = null;
        attemptA = 0;
        attemptB = 0;
        solvedA = false;
        solvedB = false;

        System.out.println("게임방 종료됨: 한 명이 연결 종료");
    }
}
