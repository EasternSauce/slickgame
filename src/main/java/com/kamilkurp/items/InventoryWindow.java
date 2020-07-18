package com.kamilkurp.items;

import com.kamilkurp.*;
import com.kamilkurp.Renderable;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;

import java.util.*;

// too many responsibilities
public class InventoryWindow implements Renderable {

    private Rectangle background = new Rectangle((int)(Globals.SCREEN_WIDTH * 0.1), (int)(Globals.SCREEN_HEIGHT * 0.1), (int)(Globals.SCREEN_WIDTH * 0.6), (int)(Globals.SCREEN_HEIGHT * 0.6));

    private List<Rectangle> slotList;
    private List<Rectangle> equipmentSlotList;
    private List<Rectangle> traderInventorySlotList;


    private boolean visible = false;

    private SpriteSheet itemIcons;

    private int currentSelected = 0;

    private boolean moving = false;
    private int currentMoved = 0;
    private boolean movingInEquipment = false;

    private Map<String, ItemType> itemTypes;

    private Map<Integer, Item> items;

    private Map<Integer, Item> equipmentItems;

    private Map<Integer, Item> traderInventoryItems;


    private List<String> equipmentSlotNameList;

    private boolean inEquipment = false;

    private boolean inTraderInventory = false;

    private boolean trading = false;

    private int inventoryRows = 4;
    private int inventoryColumns = 5;
    private int inventorySlots = inventoryRows * inventoryColumns;

    private int space = 10;
    private int width = 40;
    private int height = 40;

    private int equipmentSlots = 5;

    private int tradeInventoryRows = 6;
    private int tradeInventoryColumns = 2;
    private int tradeInventorySlots = tradeInventoryRows * tradeInventoryColumns;

    private int gold = 0;

    public InventoryWindow() throws SlickException {

        slotList = new LinkedList<>();
        equipmentSlotList = new LinkedList<>();
        traderInventorySlotList = new LinkedList<>();

        equipmentSlotNameList = Arrays.asList("Helmet","Body","Gloves","Ring","Boots");

        Image image = new Image("item_icons.png");
        itemIcons = new SpriteSheet(image, width, height);

        itemTypes = new HashMap<>();

        ItemType itemType1 = new ItemType("skinTunic", "Leather Armor", "-", itemIcons.getSubImage(0,0), "body", 50);
        ItemType itemType2 = new ItemType("ringmailGreaves", "Ringmail Greaves", "-", itemIcons.getSubImage(1,0), "boots", 30);
        ItemType itemType3 = new ItemType("hideGloves", "Hide Gloves", "-", itemIcons.getSubImage(2,0), "gloves", 25);

        itemTypes.put(itemType1.getId(), itemType1);
        itemTypes.put(itemType2.getId(), itemType2);
        itemTypes.put(itemType3.getId(), itemType3);

        for (int i = 0; i < inventorySlots; i++) {
            int col = i % inventoryColumns;
            int row = i / inventoryColumns;
            Rectangle slot = new Rectangle(background.getX() + 50f + (space + width) * col,background.getY() + 50f + (space + height) * row, width, height);
            slotList.add(slot);
        }

        for (int i = 0; i < equipmentSlots; i++) {
            int col = inventoryColumns + 2;
            Rectangle slot = new Rectangle(background.getX() + 50f + (space + width) * col,background.getY() + 50f + (space + height) * i, width, height);
            equipmentSlotList.add(slot);
        }

        for (int i = 0; i < tradeInventorySlots; i++) {
            int col = inventoryColumns + 1 + i % (tradeInventoryColumns);
            int row = i / (tradeInventoryColumns);
            Rectangle slot = new Rectangle(background.getX() + 50f + (space + width) * col,background.getY() + 50f + (space + height) * row, width, height);
            traderInventorySlotList.add(slot);
        }

        items = new TreeMap<>();

//        items.put(0, new Item(itemTypes.get("item type 1")));
//        items.put(5, new Item(itemTypes.get("item type 1")));
//        items.put(17, new Item(itemTypes.get("item type 3")));
//        items.put(26, new Item(itemTypes.get("item type 2")));

        equipmentItems = new TreeMap<>();

        traderInventoryItems = new TreeMap<>();

//        for (int i = 0; i < 40; i++) {
//            items.put(i, null);
//        }
    }

    @Override
    public void render(Graphics g, Camera camera) {

        if (visible) {
            g.setColor(Color.darkGray);
            g.fill(background);

            renderInventory(g);

            renderEquipment(g);

            renderTraderInventory(g);

            renderItemDescription(g);
        }

    }

    private void renderTraderInventory(Graphics g) {
        if (trading) {
            for (int i = 0; i < tradeInventorySlots; i++) {
                g.setColor(Color.black);

                if (inTraderInventory) {
                    if (currentSelected == i) {
                        g.setColor(Color.red);
                    }
                }

                g.draw(traderInventorySlotList.get(i));
                if (traderInventoryItems.get(i) != null) {
                    g.drawImage(traderInventoryItems.get(i).getItemType().getImage(), traderInventorySlotList.get(i).getX(), traderInventorySlotList.get(i).getY());
                }
            }
            g.setColor(Color.white);
            g.drawString("Trader:", traderInventorySlotList.get(0).getX() + 5f, background.getY() + 15f);
        }
    }

    public void renderEquipment(Graphics g) {
        if (!trading) {
            for (int i = 0; i < equipmentSlots; i++) {
                g.setColor(Color.black);

                if (moving && currentMoved == i && movingInEquipment) {
                    g.setColor(Color.orange);
                } else if (inEquipment) {
                    if (currentSelected == i) {
                        g.setColor(Color.red);
                    }
                }

                g.draw(equipmentSlotList.get(i));
                if (equipmentItems.get(i) != null) {
                    g.drawImage(equipmentItems.get(i).getItemType().getImage(), equipmentSlotList.get(i).getX(), equipmentSlotList.get(i).getY());
                }

                g.setColor(Color.white);
                g.drawString(equipmentSlotNameList.get(i), equipmentSlotList.get(i).getX() - 60, equipmentSlotList.get(i).getY());
            }
        }
    }

    public void renderItemDescription(Graphics g) {
        g.setColor(Color.white);
        if (items.get(currentSelected) != null) g.drawString(items.get(currentSelected).getName(), background.getX() + space, background.getY() + 50f + (space + height) * inventoryRows + space);
        if (items.get(currentSelected) != null) g.drawString(items.get(currentSelected).getDescription(), background.getX() + space, background.getY() + 50f + (space + height) * inventoryRows + space + 25);
    }

    public void renderInventory(Graphics g) {
        for (int i = 0; i < inventorySlots; i++) {

            g.setColor(Color.black);

            if (moving && currentMoved == i && !movingInEquipment) {
                g.setColor(Color.orange);
            }
            else if (!inEquipment && !inTraderInventory) {

                if (currentSelected == i){
                    g.setColor(Color.red);
                }
            }

            g.draw(slotList.get(i));
            if (items.get(i) != null) {
                g.drawImage(items.get(i).getItemType().getImage(), slotList.get(i).getX(), slotList.get(i).getY());
            }
        }

        g.setColor(Color.yellow);
        g.drawString("Gold: " + gold, background.getX() + 5, background.getY() + 20f + (space + height) * (float)inventorySlots/ inventoryColumns + 90f);


    }

    private void toggleVisible() {
        if (visible) visible = false;
        else visible = true;
    }

    public void update(KeyInput keyInput) {
        if (keyInput.isKeyPressed(KeyInput.Key.I)) {
            toggleVisible();
        }
        else if (visible) {
            if (keyInput.isKeyPressed(KeyInput.Key.W)) {
                if (inEquipment) {
                    if (currentSelected > 0) {
                        currentSelected--;
                    }
                } else if (inTraderInventory) {
                    if (currentSelected >= tradeInventoryColumns) {
                        currentSelected = currentSelected - tradeInventoryColumns;
                    }
                } else {
                    if (currentSelected >= inventoryColumns) {
                        currentSelected = currentSelected - inventoryColumns;
                    }
                }
            }
            if (keyInput.isKeyPressed(KeyInput.Key.A)) {
                if (inEquipment) {
                    if (currentSelected * inventoryColumns + (inventoryColumns - 1) < inventorySlots) {
                        inEquipment = false;
                        currentSelected = currentSelected * inventoryColumns + (inventoryColumns - 1);
                    }
                } else if (inTraderInventory) {
                    if (currentSelected % tradeInventoryColumns == 0) {
                        if (currentSelected / tradeInventoryColumns < inventoryColumns - 1) {
                            inTraderInventory = false;
                            currentSelected = currentSelected / tradeInventoryColumns * inventoryColumns + (inventoryColumns - 1);
                        }
                    }
                    else {
                        if (currentSelected >= 1) currentSelected--;
                    }
                } else {
                    if (currentSelected >= 1) currentSelected--;
                }
            }

            if (keyInput.isKeyPressed(KeyInput.Key.S)) {
                if (inEquipment) {
                    if (currentSelected < equipmentSlots - 1) {
                        currentSelected++;
                    }
                } else if (inTraderInventory) {
                    if (currentSelected <= tradeInventorySlots - tradeInventoryColumns -1) currentSelected = currentSelected + tradeInventoryColumns;

                } else {
                    if (currentSelected <= inventorySlots- inventoryColumns -1) currentSelected = currentSelected + inventoryColumns;
                }
            }
            if (keyInput.isKeyPressed(KeyInput.Key.D)) {
                if (inTraderInventory) {
                    if (currentSelected < tradeInventorySlots -1) currentSelected++;
                }
                else if (!inEquipment) {
                    if ((currentSelected + 1) % inventoryColumns == 0) {
                        if (!trading) {
                            inEquipment = true;
                            currentSelected = currentSelected / inventoryColumns;
                        }
                        else {
                            inTraderInventory = true;
                            currentSelected = currentSelected / inventoryColumns * tradeInventoryColumns;
                        }

                    }
                    else {
                        if (currentSelected < inventorySlots-1) currentSelected++;
                    }

                }

            }
            if (keyInput.isKeyPressed(KeyInput.Key.E)) {
                if (!trading) {
                    if (!moving && (!inEquipment && items.get(currentSelected) != null) || (inEquipment && equipmentItems.get(currentSelected) != null)) {
                        currentMoved = currentSelected;
                        moving = true;
                        movingInEquipment = inEquipment;
                    }
                    else {
                        if (movingInEquipment) {
                            if (inEquipment) {
                                Item from = equipmentItems.get(currentMoved);
                                Item to = equipmentItems.get(currentSelected);

                                String currentEquipmentType = null;
                                if (currentSelected == 0) {
                                    currentEquipmentType = "helmet";
                                }
                                if (currentSelected == 1) {
                                    currentEquipmentType = "body";
                                }
                                if (currentSelected == 2) {
                                    currentEquipmentType = "gloves";
                                }
                                if (currentSelected == 3) {
                                    currentEquipmentType = "ring";
                                }
                                if (currentSelected == 4) {
                                    currentEquipmentType = "boots";
                                }

                                if (from.getItemType().getEquipmentType().equals(currentEquipmentType)) {
                                    equipmentItems.put(currentMoved, to);
                                    equipmentItems.put(currentSelected, from);
                                    moving = false;
                                }
                            }
                            else {
                                Item from = equipmentItems.get(currentMoved);
                                Item to = items.get(currentSelected);

                                String currentEquipmentType = null;
                                if (currentMoved == 0) {
                                    currentEquipmentType = "helmet";
                                }
                                if (currentMoved == 1) {
                                    currentEquipmentType = "body";
                                }
                                if (currentMoved == 2) {
                                    currentEquipmentType = "gloves";
                                }
                                if (currentMoved == 3) {
                                    currentEquipmentType = "ring";
                                }
                                if (currentMoved == 4) {
                                    currentEquipmentType = "boots";
                                }

                                if (to == null || to.getItemType().getEquipmentType().equals(currentEquipmentType)) {
                                    equipmentItems.put(currentMoved, to);
                                    items.put(currentSelected, from);
                                    moving = false;
                                }
                            }
                        }
                        else {
                            if (inEquipment) {
                                Item from = items.get(currentMoved);
                                Item to = equipmentItems.get(currentSelected);

                                String currentEquipmentType = null;
                                if (currentSelected == 0) {
                                    currentEquipmentType = "helmet";
                                }
                                if (currentSelected == 1) {
                                    currentEquipmentType = "body";
                                }
                                if (currentSelected == 2) {
                                    currentEquipmentType = "gloves";
                                }
                                if (currentSelected == 3) {
                                    currentEquipmentType = "ring";
                                }
                                if (currentSelected == 4) {
                                    currentEquipmentType = "boots";
                                }

                                if (from.getItemType().getEquipmentType().equals(currentEquipmentType)) {
                                    items.put(currentMoved, to);
                                    equipmentItems.put(currentSelected, from);
                                    moving = false;
                                }
                            }
                            else {
                                Item from = items.get(currentMoved);
                                Item to = items.get(currentSelected);
                                items.put(currentMoved, to);
                                items.put(currentSelected, from);
                                moving = false;
                            }

                        }


                    }
                }
                else {
                    if (items.get(currentSelected) != null) {
                        gold += items.get(currentSelected).getItemType().getWorth() * 0.3f;
                        items.remove(currentSelected);

                    }
                }

            }
            if (keyInput.isKeyPressed(KeyInput.Key.ESC)) {
                visible = false;
                if (trading) {
                    trading = false;
                }
            }
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public Map<String, ItemType> getItemTypes() {
        return itemTypes;
    }

    public boolean pickUpItem(Item item, List<Item> itemList) {

        for (int i = 0; i < inventorySlots; i++) {
            if (items.get(i) == null) {
                items.put(i, item);

                if (item.getLootPileBackref().itemList.size() == 1) {
                    item.getLootPileBackref().setVisible(false);
                }
                item.removeFromLoot();
                itemList.remove(item);


                return true;
            }


        }
        return false;

    }

    public void openTradeWindow() {
        visible = true;
        trading = true;
    }

    public boolean isTrading() {
        return trading;
    }
}
