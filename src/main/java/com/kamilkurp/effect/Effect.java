package com.kamilkurp.effect;

import com.kamilkurp.creatures.Creature;
import com.kamilkurp.utils.Timer;

public class Effect {
    protected Creature effectCreature;

    protected boolean effectActive;

    protected Timer effectTimer;

    protected int effectEndTime = 0;

    public Effect(Creature creature) {
        this.effectCreature = creature;

        effectActive = false;

        effectTimer = new Timer();
    }

    public void applyEffect(int effectTime) {
        if (effectActive) {
            effectTimer.reset();
            int remainingTime = effectEndTime - effectTimer.getElapsed();
            effectEndTime = Math.max(remainingTime, effectTime);
        }
        else {
            effectActive = true;
            effectTimer.reset();
            effectEndTime = effectTime;

        }
    }

    public boolean isActive() {
        return effectActive;
    }

    public void update() {
        if (effectActive && effectTimer.getElapsed() > effectEndTime) {
            effectActive = false;
        }
    }

    public int getRemainingTime() {
        return effectEndTime - effectTimer.getElapsed();
    }

    public void stop() {
        effectActive = false;
        effectEndTime = 0;
        effectTimer.stop();
        effectTimer.reset();
    }
}
