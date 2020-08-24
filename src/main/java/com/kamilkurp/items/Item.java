package com.kamilkurp.items;

public class Item {
    private ItemType itemType;
    private LootPile lootPileBackref;

    public Item(ItemType itemType, LootPile lootPileBackref) {
        this.itemType = itemType;
        this.lootPileBackref = lootPileBackref;
    }

    public String getName() {
        if (itemType == null) return null;
        return itemType.getName();
    }

    public String getDescription() {
        if (itemType == null) return null;
        return itemType.getDescription();
    }

    public LootPile getLootPileBackref() {
        return lootPileBackref;
    }

    public void removeFromLoot() {
        lootPileBackref.getItemList().remove(this);

        lootPileBackref = null;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public String getItemInformation() {
        return (this.getItemType().getDamage() != null ? "Damage: " + this.getItemType().getDamage().intValue() + "\n" : "")
                + (this.getItemType().getArmor() != null ? "Armor: " + this.getItemType().getArmor().intValue() + "\n" : "")
                + this.getDescription() + "\n"
                + "Worth " + (int)(this.getItemType().getWorth() * 0.3) + " Gold" + "\n";

    }

}
