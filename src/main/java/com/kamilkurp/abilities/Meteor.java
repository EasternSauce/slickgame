package com.kamilkurp.abilities;

import com.kamilkurp.animations.AttackAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.geom.Rectangle;

public class Meteor {
    private Rectangle pos;

    protected Timer activeTimer;
    protected Timer channelTimer;

    protected int activeTime;
    protected int channelTime;

    protected AbilityState state;

    protected int startTime;

    protected boolean started;

    protected AttackAnimation explosionAnimation;


    public Meteor(int startTime, Rectangle rect) {
        this.startTime = startTime;
        state = AbilityState.ABILITY_INACTIVE;

        activeTimer = new Timer();
        channelTimer = new Timer();

        activeTime = 1800;
        channelTime = 1300;

        explosionAnimation = new AttackAnimation(Assets.explosionSpriteSheet, 20, 88);

        pos = rect;

    }

    public boolean isStarted() {
        return started;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public void start() {
        started = true;
        state = AbilityState.ABILITY_CHANNELING;
        channelTimer.reset();
    }

    public AbilityState getState() {
        return state;
    }

    public Timer getActiveTimer() {
        return activeTimer;
    }

    public Timer getChannelTimer() {
        return channelTimer;
    }

    public int getActiveTime() {
        return activeTime;
    }

    public int getChannelTime() {
        return channelTime;
    }

    public void setState(AbilityState state) {
        this.state = state;
    }

    public Rectangle getPos() {
        return pos;
    }

    public AttackAnimation getExplosionAnimation() {
        return explosionAnimation;
    }
}
