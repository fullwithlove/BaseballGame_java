package server;

import java.net.*;

public class BaseballServer {
	static int oddWins = 0, oddLosses = 0, oddDraws = 0;
	static int evenWins = 0, evenLosses = 0, evenDraws = 0;
	static int sessionCount = 0;

    public static void main(String[] args) {
        try (ServerSocket ss = new ServerSocket(5000)) {
            System.out.println("Server on 5000 …");

            while (true) {
                // 두 명이 모이면 한 게임룸
                Socket s1 = ss.accept();
                Socket s2 = ss.accept();
                
                sessionCount++;
                
                GameRoom room = new GameRoom();

                PlayerHandler pA = new PlayerHandler(s1, room, true);   // 선공
                PlayerHandler pB = new PlayerHandler(s2, room, false);  // 후공

                pA.start();
                pB.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
