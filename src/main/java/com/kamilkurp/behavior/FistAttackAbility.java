package com.kamilkurp.behavior;

import com.kamilkurp.creatures.Creature;

public class FistAttackAbility extends Ability {
    Creature creature;

    public FistAttackAbility(Creature creature) {
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
