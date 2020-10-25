package com.kamilkurp.behavior;

import com.kamilkurp.utils.Action;
import com.kamilkurp.utils.Timer;

public abstract class Ability {
    protected Action onPerformAction;

    protected Timer cooldownTimer;

    protected float cooldown;

    public Ability() {
        cooldownTimer = new Timer();
        cooldown = 3000f;

        onPerformAction = () -> {};
    }

    public abstract void update();
    public abstract void performMovement();
    public abstract boolean isActive();
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
}

