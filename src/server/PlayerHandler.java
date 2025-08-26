package server;

import java.io.*;
import java.net.Socket;
import common.JudgeUtil;

public class PlayerHandler extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter  out;
    private GameRoom room;

    public PlayerHandler(Socket socket, GameRoom room, boolean isFirst) {
        this.socket = socket;
        this.room   = room;
        room.setPlayer(this, isFirst);   // ★ 변경-점
    }

    public void sendMessage(String msg) { out.println(msg); }

    @Override
    public void run() {
        try {
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("SECRET|")) {
                    String secret = line.substring(7);
                    if (JudgeUtil.isValid(secret)) {
                        room.setSecret(this, secret);
                        sendMessage("WAITING");
                        if (room.readyToStart()) room.startGame();
                    } else {
                        sendMessage("INVALID_SECRET");
                    }
                } else if (line.startsWith("GUESS|")) {
                    room.handleGuess(this, line.substring(6));
                }
            }
        } catch (IOException e) {
            System.out.println("플레이어 접속 종료");
            room.playerDisconnected(this);  //GameRoom에 알림
        }
    }

}
