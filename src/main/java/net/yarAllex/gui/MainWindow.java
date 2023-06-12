package net.yarAllex.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

public class MainWindow {
    private JFrame frame = new JFrame();
    private JPanel panel = new JPanel();
    private Box boxRoot = Box.createVerticalBox();
    private Box boxButton = Box.createHorizontalBox();

    private JButton startButton = new JButton("start");
    private JButton stopButton = new JButton("stop");

    private JLabel captureSizeLabel = new JLabel("Capture size");
    private JSlider captureSizeSlider = new JSlider(1, 100, 20);
    private JLabel captureScaleLabel = new JLabel("Capture scale");
    private JSlider captureScaleSlider = new JSlider(1, 100, 70);
    private JLabel xLabel = new JLabel("X");
    private JSlider xPositionSlider = new JSlider();
    private JLabel yLabel = new JLabel("Y");
    private JSlider yPositionSlider = new JSlider();
    private JLabel thresholdLabel = new JLabel("Threshold");
    private JSlider thresholdSlider = new JSlider(20, 80, 35);

    private JLabel processTime = new JLabel();

    private JLabel imageLabel = new JLabel();
    private int imgWidth = 600;

    private ArrayList<Consumer<Float>> captureSizeListeners = new ArrayList<>();
    private ArrayList<Consumer<Float>> captureScaleListeners = new ArrayList<>();
    private Consumer<Integer> xPositionListener;
    private Consumer<Integer> yPositionListener;
    private ArrayList<Consumer<Float>> thresholdListeners = new ArrayList<>();

    private final Logger log = LoggerFactory.getLogger("MainWindow");

    public MainWindow() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        panel.add(imageLabel);
        panel.add(boxRoot);
        boxRoot.add(boxButton);
        boxRoot.add(captureSizeLabel);
        boxRoot.add(captureSizeSlider);
        boxRoot.add(captureScaleLabel);
        boxRoot.add(captureScaleSlider);
        boxRoot.add(xLabel);
        boxRoot.add(xPositionSlider);
        boxRoot.add(yLabel);
        boxRoot.add(yPositionSlider);
        boxRoot.add(processTime);
        boxRoot.add(thresholdLabel);
        boxRoot.add(thresholdSlider);

        captureSizeSlider.addChangeListener(e -> {
            float value = (float)captureSizeSlider.getValue() / 100;
            captureSizeListeners.forEach(c -> c.accept(value));
        });

        captureScaleSlider.addChangeListener(e -> {
            float value = (float)captureScaleSlider.getValue() / 100;
            captureScaleListeners.forEach(c -> {
                c.accept(value);
            });
        });

        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        xPositionSlider.setMinimum(1);
        xPositionSlider.setMaximum(size.width);
        xPositionSlider.setValue(1);
        xPositionSlider.addChangeListener(e -> {
            if (xPositionListener == null) {
                return;
            }
            int value = xPositionSlider.getValue();
            xPositionListener.accept(value);
        });

        yPositionSlider.setMinimum(1);
        yPositionSlider.setMaximum(size.height);
        yPositionSlider.setValue(1);
        yPositionSlider.addChangeListener(e -> {
            if (yPositionListener == null) {
                return;
            }
            int value = yPositionSlider.getValue();
            yPositionListener.accept(value);
        });

        thresholdSlider.addChangeListener(e -> {
            float value = (float) thresholdSlider.getValue() / 100;
            thresholdListeners.forEach(l -> {
                l.accept(value);
            });
        });

        createStartImg();

        boxButton.add(startButton);
        boxButton.add(stopButton);

        frame.setVisible(true);
        frame.pack();
    }

    public void addWindowListener(WindowListener listener) {
        if (listener == null) {
            return;
        }
        frame.addWindowListener(listener);
    }

    public void loadImg(BufferedImage bufferedImage) {
        log.trace("Load image");
        ImageIcon icon = new ImageIcon(bufferedImage);
        Image scaledImage = scaleImage(icon);
        this.imageLabel.removeAll();
        this.imageLabel.setIcon(new ImageIcon(scaledImage));

        frame.pack();
    }

    public void processImage(BlockingQueue<BufferedImage> images) {
        new Thread(() -> {
            while (true) {
                try {
                    BufferedImage image = images.take();
                    loadImg(image);
                } catch (InterruptedException e) {
                    log.error("Error while draw image: ", e);
                }
            }
        }).start();
    }

    public int getCaptureSize() {
        return captureSizeSlider.getValue();
    }

    public void addCaptureSizeListener(Consumer<Float> captureSizeListener) {
        this.captureSizeListeners.add(captureSizeListener);
    }

    public void addxPositionListener(Consumer<Integer> xPositionListener) {
        this.xPositionListener = xPositionListener;
    }

    public void addyPositionListener(Consumer<Integer> yPositionListener) {
        this.yPositionListener = yPositionListener;
    }

    public void addCaptureScaleListener(Consumer<Float> captureScaleListener) {
        this.captureScaleListeners.add(captureScaleListener);
    }

    public void addThresholdListener(Consumer<Float> thresholdListener) {
        this.thresholdListeners.add(thresholdListener);
    }

    public void updateProcessTime(long time) {
        processTime.setText(/*"Process time: " + time*/String.valueOf(time));
    }

    private void updateImg(Image scaledImage) {
        this.imageLabel.setIcon(new ImageIcon(scaledImage));

        frame.pack();
    }

    private Image scaleImage(ImageIcon icon) {
        Image image = icon.getImage();
        float ratio = (float) icon.getIconHeight() / icon.getIconWidth();
        int imgHeight = (int) (imgWidth * ratio);
        return image.getScaledInstance(imgWidth, imgHeight, Image.SCALE_FAST);
    }

    private BufferedImage scaleImage(BufferedImage image) {
        float ratio = (float) image.getHeight() / image.getWidth();
        int w = imgWidth;
        int h = (int) (w * ratio);
        BufferedImage after = new BufferedImage(w, h, image.getType());
        AffineTransform at = new AffineTransform();
        at.scale(w, h);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        after = scaleOp.filter(image, after);

        return after;
    }

    private void createStartImg() {
        ImageIcon icon = new ImageIcon("test.png");
        Image scaledImage = scaleImage(icon);
        updateImg(scaledImage);
    }
}
