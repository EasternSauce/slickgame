package com.kamilkurp.abilities;

import com.kamilkurp.animations.AbilityAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.geom.Rectangle;

public class Fist {
    private Rectangle pos;

    protected Timer activeTimer;
    protected Timer channelTimer;

    protected int activeTime;
    protected int channelTime;

    protected AbilityState state;

    protected int startTime;

    protected boolean started;

    protected AbilityAnimation abilityAnimation;
    protected AbilityAnimation windupAnimation;

    protected float scale;

    protected Rectangle hitbox;

    public Fist(int startTime, Rectangle rect) {
        this.startTime = startTime;
        state = AbilityState.ABILITY_INACTIVE;

        activeTimer = new Timer();
        channelTimer = new Timer();

        activeTime = 300;
        channelTime = 400;

        abilityAnimation = new AbilityAnimation(Assets.fistSlamSpriteSheet, 5, 60);
        windupAnimation = new AbilityAnimation(Assets.fistSlamWindupSpriteSheet, 5, 80);

        scale = 2.0f;

        pos = rect;

        hitbox = new Rectangle(pos.getX(), pos.getY() + 40 * scale, 40 * scale, 40 * scale);

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
        windupAnimation.restart();
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

    public AbilityAnimation getAbilityAnimation() {
        return abilityAnimation;
    }

    public AbilityAnimation getWindupAnimation() {
        return windupAnimation;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public float getScale() {
        return scale;
    }
}
