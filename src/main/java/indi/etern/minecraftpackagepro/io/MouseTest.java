package indi.etern.minecraftpackagepro.io;

import java.awt.*;
import java.awt.event.InputEvent;

public class MouseTest {
    volatile static boolean stop = false;
    public static void main(String[] args){
        Robot robot = null;
        
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    stop = true;
                }
            }
        }.start();
        do {
            assert robot != null;
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
        } while (!stop);
    }
}
