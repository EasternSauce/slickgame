package com.kamilkurp.utils;

import com.kamilkurp.Globals;

public class Timer {
    private int elapsed;
    private boolean isStarted;

    public Timer() {
        this.elapsed = 0;
        isStarted = false;
        Globals.addTimer(this);
    }

    public Timer(boolean isStarted) {
        this.elapsed = 0;
        this.isStarted = isStarted;
        Globals.addTimer(this);
    }


    public void update(int delta) {
        if (isStarted) {
            elapsed += delta;
        }
    }

    public int getElapsed() {
        return elapsed;
    }

    public void setElapsed(int elapsed) {
        this.elapsed = elapsed;
    }

    public void reset() {
        if (!isStarted) isStarted = true;
        elapsed = 0;
    }

    public void start() {
        isStarted = true;
    }

    public void stop() {
        isStarted = false;
    }
}
