package net.yarAllex;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class TcpClient2 extends WebSocketClient {

    public TcpClient2(URI serverUri) {
        super(serverUri);
    }

    public void onOpen(ServerHandshake serverHandshake) {

    }

    public void onMessage(String s) {

    }

    public void onClose(int i, String s, boolean b) {

    }

    public void onError(Exception e) {

    }
}
