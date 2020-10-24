package com.kamilkurp.creatures;

import com.kamilkurp.KeyInput;
import com.kamilkurp.animations.WalkAnimation;
import com.kamilkurp.areagate.AreaGate;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.items.Item;
import com.kamilkurp.items.ItemType;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.projectile.Arrow;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.terrain.TerrainTile;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Skeleton extends Mob {


    public Skeleton(GameSystem gameSystem, String id, float posX, float posY, Area area) throws SlickException {
        super(gameSystem, id, posX, posY, area);

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

        walkAnimation = new WalkAnimation(Assets.skeletonSpriteSheet, 9, 100, new int [] {0,1,2,3}, 0);

        destinationX = 0.0f;
        destinationY = 0.0f;
        hasDestination = false;

        hitbox = new Rectangle(17, 15, 30, 46);

        setMaxHealthPoints(1f);
        setHealthPoints(getMaxHealthPoints());


        equipmentItems.put(0, new Item(ItemType.getItemType("woodenSword"), null));

        updateAttackType();



    }

    @Override
    public void update(GameContainer gc, int i, KeyInput keyInput, Area area, Map<String, Creature> creaturesMap) {
        super.update(gc, i, keyInput, area, creaturesMap);
    }

    @Override
    public String getCreatureType() {
        return "skeleton";
    }
}
