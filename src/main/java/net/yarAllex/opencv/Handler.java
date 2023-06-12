package net.yarAllex.opencv;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Handler {

    private final Logger log = LoggerFactory.getLogger("net.yarAllex.opencv.Handler");
    private long time;
    private float scale = 0.7f;
    double threshold = 0.5;
    private BlockingQueue<BufferedImage> resultQueue = new LinkedBlockingQueue<>();

    public void processImage(BlockingQueue<BufferedImage> images) {
        new Thread(() -> {
            while (true) {
                long startTime = System.currentTimeMillis();

                BufferedImage image = null;
                try {
                    image = images.take();
                } catch (InterruptedException e) {
                    log.error("Process error: ", e);
                }
                Mat matResult = findFoodOne(imgToMat(image));
                matResult = findFoodTwo(matResult);
                matResult = findFoodThree(matResult);
                time = System.currentTimeMillis() - startTime;
                resultQueue.add(matToImg(matResult));
            }
        }).start();
    }

    public BlockingQueue<BufferedImage> getResultQueue() {
        return resultQueue;
    }

    public Mat imgToMat(BufferedImage image) {
        Mat result = new Mat();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", byteArrayOutputStream);
            byteArrayOutputStream.flush();
            result = Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public BufferedImage matToImg(Mat mat) {
        //Encoding the image
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", mat, matOfByte);
        //Storing the encoded Mat in a byte array
        byte[] byteArray = matOfByte.toArray();
        //Preparing the Buffered Image
        InputStream in = new ByteArrayInputStream(byteArray);
        BufferedImage bufImage = null;
        try {
            bufImage = ImageIO.read(in);
        } catch (IOException e) {
            log.error("Error while converting Mat to BufferedImage: ", e);
        }
        return bufImage;
    }

    private Mat findMainTank(Mat img) {
        String templateFile = "/home/yar/Projects/DiepIoBot/src/main/resources/main_tank.png";

        Mat templ = Imgcodecs.imread(templateFile);

        // / Create the result matrix
        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

        // / Do the Matching and Normalize
        Imgproc.matchTemplate(img, templ, result, Imgproc.TM_CCOEFF);
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());

        // / Localizing the best match with minMaxLoc
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        Point matchLoc;
        matchLoc = mmr.maxLoc;
        Imgproc.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),
                matchLoc.y + templ.rows()), new Scalar(0, 255, 0), 4);

        Imgproc.putText(img, "Main tank", new Point(matchLoc.x - 60,matchLoc.y - 10),
                Imgproc.FONT_HERSHEY_COMPLEX, 1.0 ,new  Scalar(0,255,0));

        return img;
    }

    private Mat findFoodOne(Mat img) {
        String templateFile = "/home/yar/Projects/DiepIoBot/src/main/resources/food_1.png";

        return findObjects(img, templateFile);
    }

    private Mat findFoodTwo(Mat img) {
        String templateFile = "/home/yar/Projects/DiepIoBot/src/main/resources/food_2.png";

        return findObjects(img, templateFile);
    }

    private Mat findFoodThree(Mat img) {
        String templateFile = "/home/yar/Projects/DiepIoBot/src/main/resources/food_3.png";

        return findObjects(img, templateFile);
    }

    private Mat findObjects(Mat img, String templateFile) {
        Mat templ = Imgcodecs.imread(templateFile);
        templ = scale(templ);

        // / Create the result matrix
        int result_cols = img.cols() - templ.cols() + 1;
        int result_rows = img.rows() - templ.rows() + 1;
        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

        // / Do the Matching and Normalize
        Imgproc.matchTemplate(img, templ, result, Imgproc.TM_CCOEFF_NORMED);
        Imgproc.threshold(result, result, threshold, 1, Imgproc.THRESH_TOZERO);
        double resultThresh = 1;
        while (resultThresh > threshold) {
            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
            resultThresh = mmr.maxVal;
            if (resultThresh >= threshold) {
                Point matchLoc = mmr.maxLoc;
                Imgproc.rectangle(img, matchLoc, new Point(matchLoc.x + templ.cols(),matchLoc.y + templ.rows()), new Scalar(255, 0, 0), 1);
                //clear result
                Imgproc.rectangle(result, matchLoc, new Point(matchLoc.x + templ.cols(), matchLoc.y + templ.rows()), new Scalar(0, 255, 0),-1);
                log.trace("Max value: " + mmr.maxVal);
            }
        }

        return img;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        log.trace("Set threshold: {}", threshold);
        this.threshold = threshold;
    }

    private Mat scale(Mat templ) {
        int width = (int) (templ.width() * scale);
        int height= (int) (templ.height() * scale);
        Mat scaled = new Mat();
        log.trace("SCale: scale:{}, width:{}, height:{}", scale, width, height);
        Size size = new Size(width, height);
        Imgproc.resize(templ, scaled, size);

        return scaled;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public long getTime() {
        return time;
    }
}
