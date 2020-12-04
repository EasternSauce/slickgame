package com.kamilkurp.behavior;

import com.kamilkurp.creatures.Creature;
import com.kamilkurp.utils.Action;
import com.kamilkurp.utils.Camera;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;

public class Ability {
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

    public Ability(Creature abilityCreature) {
        this.abilityCreature = abilityCreature;

        activeTimer = new Timer();
        channelTimer = new Timer();
        cooldownTime = 3000;
        activeTime = 200;
        channelTime = 500;

        state = AbilityState.ABILITY_INACTIVE;

        onPerformAction = () -> {};
        activeTimer.setTime(cooldownTime);
        channelTimer.setTime(channelTime);
    }

    public void update(int i) {
        if (state == AbilityState.ABILITY_CHANNELING && channelTimer.getTime() > channelTime) {
            state = AbilityState.ABILITY_ACTIVE;
            onAbilityStart();
            onPerformAction.execute();
            abilityCreature.startStaminaRegen();

        }
        if (state == AbilityState.ABILITY_ACTIVE && activeTimer.getTime() > activeTime) {
            state = AbilityState.ABILITY_INACTIVE;
            onAbilityStop();
        }

        if (state == AbilityState.ABILITY_CHANNELING) {
            onUpdateChanneling(i);
        }
        else if (state == AbilityState.ABILITY_ACTIVE) {
            onUpdateActive(i);
        }
    }

    public void performMovement() {

    }

    public void performOnUpdateStart(int i) {

    }

    public void onStartActiveAction(Action action) {
        onPerformAction = action;
    }

    public void onChannel() {

    }

    public void tryPerforming() {
        if (abilityCreature.getStaminaPoints() != 0 && state == AbilityState.ABILITY_INACTIVE && activeTimer.getTime() > cooldownTime) {
            channelTimer.reset();
            state = AbilityState.ABILITY_CHANNELING;
            onChannel();
            abilityCreature.stopStaminaRegen();
        }
    }

    protected void onAbilityStart() {

    }

    protected void onAbilityStop() {

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
}

