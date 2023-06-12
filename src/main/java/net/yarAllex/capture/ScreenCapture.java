package net.yarAllex.capture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class ScreenCapture implements Runnable{
    private boolean run = false;
    private int fps = 30;
    private int startX = 0;
    private int startY = 0;
    private int width;
    private int height;
    private float defCaptureSize = 0.2f;
    private float scale = 0.7f;
    private BlockingQueue<BufferedImage> images = new LinkedBlockingQueue<>();
    private Consumer<Float> captureSizeListener;
    private Consumer<Float> captureScaleListener;
    private Consumer<Integer> xPositionListener;
    private Consumer<Integer> yPositionListener;

    private final Logger log = LoggerFactory.getLogger("ScreenCapture");

    public ScreenCapture() {
        setCaptureSize(defCaptureSize);
        this.captureSizeListener = this::setCaptureSize;
        this.captureScaleListener = this::setScale;
        this.xPositionListener = this::setStartX;
        this.yPositionListener = this::setStartY;
    }

    @Override
    public void run() {
        log.info("Start net.yarAllex.capture");
        while (run) {
            BufferedImage image = null;
            try {
                image = new Robot().createScreenCapture(new Rectangle(startX, startY, width, height));
                images.put(scaleImage(image, scale));
//                images.put(image);
                log.trace("Take screen net.yarAllex.capture");
                Thread.sleep(1000/fps);
            } catch (AWTException | InterruptedException e) {
                log.error("Failed when make screen net.yarAllex.capture: ", e);
            }
        }
        log.info("End net.yarAllex.capture");
    }

    private BufferedImage scaleImage(BufferedImage image, float proportion) {
        int w = (int) (image.getWidth() * proportion);
        int h = (int) (image.getHeight() * proportion);
        BufferedImage after = new BufferedImage(w, h, image.getType());
        AffineTransform at = new AffineTransform();
        at.scale(proportion, proportion);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        after = scaleOp.filter(image, after);

        return after;
    }

    public void start() {
        if (run) {
            log.warn("Screen capturing is already running!");
            return;
        }

        run = true;
        new Thread(this).start();
    }

    public void stop() {
        log.info("Stop net.yarAllex.capture");
        run = false;
    }

    public Consumer<Float> getCaptureSizeListener() {
        return captureSizeListener;
    }

    public Consumer<Float> getCaptureScaleListener() {
        return captureScaleListener;
    }

    public Consumer<Integer> getxPositionListener() {
        return xPositionListener;
    }

    public Consumer<Integer> getyPositionListener() {
        return yPositionListener;
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public BlockingQueue<BufferedImage> getImages() {
        return images;
    }

    public void setImages(BlockingQueue<BufferedImage> images) {
        this.images = images;
    }

    public void setScale(float scale) {
        log.trace("Set scale : {}", scale);
        this.scale = scale;
    }

    private void setCaptureSize(Float scale) {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        width = (int) (size.getWidth() * scale);
        height = (int) (size.getHeight() * scale);
        log.debug("Set scale: {}, new w: {}, new h: {}", scale, width, height);
    }
}
