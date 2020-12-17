package com.kamilkurp.creatures;

import com.kamilkurp.KeyInput;
import com.kamilkurp.abilities.AbilityState;
import com.kamilkurp.abilities.MeteorRainAbility;
import com.kamilkurp.animations.WalkAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.items.Item;
import com.kamilkurp.items.ItemType;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.HashMap;

public class FireDemon extends Mob {

    protected MeteorRainAbility meteorRainAbility;

    public FireDemon(GameSystem gameSystem, String id, String weapon) throws SlickException {
        super(gameSystem, id);


        scale = 2.0f;

        rect = new Rectangle(0, 0, 80 * scale, 80 * scale);
        hitbox = new Rectangle(0, 0, 80 * scale, 80 * scale);

        actionTimer = new Timer();

        dropTable = new HashMap<>();
        dropTable.put("ironSword", 0.1f);
        dropTable.put("poisonDagger", 0.1f);
        dropTable.put("healingPowder", 0.4f);
        dropTable.put("steelArmor", 0.1f);
        dropTable.put("steelGreaves", 0.2f);
        dropTable.put("steelGloves", 0.2f);
        dropTable.put("steelHelmet", 0.2f);


        findNewDestinationTimer = new Timer();

        walkAnimation = new WalkAnimation(Assets.fireDemonSpriteSheet, 4, 300, new int [] {3,1,0,2}, 0);

        destinationX = 0.0f;
        destinationY = 0.0f;
        hasDestination = false;

        setMaxHealthPoints(2500f);
        setHealthPoints(getMaxHealthPoints());

        equipmentItems.put(0, new Item(ItemType.getItemType(weapon), null));

        creatureType = "boss";

    }

    @Override
    public void attack() {

        if (staminaPoints > 0f && meteorRainAbility.getState() == AbilityState.ABILITY_INACTIVE) {
            if (healthPoints < maxHealthPoints * 0.70) meteorRainAbility.tryPerforming();

            if (currentAttackType == AttackType.UNARMED) {
                unarmedAttackAbility.tryPerforming();
            } else if (currentAttackType == AttackType.SWORD) {
                swordAttackAbility.tryPerforming();
            } else if (currentAttackType == AttackType.BOW) {
                bowAttackAbility.tryPerforming();
            } else if (currentAttackType == AttackType.TRIDENT) {
                tridentAttackAbility.tryPerforming();
            }
        }

    }

    @Override
    public void onInit() {
        defineAbilities();

        tridentAttackAbility.setAttackRange(45f);
        tridentAttackAbility.setScale(3.0f);

        meteorRainAbility = new MeteorRainAbility(this);
        abilityList.add(meteorRainAbility);

        updateAttackType();
    }

    @Override
    public void update(GameContainer gc, int i, KeyInput keyInput, GameSystem gameSystem) {
        super.update(gc, i, keyInput, gameSystem);
    }


    @Override
    public String getCreatureType() {
        return "fireDemon";
    }

    @Override
    public void onUpdateStart(int i) {
        moving = false;

        totalDirections = 0;

        knockbackSpeed = knockbackPower * i;

        dirX = 0;
        dirY = 0;

        speed = 0.5f * i;

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

//            if (!knockback && knockbackPower > 0f) {
//                this.knockbackPower = knockbackPower;
//
//                knockbackVector = new Vector2f(rect.getX() - sourceX, rect.getY() - sourceY).getNormal();
//                knockback = true;
//                knockbackTimer.reset();
//
//            }

            //demonsound.play
        }

    }
}
