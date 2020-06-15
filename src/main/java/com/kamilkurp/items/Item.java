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
}
