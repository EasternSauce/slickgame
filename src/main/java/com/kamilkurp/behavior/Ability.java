package com.kamilkurp.behavior;

public interface Ability {
    void update();
    void performMovement();
    boolean isActive();
    void performOnUpdateStart(int i);
}
