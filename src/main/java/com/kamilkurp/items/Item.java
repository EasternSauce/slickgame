package com.kamilkurp.items;

import com.kamilkurp.Globals;

public class Item {
    private final ItemType itemType;
    private LootPile lootPileBackref;

    private Float damage;
    private Float armor;

    public Item(ItemType itemType, LootPile lootPileBackref) {
        this.itemType = itemType;
        this.lootPileBackref = lootPileBackref;

        if (itemType.getMaxDamage() != null) this.damage = (float)Math.ceil(itemType.getMaxDamage() * (0.5f + 0.5f * Globals.randFloat()));
        if (itemType.getMaxArmor() != null) this.armor = (float)Math.ceil(itemType.getMaxArmor() * (0.5f + 0.5f * Globals.randFloat()));

    }

    public Item(ItemType itemType, LootPile lootPileBackref, Float damage, Float armor) {
        this.itemType = itemType;
        this.lootPileBackref = lootPileBackref;

        this.damage = damage;
        this.armor = armor;


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

    public String getItemInformation(boolean trader) {
        if (trader) {
            return (this.damage != null ? "Damage: " + damage.intValue() + "\n" : "")
                    + (this.armor != null ? "Armor: " + armor.intValue() + "\n" : "")
                    + this.getDescription() + "\n"
                    + "Worth " + (int)(this.getItemType().getWorth()) + " Gold" + "\n";
        } else {
            return (this.damage != null ? "Damage: " + damage.intValue() + "\n" : "")
                    + (this.armor != null ? "Armor: " + armor.intValue() + "\n" : "")
                    + this.getDescription() + "\n"
                    + "Worth " + (int)(this.getItemType().getWorth() * 0.3) + " Gold" + "\n";
        }

    }

    public Float getDamage() {
        return damage;
    }

    public Float getArmor() {
        return armor;
    }

}
