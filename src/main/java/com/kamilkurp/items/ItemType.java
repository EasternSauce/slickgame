package com.kamilkurp.items;

import com.kamilkurp.assets.Assets;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import java.util.HashMap;
import java.util.Map;

public class ItemType {
    private final String id;
    private final String name;
    private final String description;
    private final Image image;
    private final String equipmentType;
    private final int worth;
    private final Float maxDamage;
    private final Float maxArmor;



    public ItemType(String id, String name, String description, Image image, String equipmentType, int worth, Float maxDamage, Float maxArmor) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.equipmentType = equipmentType;
        this.worth = worth;
        this.maxDamage = maxDamage;
        this.maxArmor = maxArmor;
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

        ItemType itemType1 = new ItemType("leatherArmor", "Leather Armor", "-", itemIcons.getSprite(8,7), "body", 50, null, 7f);
        ItemType itemType2 = new ItemType("ringmailGreaves", "Ringmail Greaves", "-", itemIcons.getSprite(3,8), "boots", 30, null, 3f);
        ItemType itemType3 = new ItemType("hideGloves", "Hide Gloves", "-", itemIcons.getSprite(0,8), "gloves", 25, null, 2f);
        ItemType itemType4 = new ItemType("crossbow", "Crossbow", "-", itemIcons.getSprite(4,6), "weapon", 250, 30f, null);
        ItemType itemType5 = new ItemType("ironSword", "Iron Sword", "-", itemIcons.getSprite(2, 5), "weapon", 200, 25f, null);
        ItemType itemType6 = new ItemType("woodenSword", "Wooden Sword", "-", itemIcons.getSprite(0, 5), "weapon", 200, 20f, null);
        ItemType itemType7 = new ItemType("leatherHelmet", "Leather Helmet", "-", itemIcons.getSprite(2, 7), "helmet", 70, null, 5f);
        ItemType itemType8 = new ItemType("lifeRing", "Life Ring", "Increases life when worn", itemIcons.getSprite(5, 8), "ring", 300, null, null);


        itemTypes.put(itemType1.getId(), itemType1);
        itemTypes.put(itemType2.getId(), itemType2);
        itemTypes.put(itemType3.getId(), itemType3);
        itemTypes.put(itemType4.getId(), itemType4);
        itemTypes.put(itemType5.getId(), itemType5);
        itemTypes.put(itemType6.getId(), itemType6);
        itemTypes.put(itemType7.getId(), itemType7);
        itemTypes.put(itemType8.getId(), itemType8);
    }

    public static ItemType getItemType(String itemTypeId) {
        ItemType itemType = itemTypes.get(itemTypeId);
        if (itemType == null) throw new RuntimeException("item type doesn't exist: " + itemTypeId);
        return itemType;
    }

    public Float getMaxDamage() {
        return maxDamage;
    }

    public Float getMaxArmor() {
        return maxArmor;
    }
}
