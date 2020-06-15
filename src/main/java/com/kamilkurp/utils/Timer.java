package com.kamilkurp.utils;

import com.kamilkurp.Globals;

public class Timer {
    private int time;

    public Timer() {
        this.time = 0;
        Globals.addTimer(this);
    }

    public void update(int delta) {
        time += delta;
    }

    public int getTime() {
        return time;
    }

    public void reset() {
        time = 0;
    }
}
