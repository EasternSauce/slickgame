package com.kamilkurp.creatures;

import com.kamilkurp.abilities.Ability;
import com.kamilkurp.abilities.ExplodeAbility;
import com.kamilkurp.animations.WalkAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.spawn.MobSpawnPoint;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

public class Ghost extends Mob {

    private ExplodeAbility explodeAbility;

    public Ghost(GameSystem gameSystem, MobSpawnPoint mobSpawnPoint, String id, String weapon) throws SlickException {
        super(gameSystem, mobSpawnPoint, id);

        actionTimer = new Timer(true);

        dropTable.put("healingPowder", 0.3f);
        dropTable.put("steelArmor", 0.03f);
        dropTable.put("steelGreaves", 0.05f);
        dropTable.put("steelGloves", 0.05f);
        dropTable.put("steelHelmet", 0.05f);
        dropTable.put("ironSword", 0.03f);
        dropTable.put("lifeRing", 0.01f);

        walkAnimation = new WalkAnimation(Assets.ghostSpriteSheet, 3, 100, new int [] {3,1,0,2}, 0);

        hitbox = new Rectangle(17, 15, 30, 46);

        onGettingHitSound = Assets.evilYellingSound;

        baseSpeed = 0.35f;

        creatureType = "ghost";

        setMaxHealthPoints(600f);
        setHealthPoints(getMaxHealthPoints());

        grantWeapon(weapon);

    }

    @Override
    public void performCombatAbilities() {

        if (healthPoints > maxHealthPoints * 0.30) {
            if (currentAttack.canPerform()) {
                currentAttack.perform();
            }
        }
        else {
            if (explodeAbility.canPerform()) {
                explodeAbility.perform();
            }
        }
    }

    @Override
    public void onInit() {
        defineStandardAbilities();

        explodeAbility = ExplodeAbility.newInstance(this);
        explodeAbility.onStartChannelAction(() -> { Assets.darkLaughSound.play(1.0f, 0.1f); });
        abilityList.add(explodeAbility);

        updateAttackType();
    }

    @Override
    public void onDeath() {
        gameSystem.getLootSystem().spawnLootPile(area, rect.getCenterX(), rect.getCenterY(), dropTable);

        for (Ability ability : abilityList) {
            if (ability instanceof ExplodeAbility) continue;
            ability.stopAbility();
        }

        currentAttack.stopAbility();
    }

}
