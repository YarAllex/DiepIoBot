package net.yarAllex.capture;

import net.yarAllex.opencv.Handler;
import net.yarAllex.gui.MainWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

public class ScreenCaptureTest {

    private static final Logger log = LoggerFactory.getLogger("ScreenCaptureTest");

    public static void main(String[] args) throws InterruptedException {
        nu.pattern.OpenCV.loadShared();

        final boolean[] run = {true};
        MainWindow mainWindow = new MainWindow();
        ScreenCapture screenCapture = new ScreenCapture();
        mainWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                screenCapture.stop();
                run[0] = false;
                try {
                    Thread.sleep(100); //for stop all threads
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                super.windowClosing(e);
            }
        });

        screenCapture.setFps(30);
        Handler handler = new Handler();

        mainWindow.addCaptureSizeListener(screenCapture.getCaptureSizeListener());

        mainWindow.addCaptureScaleListener(screenCapture.getCaptureScaleListener());
        mainWindow.addCaptureScaleListener(handler::setScale);

        mainWindow.addxPositionListener(screenCapture.getxPositionListener());
        mainWindow.addyPositionListener(screenCapture.getyPositionListener());

        mainWindow.addThresholdListener(handler::setThreshold);

        screenCapture.start();

        BlockingQueue<BufferedImage> images = screenCapture.getImages();
        handler.processImage(images);

        mainWindow.processImage(handler.getResultQueue());
    }
}
