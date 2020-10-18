package com.kamilkurp.gui;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.Renderable;
import com.kamilkurp.items.InventoryWindow;
import com.kamilkurp.items.Item;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;

public class LootOptionWindow implements Renderable {
    private boolean visible = false;

    private int currentSelected = 0;

    private boolean activated = false;

    private List<Item> itemList;

    private final InventoryWindow inventoryWindow;

    private int scroll = 0;

    public LootOptionWindow(InventoryWindow inventoryWindow) {
        this.inventoryWindow = inventoryWindow;
    }

    @Override
    public void render(Graphics g, Camera camera) {
        if (visible) {


            for (int i = 0; i < Math.min(4, itemList.size()); i++) {
                g.drawString((currentSelected == (i + scroll) && activated ? ">" : "") + itemList.get(i + scroll).getName(), 10, Globals.SCREEN_HEIGHT * Globals.SCREEN_PROPORTION + 10 + 30 * i);
            }


        }
    }

    public void update(KeyInput keyInput) {

        if (visible) {

            if (activated) {
                if (keyInput.isKeyPressed(KeyInput.Key.W)) {
                    if (currentSelected > 0) {
                        currentSelected--;
                        if (scroll > currentSelected) scroll--;
                    }
                }
                if (keyInput.isKeyPressed(KeyInput.Key.S)) {
                    if (currentSelected < itemList.size() - 1) {
                        currentSelected++;
                        if (scroll + 4 <= currentSelected) scroll++;
                    }
                }
                if (keyInput.isKeyPressed(KeyInput.Key.E)) {
                    if (itemList.size() != 0) {
                        inventoryWindow.pickUpItem(itemList.get(currentSelected), itemList);
                        if (currentSelected > 0) currentSelected--;
                        if(scroll > 0) scroll--;
                        if (itemList.size() == 0) {
                            currentSelected = 0;
                            activated = false;
                        }
                    }
                }
                if (keyInput.isKeyPressed(KeyInput.Key.ESC)) {
                    activated = false;
                    currentSelected = 0;
                }
            }
            else {
                if (keyInput.isKeyPressed(KeyInput.Key.E)) {
                    if (itemList.size() != 0) {
                        activated = true;
                        currentSelected = 0;
                    }
                }
            }



        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }


    public boolean isActivated() {
        return activated;
    }


    public void setLootOptions(List<Item> itemsInLoot) {
        if (!itemsInLoot.equals(itemList)) {
            itemList = new ArrayList<>(itemsInLoot);
        }


    }
}
