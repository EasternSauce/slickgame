package com.kamilkurp.creatures;

import com.kamilkurp.KeyInput;
import com.kamilkurp.animations.WalkAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.behavior.Ability;
import com.kamilkurp.behavior.ExplodeAbility;
import com.kamilkurp.items.Item;
import com.kamilkurp.items.ItemType;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import java.util.HashMap;

public class Ghost extends Mob {

    ExplodeAbility explodeAbility;

    public Ghost(GameSystem gameSystem, String id, String weapon) throws SlickException {
        super(gameSystem, id);

        actionTimer = new Timer();

        dropTable = new HashMap<>();

        dropTable.put("healingPowder", 0.1f);
        dropTable.put("steelArmor", 0.01f);
        dropTable.put("steelGreaves", 0.03f);
        dropTable.put("steelGloves", 0.03f);
        dropTable.put("steelHelmet", 0.02f);
        dropTable.put("ironSword", 0.01f);
        dropTable.put("lifeRing", 0.008f);


        findNewDestinationTimer = new Timer();

        walkAnimation = new WalkAnimation(Assets.ghostSpriteSheet, 3, 100, new int [] {3,1,0,2}, 0);

        destinationX = 0.0f;
        destinationY = 0.0f;
        hasDestination = false;

        hitbox = new Rectangle(17, 15, 30, 46);

        setMaxHealthPoints(800f);
        setHealthPoints(getMaxHealthPoints());

        equipmentItems.put(0, new Item(ItemType.getItemType(weapon), null));

        explodeAbility = new ExplodeAbility(this);
        abilityList.add(explodeAbility);


        updateAttackType();



    }

    @Override
    public void attack() {

        if (healthPoints > maxHealthPoints * 0.70) {
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
    public String getCreatureType() {
        return "ghost";
    }
}
