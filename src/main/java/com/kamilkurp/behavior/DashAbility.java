package com.kamilkurp.behavior;

import com.kamilkurp.creatures.Creature;
import com.kamilkurp.terrain.TerrainTile;
import com.kamilkurp.utils.Camera;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.List;

public class DashAbility extends Ability {

    private Creature abilityCreature;
    protected float dashSpeed = 0.0f;

    protected Timer dashTimer;
    protected Vector2f dashVector;

    public DashAbility(Creature abilityCreature) {
        super();

        this.abilityCreature = abilityCreature;

        dashTimer = new Timer();
        dashVector = new Vector2f(0f, 0f);

        cooldown = 1000;
    }

    public void update(int i) {
        if (active) {
            //end dash
            if (dashTimer.getTime() > abilityTime) {
                abilityCreature.setImmobilized(false);
                active = false;
            }
        }
    }

    @Override
    public void performMovement() {
         if (active) {
             Rectangle rect = abilityCreature.getRect();
             List<TerrainTile> tiles = abilityCreature.getArea().getTiles();

             float newPosX = rect.getX() + dashSpeed * dashVector.getX();
             float newPosY = rect.getY() + dashSpeed * dashVector.getY();

             if (!abilityCreature.isCollidingX(tiles, newPosX, newPosY) && newPosX >= 0 && newPosX < tiles.get(tiles.size() - 1).getRect().getX()) {
                 abilityCreature.move(dashSpeed * dashVector.getX(), 0);
             }

            if (!abilityCreature.isCollidingY(tiles, newPosX, newPosY) && newPosY >= 0 && newPosY < tiles.get(tiles.size() - 1).getRect().getY()) {
                abilityCreature.move(0, dashSpeed * dashVector.getY());

            }

        }
    }

    @Override
     protected void perform() {
        active = true;

        abilityCreature.setImmobilized(true);

        cooldownTimer.reset();
        dashTimer.reset();

        abilityCreature.takeStaminaDamage(20f);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void performOnUpdateStart(int i) {
        dashSpeed = 0.7f * i;
    }

    public Timer getCooldownTimer() {
        return cooldownTimer;
    }

    public void setDashVector(Vector2f dashVector) {
        this.dashVector = dashVector;
    }

    @Override
    public void render(Graphics g, Camera camera) {

    }
}
