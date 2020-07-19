package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.dialogue.DialogueWindow;
import com.kamilkurp.items.Item;
import com.kamilkurp.items.ItemType;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.utils.Timer;
import com.kamilkurp.terrain.TerrainTile;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

import java.util.*;

public class NPC extends Creature {
    private Timer actionTimer;

    public Random random = new Random();

    private DialogueWindow dialogueWindow;
    private String dialogueStartId = null;

    private List<Item> traderInventory;

    private boolean trader = false;

    private Map<String, Float> dropTable;


    public NPC(String id, int posX, int posY, Map<String, Creature> creatures, LootSystem lootSystem, DialogueWindow dialogueWindow, String dialogueStartId, boolean trader) throws SlickException {
        super(id, posX, posY, creatures, lootSystem);

        this.dialogueWindow = dialogueWindow;
        this.dialogueStartId = dialogueStartId;

        actionTimer = new Timer();

        this.trader = trader;

        traderInventory = new LinkedList<>();

        dropTable = new HashMap<>();
        dropTable.put("ringmailGreaves", 0.9f);
        dropTable.put("skinTunic", 0.2f);
        dropTable.put("hideGloves", 0.1f);

        if (trader) {
            for (Map.Entry<String, Float> entry : dropTable.entrySet()) {
                for (int i = 0; i < 6; i++) {
                    if (Globals.random.nextFloat() < entry.getValue()) {
                        Item item = new Item(ItemType.getItemType(entry.getKey()), null);
                        traderInventory.add(item);
                    }
                }

            }
        }
    }

    @Override
    public void performActions(GameContainer gc, Collection<Creature> creatures, KeyInput keyInput) {

        if (actionTimer.getTime() > 4000) {
            direction = Math.abs(random.nextInt()%4);

            actionTimer.reset();
        }

    }

    @Override
    public void update(GameContainer gc, int i, List<TerrainTile> tiles, Collection<Creature> creatures, KeyInput keyInput) {
        super.update(gc, i, tiles, creatures, keyInput);

        // CHANGE THESE to only update once...

        if (runningTimer.getTime() > 200) {
            running = false;
        }

        if (attackingTimer.getTime() > 200) {
            attacking = false;
        }

        if (immunityTimer.getTime() > 500) {
            immune = false;
        }


    }

    @Override
    protected void onDeath() {

    }

    public void triggerDialogue() {
        if (!dialogueWindow.isActivated()) {
            dialogueWindow.setDialogueNPC(this);
            dialogueWindow.setTraderInventory(traderInventory);
        }


    }

    public String getDialogueStartId() {
        return dialogueStartId;
    }
}
