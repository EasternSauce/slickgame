package com.kamilkurp.items;

import org.newdawn.slick.Image;

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
}
