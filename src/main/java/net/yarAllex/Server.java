package net.yarAllex;

import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.Scanner;

public class Server {

    public static final int time = 500;
    public static final int port = 8087;
    public static final String host = "127.0.0.1";
    private static Robot robot;

    static {
        nu.pattern.OpenCV.loadShared();
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws AWTException, InterruptedException, IOException, URISyntaxException {
        TcpServer2 server = new TcpServer2(new InetSocketAddress(host, port));
        server.start();

//        net.yarAllex.opencv.Handler handler = new net.yarAllex.opencv.Handler(server.getQueue());
        System.out.println("Press enter to stop...");
        System.in.read();

        server.stop();
//        handler.stop();
    }

    private static void testmove() throws InterruptedException {
        for (int i = 100; i > 0; i--) {
            Random random = new Random();
            int key = random.nextInt(4);
            System.out.printf("Key=%d\n", key);
            driveTo(37+key);

//            driveTo(KeyEvent.VK_DOWN);
//
//            driveTo(KeyEvent.VK_LEFT);
//
//            driveTo(KeyEvent.VK_UP);
        }
    }

    private static void driveTo(int vkDown) throws InterruptedException {
        robot.keyPress(vkDown);
        Thread.sleep(time);
        robot.keyRelease(vkDown);
    }
}
