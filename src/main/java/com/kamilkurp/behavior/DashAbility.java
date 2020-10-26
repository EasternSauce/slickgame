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

    private Creature creature;
    protected float dashSpeed = 0.0f;

    protected Timer dashTimer;
    protected Vector2f dashVector;

    public DashAbility(Creature creature) {
        super();

        this.creature = creature;

        dashTimer = new Timer();
        dashVector = new Vector2f(0f, 0f);


    }

    public void update(int i) {
        if (active) {
            //end dash
            if (dashTimer.getTime() > abilityTime) {
                creature.setImmobilized(false);
                active = false;
            }
        }
    }

    @Override
    public void performMovement() {
         if (active) {
             Rectangle rect = creature.getRect();
             List<TerrainTile> tiles = creature.getArea().getTiles();

             float newPosX = rect.getX() + dashSpeed * dashVector.getX();
             float newPosY = rect.getY() + dashSpeed * dashVector.getY();

             if (!creature.isCollidingX(tiles, newPosX, newPosY) && newPosX >= 0 && newPosX < tiles.get(tiles.size() - 1).getRect().getX()) {
                 creature.move(dashSpeed * dashVector.getX(), 0);
             }

            if (!creature.isCollidingY(tiles, newPosX, newPosY) && newPosY >= 0 && newPosY < tiles.get(tiles.size() - 1).getRect().getY()) {
                creature.move(0, dashSpeed * dashVector.getY());

            }

        }
    }

    @Override
     protected void perform() {
        active = true;

        creature.setImmobilized(true);

        cooldownTimer.reset();
        dashTimer.reset();
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
