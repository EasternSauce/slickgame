package com.kamilkurp.creatures;

public enum AttackType {
    UNARMED(100f, 300f, 175f, 130f),
    SWORD(100f, 300f, 175f, 130f),
    BOW(300f, 300f, 300f, 300f),
    TRIDENT(180f, 400f, 220f, 200f);

    public float minimumDistance;
    public float walkUpDistance;
    public float holdDistance;
    public float attackDistance;

    AttackType(float minimumDistance, float walkUpDistance, float holdDistance, float attackDistance) {
        this.minimumDistance = minimumDistance;
        this.walkUpDistance = walkUpDistance;
        this.holdDistance = holdDistance;
        this.attackDistance = attackDistance;
    }
}
