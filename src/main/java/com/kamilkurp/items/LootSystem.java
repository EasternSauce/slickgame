package com.kamilkurp.items;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.Renderable;
import com.kamilkurp.creatures.PlayerCharacter;
import com.kamilkurp.gui.LootOptionWindow;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.terrain.CurrentAreaManager;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LootSystem implements Renderable {
    private final LootOptionWindow lootOptionWindow;

    private final CurrentAreaManager currentAreaManager;

    public LootSystem(LootOptionWindow lootOptionWindow, CurrentAreaManager currentAreaManager) {
        this.lootOptionWindow = lootOptionWindow;

        this.currentAreaManager = currentAreaManager;

    }

    @Override
    public void render(Graphics g, Camera camera) {
        for (LootPile lootPile : currentAreaManager.getCurrentArea().getLootPileList()) {
            lootPile.render(g, camera);
        }
    }

    public void update(KeyInput keyInput, PlayerCharacter playerCharacter) {
//        List<LootPile> visibleLootPile = new LinkedList<>();

        List<Item> visibleItems = new ArrayList<>();
        for (LootPile lootPile : currentAreaManager.getCurrentArea().getLootPileList()) {
            if (currentAreaManager.getCurrentArea() == lootPile.getArea()) {

                if (Globals.distance(playerCharacter.getRect(), lootPile.getRect()) < 40f) {
                    lootOptionWindow.setVisible(true);

//                    visibleLootPile.add(lootPile);
                    visibleItems.addAll(lootPile.getItemList());

                }
            }

        }

        lootOptionWindow.setLootOptions(visibleItems);
        lootOptionWindow.update(keyInput);


    }

    public void spawnLootPile(Area area, float x, float y, Map<String, Float> dropTable) {
        LootPile newLootPile = new LootPile(area, x, y);

        for (Map.Entry<String, Float> entry : dropTable.entrySet()) {
            if (Globals.random.nextFloat() < entry.getValue()) {
                Item item = new Item(ItemType.getItemType(entry.getKey()), newLootPile);
                newLootPile.addItem(item);
            }

        }

        if (!newLootPile.getItemList().isEmpty()) {
            area.getLootPileList().add(newLootPile);
        }
    }


}
