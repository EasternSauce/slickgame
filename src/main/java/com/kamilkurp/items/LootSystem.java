package com.kamilkurp.items;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.Renderable;
import com.kamilkurp.gui.LootOptionWindow;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LootSystem implements Renderable {
    private List<LootPile> lootPileList;
    private LootOptionWindow lootOptionWindow;

    public LootSystem(LootOptionWindow lootOptionWindow) {
        this.lootOptionWindow = lootOptionWindow;

        lootPileList = new LinkedList<>();

    }

    @Override
    public void render(Graphics g, Camera camera) {
        for (LootPile lootPile : lootPileList) {
            lootPile.render(g, camera);
        }
    }

    public void update(KeyInput keyInput, com.kamilkurp.creatures.Character character) {
        List<LootPile> visibleLootPile = new LinkedList<>();

        List<Item> visibleItems = new ArrayList<>();
        for (LootPile lootPile : lootPileList) {
            if (Globals.distance(character.getRect(), lootPile.getRect()) < 40f) {
                lootOptionWindow.setVisible(true);

                visibleLootPile.add(lootPile);
                visibleItems.addAll(lootPile.getItemList());

            }

        }

        lootOptionWindow.setLootOptions(visibleItems);
        lootOptionWindow.update(keyInput);


    }

    public void spawnLootPile(float x, float y, Map<String, Float> dropTable) {
        LootPile newLootPile = new LootPile(x, y);

        for (Map.Entry<String, Float> entry : dropTable.entrySet()) {
            if (Globals.random.nextFloat() < entry.getValue()) {
                Item item = new Item(ItemType.getItemType(entry.getKey()), newLootPile);
                newLootPile.addItem(item);
                System.out.println("added item to lootpile: " + item.getName());
            }

        }

        if (!newLootPile.getItemList().isEmpty()) lootPileList.add(newLootPile);
    }


}
