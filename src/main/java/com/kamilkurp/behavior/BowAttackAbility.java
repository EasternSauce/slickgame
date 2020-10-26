package com.kamilkurp.behavior;

import com.kamilkurp.creatures.Creature;

public class BowAttackAbility extends Ability {
    Creature creature;

    public BowAttackAbility(Creature creature) {
        super();

        this.creature = creature;
    }

    @Override
    public void update() {

    }

    @Override
    public void performMovement() {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void performOnUpdateStart(int i) {

    }

    @Override
    protected void perform() {

    }
}
