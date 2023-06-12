package net.yarAllex;

import java.awt.*;
import java.util.LinkedList;

public class Item {
    private Color color;// = new Color(184, 184, 184);
    //height x width
    private LinkedList<Pixel> coordinates = new LinkedList<>();

    public Item(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public LinkedList<Pixel> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LinkedList<Pixel> coordinates) {
        this.coordinates = coordinates;
    }
}
