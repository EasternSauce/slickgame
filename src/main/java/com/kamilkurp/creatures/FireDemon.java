package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.abilities.DashAbility;
import com.kamilkurp.abilities.FistSlamAbility;
import com.kamilkurp.abilities.MeteorCrashAbility;
import com.kamilkurp.abilities.MeteorRainAbility;
import com.kamilkurp.animations.WalkAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.spawn.MobSpawnPoint;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class FireDemon extends Boss {

    protected MeteorRainAbility meteorRainAbility;
    protected FistSlamAbility fistSlamAbility;
    protected MeteorCrashAbility meteorCrashAbility;
    protected DashAbility dashAbility;

    public FireDemon(GameSystem gameSystem, MobSpawnPoint mobSpawnPoint, String id, String weapon) throws SlickException {
        super(gameSystem, mobSpawnPoint, id);

        scale = 2.0f;

        rect = new Rectangle(0, 0, 80 * scale, 80 * scale);
        hitbox = new Rectangle(0, 0, 80 * scale, 80 * scale);

        actionTimer = new Timer(true);

        dropTable.put("ironSword", 0.3f);
        dropTable.put("poisonDagger", 0.3f);
        dropTable.put("steelArmor", 0.8f);
        dropTable.put("steelHelmet", 0.5f);
        dropTable.put("thiefRing", 1.0f);

        walkAnimation = new WalkAnimation(Assets.fireDemonSpriteSheet, 4, 300, new int [] {3,1,0,2}, 0);

        setMaxHealthPoints(2500f);
        setHealthPoints(getMaxHealthPoints());

        grantWeapon(weapon);

        name = "Magma Stalker";

        aggroDistance = 500f;
        attackDistance = 500f;
        walkUpDistance = 500f;

        bossMusic = Assets.fireDemon;

        onGettingHitSound = Assets.roarSound;

        baseSpeed = 0.15f;

        creatureType = "fireDemon";

    }

    @Override
    public void performCombatAbilities() {

        if (!immobilized && isNoAbilityActive() && aggroed != null) {
            if (meteorRainAbility.canPerform() && healthPoints < maxHealthPoints * 0.7) {
                meteorRainAbility.perform();
                Assets.monsterGrowlSound.play(1.0f, 0.5f);
            }
            else if (fistSlamAbility.canPerform() && Globals.distance(aggroed.getRect(), rect) < 80f) {
                fistSlamAbility.perform();
            }
            else if (meteorCrashAbility.canPerform() && Globals.distance(aggroed.getRect(), rect) > 220f) {
                meteorCrashAbility.perform();
            }
            else if (currentAttack.canPerform() && Globals.distance(aggroed.getRect(), rect) < 170f) {
                currentAttack.perform();
            }
            else if (dashAbility.canPerform() && Globals.distance(aggroed.getRect(), rect) > 300f) {
                if (hasDestination) {
                    dashAbility.setDashVector(new Vector2f(destinationX - rect.getX(), destinationY - rect.getY()).getNormal());
                    dashAbility.perform();
                }
            }
        }

    }

    @Override
    protected void defineCustomAbilities() {
        tridentAttack.setAttackRange(45f);
        tridentAttack.setScale(2.0f);

        meteorRainAbility = MeteorRainAbility.newInstance(this);
        fistSlamAbility = FistSlamAbility.newInstance(this);
        meteorCrashAbility = MeteorCrashAbility.newInstance(this);
        dashAbility = DashAbility.newInstance(this);

        abilityList.add(meteorRainAbility);
        abilityList.add(fistSlamAbility);
        abilityList.add(meteorCrashAbility);
        abilityList.add(dashAbility);
    }

    @Override
    public void onAggroed() {
        if (!bossBattleStarted) {
            bossBattleStarted = true;

            bossMusic.loop(1.0f, Globals.MUSIC_VOLUME);

            gameSystem.getHud().getBossHealthBar().onBossBattleStart(this);

            mobSpawnPoint.getBlockade().setActive(true);

            Assets.monsterGrowlSound.play(1.0f, 0.5f);
        }
    }
}
