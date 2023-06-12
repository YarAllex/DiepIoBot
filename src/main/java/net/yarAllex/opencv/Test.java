package net.yarAllex.opencv;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Test {

    static{
        nu.pattern.OpenCV.loadShared();
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    static class DetectFaceDemo {
        public void run() {
            System.out.println("\nRunning DetectFaceDemo");

            // Create a face detector from the cascade file in the resources
            // directory.
            CascadeClassifier faceDetector = new CascadeClassifier(getClass().getResource("/lbpcascade_frontalface.xml").getPath());
            Mat image = Imgcodecs.imread(getClass().getResource("/lena2.png").getPath());
            System.out.println("OpenCV Mat: " + Arrays.toString(image.get(1, 1)));

            // Detect faces in the image.
            // MatOfRect is a special container class for Rect.
            MatOfRect faceDetections = new MatOfRect();
            faceDetector.detectMultiScale(image, faceDetections);

            System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

            // Draw a bounding box around each face.
            for (Rect rect : faceDetections.toArray()) {
                Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
            }

            // Save the visualized detection.
            String filename = "faceDetection.png";
            System.out.println(String.format("Writing %s", filename));
            Imgcodecs.imwrite(filename, image);
        }
    }

    public static void main(String[] args) throws IOException {
        String inFile = "/home/yar/Projects/DiepIoBot/src/main/resources/test.png";
        String templateFile = "/home/yar/Projects/DiepIoBot/src/main/resources/main_tank.png";
        String outFile = "result.png";

        Mat img = Imgcodecs.imread(inFile);
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

        Imgproc.putText(img, "Found", new Point(matchLoc.x - 60,matchLoc.y - 10),
                Imgproc.FONT_HERSHEY_COMPLEX, 1.0 ,new  Scalar(0,255,0));

        Imgcodecs.imwrite(outFile, img);

        //Encoding the image
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", img, matOfByte);

        //Storing the encoded Mat in a byte array
        byte[] byteArray = matOfByte.toArray();

        //Preparing the Buffered Image
        InputStream in = new ByteArrayInputStream(byteArray);
        BufferedImage bufImage = ImageIO.read(in);

        JFrame frame = new JFrame();
        frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
        frame.pack();
        frame.setVisible(true);

        frame.dispose();
    }

    @org.junit.Test
    public void TestScreenShot() throws AWTException, InterruptedException {
        JFrame frame = new JFrame();
        frame.setVisible(true);
        for (int i = 0; i < 1000; i++) {
            BufferedImage image = new Robot().createScreenCapture(new Rectangle(300, 300, 300, 300));
            frame.getContentPane().removeAll();
            frame.getContentPane().add(new JLabel(new ImageIcon(image)));
            frame.pack();
            Thread.sleep(33);
        }
        frame.dispose();
    }
}
