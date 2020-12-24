package com.kamilkurp.creatures;

import com.kamilkurp.KeyInput;
import com.kamilkurp.animations.WalkAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.abilities.Ability;
import com.kamilkurp.abilities.ExplodeAbility;
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

public class Ghost extends Mob {
    private Sound darkLaughSound = Assets.darkLaughSound;
    private Sound evilYellingSound = Assets.evilYellingSound;

    ExplodeAbility explodeAbility;

    public Ghost(GameSystem gameSystem, MobSpawnPoint mobSpawnPoint, String id, String weapon) throws SlickException {
        super(gameSystem, mobSpawnPoint, id);

        actionTimer = new Timer();

        dropTable = new HashMap<>();

        dropTable.put("healingPowder", 0.3f);
        dropTable.put("steelArmor", 0.03f);
        dropTable.put("steelGreaves", 0.05f);
        dropTable.put("steelGloves", 0.05f);
        dropTable.put("steelHelmet", 0.05f);
        dropTable.put("ironSword", 0.03f);
        dropTable.put("lifeRing", 0.01f);


        findNewDestinationTimer = new Timer();

        walkAnimation = new WalkAnimation(Assets.ghostSpriteSheet, 3, 100, new int [] {3,1,0,2}, 0);

        destinationX = 0.0f;
        destinationY = 0.0f;
        hasDestination = false;

        hitbox = new Rectangle(17, 15, 30, 46);

        setMaxHealthPoints(600f);
        setHealthPoints(getMaxHealthPoints());

        equipmentItems.put(0, new Item(ItemType.getItemType(weapon), null));





    }

    @Override
    public void attack() {

        if (healthPoints > maxHealthPoints * 0.30) {
            if (staminaPoints > 0f) {
                if (currentAttackType == AttackType.UNARMED) {
                    unarmedAttackAbility.tryPerforming();
                } else if (currentAttackType == AttackType.SWORD) {
                    swordAttackAbility.tryPerforming();
                } else if (currentAttackType == AttackType.BOW) {
                    bowAttackAbility.tryPerforming();
                }
            }
        }
        else {
            explodeAbility.tryPerforming();
        }
    }

    @Override
    public void onInit() {
        defineAbilities();

        explodeAbility = ExplodeAbility.newInstance(this);
        explodeAbility.onStartChannelAction(() -> { darkLaughSound.play(1.0f, 0.1f); });
        abilityList.add(explodeAbility);

        updateAttackType();
    }

    @Override
    public void update(GameContainer gc, int i, KeyInput keyInput, GameSystem gameSystem) {
        super.update(gc, i, keyInput, gameSystem);
    }

    @Override
    public void onDeath() {
        gameSystem.getLootSystem().spawnLootPile(area, rect.getCenterX(), rect.getCenterY(), dropTable);

        for (Ability ability : abilityList) {
            if (ability instanceof ExplodeAbility) continue;
            ability.stopAbility();
        }
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

            if (!knockback && knockbackPower > 0f) {
                this.knockbackPower = knockbackPower;

                knockbackVector = new Vector2f(rect.getX() - sourceX, rect.getY() - sourceY).getNormal();
                knockback = true;
                knockbackTimer.reset();

            }

            evilYellingSound.play(1.0f, 0.1f);
        }

    }

    @Override
    public void onUpdateStart(int i) {
        moving = false;

        totalDirections = 0;

        knockbackSpeed = knockbackPower * i;

        dirX = 0;
        dirY = 0;

        speed = 0.25f * i;

    }

    @Override
    public String getCreatureType() {
        return "ghost";
    }
}
