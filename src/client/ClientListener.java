package client;

import java.io.*;
import java.net.Socket;
import javax.swing.JOptionPane;

public class ClientListener extends Thread {
    private final Socket socket;
    private final BaseballUI ui;

    public ClientListener(Socket socket, BaseballUI ui) {
        this.socket = socket;
        this.ui = ui;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                //System.out.println("[수신된 메시지] " + line);
                if (line.startsWith("WAITING")) {
                    ui.appendLog("상대방을 기다리는 중입니다...");
                } else if (line.startsWith("START")) {
                    ui.appendLog("게임이 시작되었습니다.");
                } else if (line.equals("YOURTURN")) {
                    ui.appendLog("당신의 턴입니다.");
                    ui.enableGuess(true);
                } else if (line.equals("NOT_YOUR_TURN")) {
                    ui.appendLog("차례가 아닙니다.");
                } else if (line.startsWith("RESULT")) {
                    String[] parts = line.split("\\|");
                    ui.appendLog("내가 " + parts[1] + " → 결과: " + parts[2]);
                } else if (line.startsWith("ENEMY")) {
                    String[] parts = line.split("\\|");
                    ui.appendLog("상대가 " + parts[1] + " → 결과: " + parts[2]);
                } else if (line.equals("WIN")) {
                    ui.appendLog("승리했습니다!");
                } else if (line.startsWith("LOSE")) {
                    String[] parts = line.split("\\|");
                    ui.appendLog("패배했습니다...");
                } else if (line.equals("DRAW")) {
                    ui.appendLog("무승부입니다!");
                }
                else if (line.startsWith("ALERT|2S")) {
                    JOptionPane.showMessageDialog(null,
                        "상대방이 당신의 숫자에 거의 근접했습니다!\n(2 스트라이크)",
                        "경고",
                        JOptionPane.WARNING_MESSAGE);
                }

                else if (line.equals("DISCONNECTION")) {
                    ui.appendLog("상대방이 연결을 종료했습니다.");
                    ui.enableGuess(false);
                }
            }
        } catch (Exception e) {
            ui.appendLog("서버와의 연결이 끊겼습니다.");
            e.printStackTrace();
        }
    }
}
