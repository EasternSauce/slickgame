package com.kamilkurp.behavior;

import com.kamilkurp.Renderable;
import com.kamilkurp.utils.Action;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.geom.Rectangle;

public abstract class Ability implements Renderable {
    protected Action onPerformAction;
    protected Action onWindupAction;

    protected Timer cooldownTimer;
    protected Timer windupTimer;

    protected int cooldown;
    protected int abilityTime;
    protected int windupTime;

    protected boolean windup;

    protected boolean active;

    protected Rectangle swordAttackRect;

    public Ability() {
        cooldownTimer = new Timer();
        windupTimer = new Timer();
        cooldown = 3000;
        abilityTime = 200;
        windupTime = 500;

        active = false;
        windup = false;

        onPerformAction = () -> {};
        cooldownTimer.setTime(cooldown); // ability immediately available
        windupTimer.setTime(windupTime);
    }

    public abstract void update(int i);

    public void performMovement() {

    }

    public void performOnUpdateStart(int i) {

    }

    public void onPerform(Action action) {
        onPerformAction = action;
    }

    public void onWindup() {

    }

    public void tryPerforming() {
        if (!windup && cooldownTimer.getTime() > cooldown) {
            windupTimer.reset();
            windup = true;
            onWindup();
        }
    }

    protected abstract void perform();

    public boolean isActive() {
        return active;
    }
}

