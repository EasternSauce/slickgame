package com.kamilkurp.items;

import org.newdawn.slick.Image;

public class ItemType {
    private String id;
    private String name;
    private String description;
    private Image image;
    private String equipmentType;

    public ItemType(String id, String name, String description, Image image, String equipmentType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.equipmentType = equipmentType;
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
}
