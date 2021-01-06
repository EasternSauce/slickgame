package com.kamilkurp.creatures;

import com.kamilkurp.KeyInput;
import com.kamilkurp.animations.WalkAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.items.Item;
import com.kamilkurp.items.ItemType;
import com.kamilkurp.spawn.MobSpawnPoint;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.HashMap;

public class Skeleton extends Mob {

    private Sound boneClickSound = Assets.boneClickSound;

    public Skeleton(GameSystem gameSystem, MobSpawnPoint mobSpawnPoint, String id, String weapon) throws SlickException {
        super(gameSystem, mobSpawnPoint, id);

        actionTimer = new Timer(true);

        dropTable = new HashMap<>();

        dropTable.put("ringmailGreaves", 0.1f);
        dropTable.put("leatherArmor", 0.05f);
        dropTable.put("hideGloves", 0.1f);
        dropTable.put("leatherHelmet", 0.1f);
        dropTable.put("woodenSword", 0.1f);
        dropTable.put("healingPowder", 0.5f);

        walkAnimation = new WalkAnimation(Assets.skeletonSpriteSheet, 9, 100, new int [] {0,1,2,3}, 0);

        destinationX = 0.0f;
        destinationY = 0.0f;
        hasDestination = false;

        hitbox = new Rectangle(17, 15, 30, 46);

        setMaxHealthPoints(200f);
        setHealthPoints(getMaxHealthPoints());

        grantWeapon(weapon);

    }

    @Override
    public void onInit() {
        defineAbilities();

        updateAttackType();
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
    public void takeDamage(float damage, boolean immunityFrames, float knockbackPower, float sourceX, float sourceY) {

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

            if (knocbackable && !knockback && knockbackPower > 0f) {
                this.knockbackPower = knockbackPower;

                knockbackVector = new Vector2f(rect.getX() - sourceX, rect.getY() - sourceY).getNormal();
                knockback = true;
                knockbackTimer.reset();

            }

            boneClickSound.play(1.0f, 0.1f);
        }

    }
}
