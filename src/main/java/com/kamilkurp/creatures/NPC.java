package com.kamilkurp.creatures;

import com.kamilkurp.KeyInput;
import com.kamilkurp.dialogue.DialogueWindow;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.utils.Timer;
import com.kamilkurp.terrain.TerrainTile;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class NPC extends Creature {
    private Timer actionTimer;

    public Random random = new Random();

    private DialogueWindow dialogueWindow;
    private int dialogueStart = 0;

    private Timer dialogueResetTimer;

    public NPC(String id, int posX, int posY, Map<String, Creature> creatures, LootSystem lootSystem, DialogueWindow dialogueWindow, int dialogueStart) throws SlickException {
        super(id, posX, posY, creatures, lootSystem);

        this.dialogueWindow = dialogueWindow;
        this.dialogueStart = dialogueStart;

        actionTimer = new Timer();
        dialogueResetTimer = new Timer();
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

        if (dialogueResetTimer.getTime() > 6000) {
            dialogueStart = 0;
        }
    }

    @Override
    protected void onDeath() {

    }

    public void triggerDialogue() {
        if (!dialogueWindow.isActivated()) {
            dialogueWindow.showDialogue(dialogueStart);

            dialogueResetTimer.reset();
        }


    }
}
