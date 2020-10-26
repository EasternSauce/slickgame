package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.animations.WalkAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.items.Item;
import com.kamilkurp.items.ItemType;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.GameContainer;

import java.util.*;

public class NonPlayerCharacter extends Creature {
    private final Timer actionTimer;

    public Random random = new Random();

    private String dialogueStartId;

    private final List<Item> traderInventory;

    private boolean trader = false;

    private final Map<String, Float> dropTable;


    public NonPlayerCharacter(GameSystem gameSystem, String id, String dialogueStartId, boolean trader) {
        super(gameSystem, id);

        this.dialogueStartId = dialogueStartId;

        actionTimer = new Timer();

        this.trader = trader;

        traderInventory = new LinkedList<>();

        walkAnimation = new WalkAnimation(Assets.male1SpriteSheet, 3, 100, new int [] {3,1,0,2}, 1);


        dropTable = new HashMap<>();
        dropTable.put("ringmailGreaves", 0.9f);
        dropTable.put("leatherArmor", 0.2f);
        dropTable.put("hideGloves", 0.1f);
        dropTable.put("crossbow", 0.35f);

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
    public void performActions(GameContainer gc, KeyInput keyInput) {

        if (actionTimer.getTime() > 4000) {
            direction = Math.abs(random.nextInt()%4);

            actionTimer.reset();
        }

    }

    @Override
    public String getCreatureType() {
        return "nonPlayerCharacter";
    }

    @Override
    public void onInit() {

    }

    @Override
    public void update(GameContainer gc, int i, KeyInput keyInput) {
        super.update(gc, i, keyInput);

        // CHANGE THESE to only update once...

        if (runningTimer.getTime() > 200) {
            running = false;
        }

//        if (attackingTimer.getTime() > 300) {
//            attacking = false;
//        }

        if (immunityTimer.getTime() > 500) {
            immune = false;
        }


    }

    @Override
    protected void setFacingDirection(GameContainer gc) {

    }

    @Override
    public void onDeath() {

    }

    @Override
    protected void performAbilityOnUpdateStart(int i) {

    }

    @Override
    public void performAbilityMovement() {

    }

    public void triggerDialogue() {
        if (!gameSystem.getDialogueWindow().isActivated()) {
            gameSystem.getDialogueWindow().setDialogueNonPlayerCharacter(this);
            gameSystem.getDialogueWindow().setTraderInventory(traderInventory);
        }


    }

    public String getDialogueStartId() {
        return dialogueStartId;
    }
}
