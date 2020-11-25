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
    protected float dashSpeed;

    protected Timer dashTimer;
    protected Vector2f dashVector;

    public DashAbility(Creature abilityCreature) {
        super(abilityCreature);

        this.abilityCreature = abilityCreature;

        dashTimer = new Timer();
        dashVector = new Vector2f(0f, 0f);

        cooldownTime = 1000;
        dashSpeed = 0.0f;
    }


    @Override
    public void performMovement() {
         if (state == AbilityState.ABILITY_ACTIVE) {
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
     protected void onAbilityStart() {
        abilityCreature.setImmobilized(true);

        activeTimer.reset();
        dashTimer.reset();

        abilityCreature.takeStaminaDamage(35f);

        channelTime = 150;
    }

    @Override
    protected void onAbilityStop() {
        abilityCreature.setImmobilized(false);
    }

    @Override
    public void performOnUpdateStart(int i) {
        dashSpeed = 0.7f * i;
    }

    public void setDashVector(Vector2f dashVector) {
        this.dashVector = dashVector;
    }

    @Override
    public void render(Graphics g, Camera camera) {

    }
}
