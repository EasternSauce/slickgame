package com.kamilkurp.abilities;

import com.kamilkurp.creatures.Creature;
import com.kamilkurp.utils.Action;
import com.kamilkurp.utils.Camera;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;

public abstract class Ability {
    protected Action onPerformAction;
    protected Action onChannelAction;

    protected Timer activeTimer;
    protected Timer channelTimer;

    protected int cooldownTime;
    protected int activeTime;
    protected int channelTime;

    protected AbilityState state;

    protected Rectangle meleeAttackRect;
    protected Polygon meleeAttackHitbox;

    Creature abilityCreature;

    protected boolean onCooldown;

    protected boolean isAttack;


    protected Ability(Creature abilityCreature) {
        this.abilityCreature = abilityCreature;

        activeTimer = new Timer();
        channelTimer = new Timer();

        state = AbilityState.ABILITY_INACTIVE;

        onPerformAction = () -> {};
        onChannelAction = () -> {};

        onCooldown = false;

        isAttack = false;
    }

    public abstract void init();

    public void update(int i) {
        if (state == AbilityState.ABILITY_CHANNELING && channelTimer.getElapsed() > channelTime) {
            state = AbilityState.ABILITY_ACTIVE;
            onActiveStart();
            onPerformAction.execute();
            activeTimer.reset();
            onCooldown = true;
        }
        if (state == AbilityState.ABILITY_ACTIVE && activeTimer.getElapsed() > activeTime) {
            state = AbilityState.ABILITY_INACTIVE;
            onStop();
        }

        if (state == AbilityState.ABILITY_CHANNELING) {
            onUpdateChanneling(i);
        }
        else if (state == AbilityState.ABILITY_ACTIVE) {
            onUpdateActive(i);
        }




        if (state == AbilityState.ABILITY_INACTIVE && onCooldown) {
            if (activeTimer.getElapsed() > cooldownTime) {
                onCooldown = false;
            }
        }
    }

    public void performMovement() {

    }

    public void performOnUpdateStart(int i) {

    }

    public void onStartActiveAction(Action action) {
        onPerformAction = action;
    }

    public void onStartChannelAction(Action action) {
        onChannelAction = action;
    }

    public void onChannellingStart() {

    }

    public boolean canPerform() {
        if (abilityCreature.getStaminaPoints() > 0 && state == AbilityState.ABILITY_INACTIVE && !onCooldown) {
            return true;
        }
        return false;
    }

    public void perform() {
        channelTimer.reset();
        state = AbilityState.ABILITY_CHANNELING;
        onChannellingStart();
        onChannelAction.execute();


        if (isAttack) {
            // + 10 to ensure regen doesnt start if we hold attack button
            abilityCreature.getEffect("staminaRegenStopped").applyEffect(channelTime + cooldownTime + 10);
        }
        else {
            abilityCreature.getEffect("staminaRegenStopped").applyEffect(1000);

        }
    }

    protected void onActiveStart() {

    }

    protected void onStop() {

    }

    public boolean isActive() {
        return state == AbilityState.ABILITY_ACTIVE;
    }


    protected void onUpdateActive(int i) {

    }

    protected void onUpdateChanneling(int i) {

    }

    public void render(Graphics g, Camera camera) {

    }

    public void stopAbility() {
        state = AbilityState.ABILITY_INACTIVE;
    }

    public AbilityState getState() {
        return state;
    }

    public boolean isOnCooldown() {
        return onCooldown;
    }
}

