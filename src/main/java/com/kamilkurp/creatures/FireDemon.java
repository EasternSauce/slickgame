package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.abilities.*;
import com.kamilkurp.animations.WalkAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.spawn.MobSpawnPoint;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.HashMap;
import java.util.Map;

public class FireDemon extends Boss {

    protected MeteorRainAbility meteorRainAbility;
    protected FistSlamAbility fistSlamAbility;
    protected MeteorCrashAbility meteorCrashAbility;
    protected DashAbility dashAbility;

    protected Sound roarSound = Assets.roarSound;

    public FireDemon(GameSystem gameSystem, MobSpawnPoint mobSpawnPoint, String id, String weapon) throws SlickException {
        super(gameSystem, mobSpawnPoint, id);


        scale = 2.0f;

        rect = new Rectangle(0, 0, 80 * scale, 80 * scale);
        hitbox = new Rectangle(0, 0, 80 * scale, 80 * scale);

        actionTimer = new Timer();

        dropTable = new HashMap<>();
        dropTable.put("ironSword", 0.3f);
        dropTable.put("poisonDagger", 0.3f);
        dropTable.put("steelArmor", 0.8f);
        dropTable.put("steelHelmet", 0.5f);
        dropTable.put("thiefRing", 1.0f);

        findNewDestinationTimer = new Timer();

        walkAnimation = new WalkAnimation(Assets.fireDemonSpriteSheet, 4, 300, new int [] {3,1,0,2}, 0);

        destinationX = 0.0f;
        destinationY = 0.0f;
        hasDestination = false;

        setMaxHealthPoints(2500f);
        setHealthPoints(getMaxHealthPoints());

        grantWeapon(weapon);

        name = "Magma Stalker";

        aggroDistance = 500f;
        attackDistance = 500f;
        walkUpDistance = 500f;

        bossMusic = Assets.fireDemon;

    }

    @Override
    public void performCombatAbilities() {

        if (!immobilized && isNoAbilityActive() && aggroed != null) {
            if (healthPoints < maxHealthPoints * 0.7) {
                meteorRainAbility.tryPerforming();
            }

            if (Globals.distance(aggroed.getRect(), rect) < 80f) {
                fistSlamAbility.tryPerforming();
            }

            if (Globals.distance(aggroed.getRect(), rect) > 220f) {
                meteorCrashAbility.tryPerforming();
            }

            if (Globals.distance(aggroed.getRect(), rect) < 170f) {
                currentAttack.tryPerforming();
            }

            if (Globals.distance(aggroed.getRect(), rect) > 300f) {
                if (hasDestination) {
                    dashAbility.setDashVector(new Vector2f(destinationX - rect.getX(), destinationY - rect.getY()).getNormal());
                    dashAbility.tryPerforming();
                }
            }
        }

    }

    @Override
    public void onInit() {
        defineAbilities();

        tridentAttack.setAttackRange(45f);
        tridentAttack.setScale(2.0f);

        meteorRainAbility = MeteorRainAbility.newInstance(this);
        abilityList.add(meteorRainAbility);

        fistSlamAbility = FistSlamAbility.newInstance(this);
        abilityList.add(fistSlamAbility);

        meteorCrashAbility = MeteorCrashAbility.newInstance(this);
        abilityList.add(meteorCrashAbility);

        dashAbility = DashAbility.newInstance(this);
        abilityList.add(dashAbility);

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

        speed = 0.15f * i;

        if (isAttacking) {
            speed = speed / 2f;
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

            if (knocbackable && !knockback && knockbackPower > 0f) {
                this.knockbackPower = knockbackPower;

                knockbackVector = new Vector2f(rect.getX() - sourceX, rect.getY() - sourceY).getNormal();
                knockback = true;
                knockbackTimer.reset();

            }

            if (Globals.randFloat() < 0.3) roarSound.play(1.0f, 0.1f);

        }

    }

}
