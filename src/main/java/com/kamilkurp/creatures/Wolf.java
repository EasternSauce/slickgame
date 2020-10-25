package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.animations.WalkAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.behavior.DashAbility;
import com.kamilkurp.items.Item;
import com.kamilkurp.items.ItemType;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.HashMap;

public class Wolf extends Mob {


    private final Sound dogBarkSound = Assets.dogBarkSound;
    private final Sound dogWhimperSound = Assets.dogWhimperSound;

    private DashAbility dashAbility;

    public Wolf(GameSystem gameSystem, String id) throws SlickException {
        super(gameSystem, id);

        actionTimer = new Timer();

        dropTable = new HashMap<>();
        dropTable.put("ringmailGreaves", 0.9f);
        dropTable.put("leatherArmor", 0.2f);
        dropTable.put("hideGloves", 0.1f);
        dropTable.put("crossbow", 0.05f);
        dropTable.put("ironSword", 0.05f);
        dropTable.put("leatherHelmet", 0.15f);
        dropTable.put("lifeRing", 0.05f);


        findNewDestinationTimer = new Timer();

        walkAnimation = new WalkAnimation(Assets.wolfSpriteSheet, 4, 100, new int [] {3,1,0,2}, 0);

        destinationX = 0.0f;
        destinationY = 0.0f;
        hasDestination = false;

        hitbox = new Rectangle(17, 15, 30, 46);

        setMaxHealthPoints(1f);
        setHealthPoints(getMaxHealthPoints());


        equipmentItems.put(0, new Item(ItemType.getItemType("woodenSword"), null));

        dashAbility = new DashAbility(this);
        dashAbility.onPerform(() -> { dogBarkSound.play(1.0f, 0.1f); });

        updateAttackType();



    }

    @Override
    public void onInit() {

    }

    @Override
    public void update(GameContainer gc, int i, KeyInput keyInput) {
        super.update(gc, i, keyInput);
    }

    @Override
    public String getCreatureType() {
        return "wolf";
    }



    @Override
    public void performActions(GameContainer gc, KeyInput keyInput) {
        super.performActions(gc, keyInput);
        float dashDistance = 250f;

        if (aggroed != null) {
            if (hasDestination) {
                if (Globals.distance(aggroed.rect, rect) < dashDistance) {
                    dashAbility.setDashVector(new Vector2f(destinationX - rect.getX(), destinationY - rect.getY()).normalise());
                    dashAbility.tryPerforming();
                }
            }
        }

        dashAbility.update();



    }

    @Override
    public void takeDamage(float damage) {
        if (!immune) {

            float beforeHP = healthPoints;

            float postMitigationDamage = damage * 100f/(100f + getTotalArmor());

            if (healthPoints - postMitigationDamage > 0) healthPoints -= postMitigationDamage;
            else healthPoints = 0f;

            if (beforeHP != healthPoints && healthPoints == 0f) {
                onDeath();
            }

            immunityTimer.reset();
            immune = true;
            dogWhimperSound.play(1.0f, 0.1f);
        }

    }

    @Override
    protected void performAbilityOnUpdateStart(int i) {
        dashAbility.performOnUpdateStart(i);
    }

    @Override
    public void performAbilityMovement() {
        dashAbility.performMovement();
    }
}
