package com.kamilkurp.abilities;

import com.kamilkurp.creatures.AttackType;
import com.kamilkurp.creatures.Creature;

public abstract class Attack extends Ability {
    protected AttackType attackType;

    protected Attack(Creature abilityCreature) {
        super(abilityCreature);

        isAttack = true;
    }

    public AttackType getAttackType() {
        return attackType;
    }
}
