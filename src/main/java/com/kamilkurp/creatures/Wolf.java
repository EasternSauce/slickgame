package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.abilities.DashAbility;
import com.kamilkurp.animations.WalkAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.spawn.MobSpawnPoint;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class Wolf extends Mob {
    private DashAbility dashAbility;

    public Wolf(GameSystem gameSystem, MobSpawnPoint mobSpawnPoint, String id) throws SlickException {
        super(gameSystem, mobSpawnPoint, id);

        actionTimer = new Timer(true);

        dropTable.put("ringmailGreaves", 0.1f);
        dropTable.put("leatherArmor", 0.05f);
        dropTable.put("hideGloves", 0.1f);
        dropTable.put("leatherHelmet", 0.1f);
        dropTable.put("healingPowder", 0.5f);

        walkAnimation = new WalkAnimation(Assets.wolfSpriteSheet, 4, 100, new int [] {3,1,0,2}, 0);

        hitbox = new Rectangle(17, 15, 30, 46);

        setMaxHealthPoints(150f);
        setHealthPoints(getMaxHealthPoints());

        unarmedDamage = 30f;

        onGettingHitSound = Assets.dogWhimperSound;

        creatureType = "wolf";

        baseSpeed = 0.3f;

    }

    @Override
    public void onInit() {
        defineStandardAbilities();

        dashAbility = DashAbility.newInstance(this);
        dashAbility.onStartActiveAction(() -> { Assets.dogBarkSound.play(1.0f, 0.1f); });
        abilityList.add(dashAbility);

        updateAttackType();
    }

    @Override
    public void performAggroedBehavior() {
        float dashDistance = 250f;

        if (hasDestination) {
            if (dashAbility.canPerform() && Globals.distance(aggroed.rect, rect) < dashDistance) {
                dashAbility.setDashVector(new Vector2f(destinationX - rect.getX(), destinationY - rect.getY()).getNormal());
                dashAbility.perform();
            }
        }
    }
}
