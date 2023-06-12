package net.yarAllex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpClient {
    private int port;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    public TcpClient(int port) throws IOException {
        this.port = port;
        socket = new Socket("127.0.0.1", port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void write(String data) {
        out.println(data);
    }

    public void stop() throws IOException {
        out.close();
        in.close();
        socket.close();
    }
}
