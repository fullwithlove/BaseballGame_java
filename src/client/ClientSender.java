package client;

import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

public class ClientSender extends Thread {
    private final BlockingQueue<String> queue;
    private final PrintWriter out;

    public ClientSender(BlockingQueue<String> queue, PrintWriter out) {
        this.queue = queue;
        this.out = out;
    }

    public void run() {
        try {
            while (true) {
                String msg = queue.take();
                out.println(msg);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
