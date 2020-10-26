package com.kamilkurp.behavior;

import com.kamilkurp.Renderable;
import com.kamilkurp.utils.Action;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.geom.Rectangle;

public abstract class Ability implements Renderable {
    protected Action onPerformAction;

    protected Timer cooldownTimer;
    protected Timer abilityTimer;

    protected int cooldown;
    protected int abilityTime;
    protected boolean active;

    protected Rectangle swordAttackRect;

    public Ability() {
        cooldownTimer = new Timer();
        abilityTimer = new Timer();
        cooldown = 3000;
        abilityTime = 200;

        active = false;

        onPerformAction = () -> {};
        cooldownTimer.setTime(cooldown); // ability immediately available
        abilityTimer.setTime(abilityTime);
    }

    public abstract void update(int i);
    public abstract void performMovement();
    public abstract void performOnUpdateStart(int i);

    public void onPerform(Action action) {
        onPerformAction = action;
    }

    public void tryPerforming() {
        if (cooldownTimer.getTime() > cooldown) {
            perform();
            onPerformAction.execute();
        }
    }

    protected abstract void perform();

    public boolean isActive() {
        return active;
    }
}

