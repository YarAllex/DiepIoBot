package net.yarAllex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer implements Runnable {
    public static final int MILLIS = 10;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private int port;
    private boolean run = true;

    public TcpServer(int port) {
        this.port = port;

        new Thread(this).start();
    }

    private void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void stop() throws IOException {
        this.run = false;
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }

    public void run() {
        try {
            start(port);
            System.out.println("Client is connected");
            while (run) {
                if (in.ready()) {
                    String msg = in.readLine();
                    System.out.printf("net.yarAllex.Message: %s\n", msg);
                }

                Thread.sleep(MILLIS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}