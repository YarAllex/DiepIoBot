package net.yarAllex.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindowTest {

    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
        mainWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Close window");
                super.windowClosing(e);
            }
        });
    }
}
