package com.kamilkurp;

import com.kamilkurp.utils.Timer;
import org.newdawn.slick.geom.Rectangle;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Globals {
    public static float SCREEN_PROPORTION = 3 / 4f;
    public static int SCREEN_WIDTH = 800;
    public static int SCREEN_HEIGHT = 600;

    public static Random random = new Random();
    private static final String assetsLocation = "assets/";

    public static float distance(Rectangle rect1, Rectangle rect2) {
        float x1 = rect1.getCenterX();
        float y1 = rect1.getCenterY();
        float x2 = rect2.getCenterX();
        float y2 = rect2.getCenterY();

        return (float)Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
    }

    public static float distanceX(float x1, float x2) {

        return Math.abs(x2-x1);
    }

    public static float distanceY(float y1, float y2) {

        return Math.abs(y2-y1);
    }

    private static final List<Timer> timerList = new LinkedList<>();

    public static void addTimer(Timer timer) {
        timerList.add(timer);
    }

    public static void updateTimers(int delta) {
        for (Timer timer : timerList) {
            timer.update(delta);
        }
    }

    public static int randInt(int min, int max) {
        int randomNum = random.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public static float randFloat() {
        float randomNum = random.nextFloat();

        return randomNum;
    }

    public static String getAssetsLocation() {
        return assetsLocation;
    }
}
