package net.yarAllex;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;

public class ImageData {
    private int[] data;
    private String colorSpace;
    private int height;
    private int width;
    @JsonIgnore
    private LinkedList<Pixel> pixels;
    @JsonIgnore
    private Mat mat;

    public int[] getData() {
        return data;
    }

    public void setData(int[] data) {
        this.data = data;
    }

    public String getColorSpace() {
        return colorSpace;
    }

    public void setColorSpace(String colorSpace) {
        this.colorSpace = colorSpace;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public LinkedList<Pixel> getPixels() {
        if (pixels !=null) {
            return pixels;
        }

        pixels = new LinkedList<>();
        int c = 1;
        for (int i = 0 ; i < data.length; i += 3) {
            Color color = new Color(data[i], data[i+1], data[i+2]);
            int y = (int) Math.ceil(c / (double) width);
            int x = c - (y - 1) * width;
            Pixel pixel = new Pixel(x, y, color);
            pixels.add(pixel);
            c++;
        }

        return pixels;
    }

    public Mat getMat() {
        if (mat !=null) {
            return mat;
        }

        mat = new Mat(height, width, CvType.CV_32SC3);
        int c = 1;
        for (int i = 0 ; i < data.length; i += 4) {
            int[] pixel = {data[i+2], data[i+1], data[i]};

            int y = (int) Math.ceil(c / (double) width);
            int x = c - (y - 1) * width;

            mat.put(y-1, x-1, pixel);

            c++;
        }

        mat.convertTo(mat, CvType.CV_8UC4);

        return mat;
    }

    @Override
    public String toString() {
        return "net.yarAllex.ImageData{" +
                "data=" + Arrays.toString(data) +
                ", colorSpace='" + colorSpace + '\'' +
                ", height=" + height +
                ", width=" + width +
                '}';
    }
}
