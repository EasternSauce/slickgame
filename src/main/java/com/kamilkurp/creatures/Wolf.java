package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.animations.WalkAnimation;
import com.kamilkurp.areagate.AreaGate;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.items.Item;
import com.kamilkurp.items.ItemType;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.projectile.Arrow;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.terrain.TerrainTile;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.*;

public class Wolf extends Mob {




    public Wolf(String id, int posX, int posY, Area area, LootSystem lootSystem) throws SlickException {
        super(id, posX, posY, area, lootSystem);

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

        walkAnimation = new WalkAnimation(Assets.wolfSpriteSheet, 4, 100, new int [] {3,1,2,0}, 0);

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
    public void update(GameContainer gc, int i, List<TerrainTile> tiles, Map<String, Creature> creatures, KeyInput keyInput, List<Arrow> arrowList, List<AreaGate> gatesList) throws SlickException {
        super.update(gc, i ,tiles, creatures, keyInput, arrowList, gatesList);
    }

    @Override
    public String getCreatureType() {
        return "wolf";
    }


}