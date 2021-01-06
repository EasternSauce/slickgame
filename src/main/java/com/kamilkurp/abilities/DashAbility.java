package com.kamilkurp.abilities;

import com.kamilkurp.creatures.Creature;
import com.kamilkurp.spawn.Blockade;
import com.kamilkurp.terrain.TerrainTile;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.List;

public class DashAbility extends Ability {
    protected float dashFrameSpeed;

    protected float dashDistance;

    protected Vector2f dashVector;

    protected float dashSpeed;



    private DashAbility(Creature abilityCreature) {
        super(abilityCreature);
    }

    @Override
    public void init() {
        dashVector = new Vector2f(0f, 0f);

        cooldownTime = 2000;
        channelTime = 0;

        dashFrameSpeed = 0.0f;

        dashSpeed = 1.0f;

        dashDistance = 200;

        activeTime = (int)(dashDistance / dashSpeed);



    }


    @Override
    public void performMovement() {
         if (state == AbilityState.ABILITY_ACTIVE) {
             Rectangle rect = abilityCreature.getRect();
             List<TerrainTile> tiles = abilityCreature.getArea().getTiles();

             float newPosX = rect.getX() + dashFrameSpeed * dashVector.getX();
             float newPosY = rect.getY() + dashFrameSpeed * dashVector.getY();

             List<Blockade> blockadeList = abilityCreature.getArea().getBlockadeList();

             if (!abilityCreature.isCollidingX(tiles, blockadeList, newPosX, newPosY) && newPosX >= 0 && newPosX < tiles.get(tiles.size() - 1).getRect().getX()) {
                 abilityCreature.move(dashFrameSpeed * dashVector.getX(), 0);
             }

            if (!abilityCreature.isCollidingY(tiles, blockadeList, newPosX, newPosY) && newPosY >= 0 && newPosY < tiles.get(tiles.size() - 1).getRect().getY()) {
                abilityCreature.move(0, dashFrameSpeed * dashVector.getY());

            }

        }
    }

    @Override
     protected void onActiveStart() {
        abilityCreature.setImmobilized(true);

        abilityCreature.takeStaminaDamage(15f);
    }

    @Override
    protected void onStop() {
        abilityCreature.setImmobilized(false);
    }

    @Override
    public void performOnUpdateStart(int i) {
        dashFrameSpeed = dashSpeed * i;
    }

    public void setDashVector(Vector2f dashVector) {
        this.dashVector = dashVector;
    }

    public void render(Graphics g, Camera camera) {

    }

    public static DashAbility newInstance(Creature abilityCreature) {
        DashAbility ability = new DashAbility(abilityCreature);
        ability.init();
        return ability;
    }
}
