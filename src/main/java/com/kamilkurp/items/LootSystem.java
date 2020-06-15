package com.kamilkurp.items;

import com.kamilkurp.KeyInput;
import com.kamilkurp.gui.OptionWindow;
import com.kamilkurp.utils.Camera;
import com.kamilkurp.Globals;
import com.kamilkurp.Renderable;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LootSystem implements Renderable {
    private List<LootPile> lootPileList;
    private OptionWindow optionWindow;
    private Map<String, ItemType> itemTypes;

    public LootSystem(OptionWindow optionWindow, Map<String, ItemType> itemTypes) {
        this.optionWindow = optionWindow;
        this.itemTypes = itemTypes;

        lootPileList = new LinkedList<>();

//        Loot loot1 = new Loot(500, 1000);

//        lootList.add(loot1);
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
                optionWindow.setVisible(true);

                visibleLootPile.add(lootPile);
                visibleItems.addAll(lootPile.getItemList());

            }

        }

        optionWindow.setLootOptions(visibleItems);
        optionWindow.update(keyInput);


    }

    public void spawn(float x, float y, Map<String, Float> dropTable) {
        LootPile newLootPile = new LootPile(x, y);

        for (Map.Entry<String, Float> entry : dropTable.entrySet()) {
            if (Globals.random.nextFloat() < entry.getValue()) {
                Item item = new Item(itemTypes.get(entry.getKey()), newLootPile);
                newLootPile.addItem(item);
            }

        }

        if (!newLootPile.getItemList().isEmpty()) lootPileList.add(newLootPile);
    }


}
