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
import org.newdawn.slick.geom.Rectangle;

import java.util.HashMap;

public class Goblin extends Mob {


    public Goblin(GameSystem gameSystem, String id, String weapon) throws SlickException {
        super(gameSystem, id);

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

        walkAnimation = new WalkAnimation(Assets.goblinSpriteSheet, 3, 100, new int [] {3,1,0,2}, 0);

        destinationX = 0.0f;
        destinationY = 0.0f;
        hasDestination = false;

        hitbox = new Rectangle(17, 15, 30, 46);

        setMaxHealthPoints(500f);
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
        return "goblin";
    }
}
