package com.kamilkurp.creatures;

import com.kamilkurp.KeyInput;
import com.kamilkurp.animations.WalkAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.items.Item;
import com.kamilkurp.items.ItemType;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;

import java.util.HashMap;

public class Skeleton extends Mob {

    private Sound bounceSound = Assets.bounceSound;

    public Skeleton(GameSystem gameSystem, String id, String weapon) throws SlickException {
        super(gameSystem, id);

        actionTimer = new Timer();

        dropTable = new HashMap<>();
        dropTable.put("ringmailGreaves", 0.9f);
        dropTable.put("leatherArmor", 0.2f);
        dropTable.put("hideGloves", 0.1f);
        dropTable.put("ironSword", 0.07f);
        dropTable.put("woodenSword", 0.2f);
        dropTable.put("leatherHelmet", 0.15f);
        dropTable.put("crossbow", 0.02f);
        dropTable.put("healingPowder", 0.3f);


        findNewDestinationTimer = new Timer();

        walkAnimation = new WalkAnimation(Assets.skeletonSpriteSheet, 9, 100, new int [] {0,1,2,3}, 0);

        destinationX = 0.0f;
        destinationY = 0.0f;
        hasDestination = false;

        hitbox = new Rectangle(17, 15, 30, 46);

        setMaxHealthPoints(110f);
        setHealthPoints(getMaxHealthPoints());

        equipmentItems.put(0, new Item(ItemType.getItemType(weapon), null));

        updateAttackType();



    }

    @Override
    public void onInit() {

    }

    @Override
    public void update(GameContainer gc, int i, KeyInput keyInput, GameSystem gameSystem) {
        super.update(gc, i, keyInput, gameSystem);
    }


    @Override
    public String getCreatureType() {
        return "skeleton";
    }

    @Override
    public void takeDamage(float damage, boolean immunityFrames) {
        if (isAlive()) {

            float beforeHP = healthPoints;

            float actualDamage = damage * 100f/(100f + getTotalArmor());

            if (healthPoints - actualDamage > 0) healthPoints -= actualDamage;
            else healthPoints = 0f;

            if (beforeHP != healthPoints && healthPoints == 0f) {
                onDeath();
            }

            if (immunityFrames) {
                immunityTimer.reset();
                immune = true;
            }

            bounceSound.play(1.0f, 0.1f);
        }

    }

}
