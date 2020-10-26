package com.kamilkurp.behavior;

import com.kamilkurp.creatures.Creature;
import com.kamilkurp.utils.Timer;

public class SwordAttackAbility extends Ability {
    Creature creature;

    public SwordAttackAbility(Creature creature) {
        super();

        this.creature = creature;
        cooldown = 800;
        abilityTime = 300;
    }

    @Override
    public void update() {
        if (cooldownTimer.getTime() > abilityTime) {
            isActive = false;
        }
    }

    @Override
    public void performMovement() {

    }

    @Override
    public void performOnUpdateStart(int i) {

    }

    @Override
    protected void perform() {
        isActive = true;
        abilityTimer.reset();
        cooldownTimer.reset();
    }
}
