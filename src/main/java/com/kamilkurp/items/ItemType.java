package com.kamilkurp.items;

import com.kamilkurp.assets.Assets;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import java.util.HashMap;
import java.util.Map;

public class ItemType {
    private String id;
    private String name;
    private String description;
    private Image image;
    private String equipmentType;
    private int worth;



    public ItemType(String id, String name, String description, Image image, String equipmentType, int worth) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.equipmentType = equipmentType;
        this.worth = worth;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Image getImage() {
        return image;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public int getWorth() {
        return worth;
    }

    private static Map<String, ItemType> itemTypes;

    public static void loadItemTypes() {
        SpriteSheet itemIcons = Assets.niceItemIcons;

        itemTypes = new HashMap<>();

        ItemType itemType1 = new ItemType("skinTunic", "Leather Armor", "-", itemIcons.getSprite(8,7), "body", 50);
        ItemType itemType2 = new ItemType("ringmailGreaves", "Ringmail Greaves", "-", itemIcons.getSprite(3,8), "boots", 30);
        ItemType itemType3 = new ItemType("hideGloves", "Hide Gloves", "-", itemIcons.getSprite(0,8), "gloves", 25);

        itemTypes.put(itemType1.getId(), itemType1);
        itemTypes.put(itemType2.getId(), itemType2);
        itemTypes.put(itemType3.getId(), itemType3);
    }

    public static ItemType getItemType(String itemTypeId) {
        return itemTypes.get(itemTypeId);
    }
}
