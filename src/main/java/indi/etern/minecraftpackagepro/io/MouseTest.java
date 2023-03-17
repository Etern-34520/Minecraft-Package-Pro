package indi.etern.minecraftpackagepro.io;

import java.awt.*;
import java.awt.event.InputEvent;

public class MouseTest {
    public static void main(String[] args){
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        long startTime =  System.currentTimeMillis();
        do {
            assert robot != null;
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
        } while (System.currentTimeMillis() != startTime + 15000);
    }
}
