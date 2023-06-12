package net.yarAllex;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TcpServer2 extends WebSocketServer {

    private BlockingQueue<ImageData> queue = new LinkedBlockingQueue<>();

    private ObjectMapper objectMapper = new ObjectMapper();

    Logger log = LoggerFactory.getLogger("net.yarAllex.TcpServer2");

    public TcpServer2(InetSocketAddress address) {
        super(address);
    }

    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        log.info("Server onOpen");
    }

    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        log.info("Server onClose");
    }

    public void onMessage(WebSocket webSocket, String s) {
        try {
            Message message = objectMapper.readValue(s, Message.class);
            log.info("Server onMessage: {}", message.getMessage());
            if (Message.Type.sendCanvas.name().equals(message.getMessage())) {
                ImageData data = objectMapper.convertValue(message.getData(), ImageData.class);
                queue.put(data);
            }
        } catch (JsonProcessingException | InterruptedException e) {
            log.error("Can not convert message", e);
        }
    }

    private Wall createWall(ImageData data) {
        Wall wall = new Wall();
        List<Pixel> pixels = data.getPixels();
        Color wallColor = wall.getColor();
        for (int i = 0; i < pixels.size(); i++) {
            Color pixelColor = pixels.get(i).getColor();
            if (wallColor.equals(pixelColor)) {
                wall.getCoordinates().add(pixels.get(i));
            }
        }
        log.debug("net.yarAllex.Wall size: " + wall.getCoordinates().size());

        return wall;
    }

    public BlockingQueue<ImageData> getQueue() {
        return queue;
    }

    public void onError(WebSocket webSocket, Exception e) {
        log.error("Server onError:", e);
    }

    public void onStart() {
        log.info("Server onStart");
    }
}
