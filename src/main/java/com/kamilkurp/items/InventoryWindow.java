package com.kamilkurp.items;

import com.kamilkurp.*;
import com.kamilkurp.Renderable;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;

import java.util.*;

// too many responsibilities
public class InventoryWindow implements Renderable {

    private Rectangle background = new Rectangle((int)(Globals.SCREEN_WIDTH * 0.2), (int)(Globals.SCREEN_HEIGHT * 0.2), (int)(Globals.SCREEN_WIDTH * 0.6), (int)(Globals.SCREEN_HEIGHT * 0.6));

    private List<Rectangle> slotList;
    private List<Rectangle> equipmentSlotList;

    private boolean visible = false;

    private SpriteSheet itemIcons;

    private int currentSelected = 0;

    private boolean moving = false;
    private int currentMoved = 0;
    private boolean movingInEquipment = false;

    private Map<String, ItemType> itemTypes;

    private Map<Integer, Item> items;

    private Map<Integer, Item> equipmentItems;

    private List<String> equipmentSlotNameList;

    private boolean inEquipment = false;

    private boolean trading = false;


    private int itemsPerRow = 5;
    private int numOfSlots = 20;
    private int space = 10;
    private int width = 40;
    private int height = 40;
    private int numberOfEquipmentSlots = 5;

    private int gold = 0;

    public InventoryWindow() throws SlickException {

        slotList = new LinkedList<>();
        equipmentSlotList = new LinkedList<>();

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

        for (int i = 0; i < numOfSlots; i++) {
            int col = i % itemsPerRow;
            int row = i / itemsPerRow;
            Rectangle slot = new Rectangle(background.getX() + space + (space + width) * col,background.getY() + space + (space + height) * row, width, height);
            slotList.add(slot);
        }

        for (int i = 0; i < numberOfEquipmentSlots; i++) {
            int col = itemsPerRow + 2;
            Rectangle slot = new Rectangle(background.getX() + space + (space + width) * col,background.getY() + space + (space + height) * i, width, height);
            equipmentSlotList.add(slot);
        }



        items = new TreeMap<>();

//        items.put(0, new Item(itemTypes.get("item type 1")));
//        items.put(5, new Item(itemTypes.get("item type 1")));
//        items.put(17, new Item(itemTypes.get("item type 3")));
//        items.put(26, new Item(itemTypes.get("item type 2")));

        equipmentItems = new TreeMap<>();

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

            renderItemDescription(g);
        }

    }

    public void renderEquipment(Graphics g) {
        for (int i = 0; i < numberOfEquipmentSlots; i++) {
            g.setColor(Color.black);

            if (moving && currentMoved == i && movingInEquipment) {
                g.setColor(Color.orange);
            }
            else if (inEquipment) {
                if (currentSelected == i){
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

    public void renderItemDescription(Graphics g) {
        g.setColor(Color.white);
        if (items.get(currentSelected) != null) g.drawString(items.get(currentSelected).getName(), background.getX() + space, background.getY() + (space + height) * numOfSlots/(float)itemsPerRow + space);
        if (items.get(currentSelected) != null) g.drawString(items.get(currentSelected).getDescription(), background.getX() + space, background.getY() + (space + height) * numOfSlots/(float)itemsPerRow + space + 25);
    }

    public void renderInventory(Graphics g) {
        for (int i = 0; i < numOfSlots; i++) {

            g.setColor(Color.black);

            if (moving && currentMoved == i && !movingInEquipment) {
                g.setColor(Color.orange);
            }
            else if (!inEquipment) {

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
        g.drawString("Gold: " + gold, background.getX() + 5, background.getY() + space + (space + height) * (float)numOfSlots/itemsPerRow + 90f);


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
                if (!inEquipment) {
                    if (currentSelected >= itemsPerRow) {
                        currentSelected = currentSelected - itemsPerRow;
                    }
                }
                else {
                    if (currentSelected > 0) {
                        currentSelected--;
                    }
                }
            }
            if (keyInput.isKeyPressed(KeyInput.Key.A)) {
                if (!inEquipment) {
                    if (currentSelected >= 1) currentSelected--;
                }
                else {
                    if (currentSelected * itemsPerRow + (itemsPerRow - 1) < numOfSlots) {
                        inEquipment = false;
                        currentSelected = currentSelected * itemsPerRow + (itemsPerRow - 1);
                    }
                }
            }
            if (keyInput.isKeyPressed(KeyInput.Key.S)) {
                if (!inEquipment) {
                    if (currentSelected <= numOfSlots-itemsPerRow-1) currentSelected = currentSelected + itemsPerRow;
                }
                else {
                    if (currentSelected < numberOfEquipmentSlots - 1) {
                        currentSelected++;
                    }
                }
            }
            if (keyInput.isKeyPressed(KeyInput.Key.D)) {
                if (!inEquipment) {
                    if ((currentSelected + 1) % itemsPerRow == 0) {
                        inEquipment = true;
                        currentSelected = currentSelected / itemsPerRow;
                    }
                    else if (currentSelected < numOfSlots-1) currentSelected++;

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
                                equipmentItems.put(currentMoved, to);
                                equipmentItems.put(currentSelected, from);
                                moving = false;
                            }
                            else {
                                Item from = equipmentItems.get(currentMoved);
                                Item to = items.get(currentSelected);
                                equipmentItems.put(currentMoved, to);
                                items.put(currentSelected, from);
                                moving = false;
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
        System.out.println(item == null);

        System.out.println("zzz: " + items.size());
        for (int i = 0; i < numOfSlots; i++) {
            if (items.get(i) == null) {
                items.put(i, item);

                if (item.getLootPileBackref().itemList.size() == 1) {
                    item.getLootPileBackref().setVisible(false);
                }
                item.removeFromLoot();
                itemList.remove(item);


                System.out.println("putting item " + item.getName() + " into slot " + i);
                return true;
            }
            else {
                System.out.println("item " + i + " is " + items.get(i).getName());
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
