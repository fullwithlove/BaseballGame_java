package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import common.JudgeUtil;

public class BaseballUI extends JFrame {
    private JTextField secretField = new JTextField(5);
    private JTextField guessField = new JTextField(5);
    private JTextArea logArea = new JTextArea();
    private JButton sendSecret = new JButton("확정");
    private JButton sendGuess = new JButton("전송");

    private PrintWriter out;
    private BlockingQueue<String> sendQueue = new LinkedBlockingQueue<>();

    public BaseballUI() {
        setTitle("숫자야구 게임 클라이언트");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel for secret input
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("비밀 숫자(3자리):"));
        topPanel.add(secretField);
        topPanel.add(sendSecret);
        add(topPanel, BorderLayout.NORTH);

        // Center log area
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        // Bottom panel for guessing
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(new JLabel("추측 숫자:"));
        bottomPanel.add(guessField);
        bottomPanel.add(sendGuess);
        sendGuess.setEnabled(false);
        add(bottomPanel, BorderLayout.SOUTH);

        sendSecret.addActionListener(e -> {
            String secret = secretField.getText().trim();
            if (secret.length() == 3 && JudgeUtil.isValid(secret)) {
                sendQueue.offer("SECRET|" + secret);
                secretField.setEditable(false);
                sendSecret.setEnabled(false);
            } else {
                appendLog("비밀 숫자는 서로 다른 3자리 숫자여야 합니다.");
            }
        });

        sendGuess.addActionListener(e -> {
            String guess = guessField.getText().trim();
            if (guess.length() == 3 && JudgeUtil.isValid(guess)) {
                sendQueue.offer("GUESS|" + guess);
                guessField.setText("");
                sendGuess.setEnabled(false);
            } else {
                appendLog("추측 숫자는 서로 다른 3자리 숫자여야 합니다.");
            }
        });


        setVisible(true);

        new Thread(() -> {
            try {
                Socket socket = new Socket("localhost", 5000);
                out = new PrintWriter(socket.getOutputStream(), true);

                new ClientListener(socket, this).start();
                new ClientSender(sendQueue, out).start();
            } catch (Exception e) {
                appendLog("서버에 연결할 수 없습니다.");
                e.printStackTrace();
            }
        }).start();
    }

    public void appendLog(String msg) {
        SwingUtilities.invokeLater(() -> logArea.append(msg + "\n"));
    }

    public void enableGuess(boolean on) {
        SwingUtilities.invokeLater(() -> {
            sendGuess.setEnabled(on);
            guessField.setEditable(on);
        });
    }
    public void enableSecretInput(boolean on) {
        SwingUtilities.invokeLater(() -> {
            secretField.setEditable(on);
            sendSecret.setEnabled(on);
        });
    }

}

