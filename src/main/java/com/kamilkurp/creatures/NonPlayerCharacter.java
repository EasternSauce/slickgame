package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.animations.WalkAnimation;
import com.kamilkurp.abilities.Ability;
import com.kamilkurp.items.Item;
import com.kamilkurp.items.ItemType;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SpriteSheet;

import java.util.*;

public class NonPlayerCharacter extends Creature {
    private final Timer actionTimer;

    public Random random = Globals.random;

    private String dialogueStartId;

    private final List<Item> traderInventory;

    private boolean trader = false;

    private final Map<String, Float> dropTable;


    public NonPlayerCharacter(GameSystem gameSystem, String id, String dialogueStartId, boolean trader, SpriteSheet spriteSheet) {
        super(gameSystem, id);

        this.dialogueStartId = dialogueStartId;

        actionTimer = new Timer();

        this.trader = trader;

        traderInventory = new LinkedList<>();

        walkAnimation = new WalkAnimation(spriteSheet, 3, 100, new int [] {3,1,0,2}, 1);


        dropTable = new HashMap<>();
        dropTable.put("lifeRing", 0.05f);
        dropTable.put("poisonDagger", 0.08f);
        dropTable.put("healingPowder", 0.3f);
        dropTable.put("ironSword", 0.1f);
        dropTable.put("woodenSword", 0.1f);

        if (trader) {
            for (Map.Entry<String, Float> entry : dropTable.entrySet()) {
                for (int i = 0; i < 12; i++) {
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

        if (actionTimer.getElapsed() > 4000) {
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
        defineAbilities();

        updateAttackType();
    }

    @Override
    public void update(GameContainer gc, int i, KeyInput keyInput, GameSystem gameSystem) {
        super.update(gc, i, keyInput, gameSystem);

        // CHANGE THESE to only update once...

        if (runningTimer.getElapsed() > 200) {
            running = false;
        }

//        if (attackingTimer.getTime() > 300) {
//            attacking = false;
//        }

        if (immunityTimer.getElapsed() > 500) {
            immune = false;
        }


    }

    @Override
    protected void setFacingDirection(GameContainer gc) {

    }

    @Override
    public void onDeath() {
        for (Ability ability : abilityList) {
            ability.stopAbility();
        }

        currentAttack.stopAbility();
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
