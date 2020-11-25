package com.kamilkurp.creatures;

import com.kamilkurp.KeyInput;
import com.kamilkurp.animations.WalkAnimation;
import com.kamilkurp.assets.Assets;
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
        dropTable.put("ringmailGreaves", 0.9f);
        dropTable.put("leatherArmor", 0.2f);
        dropTable.put("hideGloves", 0.1f);
        dropTable.put("crossbow", 0.05f);
        dropTable.put("ironSword", 0.05f);
        dropTable.put("leatherHelmet", 0.15f);
        dropTable.put("lifeRing", 0.05f);


        findNewDestinationTimer = new Timer();

        walkAnimation = new WalkAnimation(Assets.ghostSpriteSheet, 3, 100, new int [] {3,1,0,2}, 0);
        walkAnimation = new WalkAnimation(Assets.ghostSpriteSheet, 3, 100, new int [] {3,1,0,2}, 0);

        destinationX = 0.0f;
        destinationY = 0.0f;
        hasDestination = false;

        hitbox = new Rectangle(17, 15, 30, 46);

        setMaxHealthPoints(60f);
        setHealthPoints(getMaxHealthPoints());

        equipmentItems.put(0, new Item(ItemType.getItemType(weapon), null));

        explodeAbility = new ExplodeAbility(this);
        abilityList.add(explodeAbility);


        updateAttackType();



    }

    @Override
    public void attack() {

        if (healthPoints > maxHealthPoints / 2) {
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
//        System.out.println("is immobilized: " + immobilized);
    }


    @Override
    public String getCreatureType() {
        return "ghost";
    }
}
