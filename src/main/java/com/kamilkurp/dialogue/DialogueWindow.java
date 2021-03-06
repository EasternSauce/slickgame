package com.kamilkurp.dialogue;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.creatures.NonPlayerCharacter;
import com.kamilkurp.items.Item;
import com.kamilkurp.systems.GameSystem;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DialogueWindow {
    List<Dialogue> dialogueList;

    private boolean activated;

    Dialogue currentDialogue = null;

    private NonPlayerCharacter dialogueNonPlayerCharacter;

    List<Dialogue> currentDialogueChoices = null;

    private int currentSelected = 0;

    private final GameSystem gameSystem;

    public DialogueWindow(GameSystem gameSystem, String filename) {
        this.gameSystem = gameSystem;

        dialogueList = new ArrayList<>();

        loadDialogueFromFile(filename);

        activated = false;

    }

    public void loadDialogueFromFile(String filename) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            while (line != null) {

                String[] split = line.split(";");

                Dialogue.Action action = null;
                String actionArgument = null;

                String id = split[0];

                String text = split[1];

                String actionCode;

                if (split.length > 2) {
                    actionCode = split[2];

                    if (actionCode.startsWith("g")) {
                        action = Dialogue.Action.GOTO;
                        actionArgument = actionCode.substring(1);
                    } else if (actionCode.startsWith("t")) {
                        action = Dialogue.Action.TRADE;
                    } else if (actionCode.startsWith("c")) {
                        action = Dialogue.Action.CHOICE;
                        actionArgument = actionCode.substring(1);
                    } else if (actionCode.startsWith("e")) {
                        action = Dialogue.Action.GOODBYE;
                    }
                }




                Dialogue dialogue = new Dialogue(id, text.startsWith(">") ? text.substring(1) : text, action, actionArgument);

                dialogueList.add(dialogue);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void render(Graphics g) {
        g.setColor(Color.white);


        if (activated) {
            g.drawString(currentDialogue.getText(), 10, Globals.SCREEN_HEIGHT * Globals.SCREEN_PROPORTION + 10);
            if (currentDialogueChoices != null) {
                for (int i = 0; i < currentDialogueChoices.size(); i++) {
                    String text = currentDialogueChoices.get(i).getText();
                    g.drawString(((currentSelected == i) ? ">" : "") + text, 10, Globals.SCREEN_HEIGHT * Globals.SCREEN_PROPORTION + 10 + 30 * (i+1));
                }
            }

        }
    }

    public void update(KeyInput keyInput) {


        if (keyInput.isKeyPressed(KeyInput.Key.E)) {

            if (activated) {
                if (currentDialogueChoices != null) {

                    Dialogue dialogue = currentDialogueChoices.get(currentSelected);
                    if (dialogue.getAction() == Dialogue.Action.GOTO) {
                        currentDialogue = findDialogueById(dialogue.getActionArgument());

                        setDialogueChoices();
                    }
                    else if (dialogue.getAction() == Dialogue.Action.GOODBYE) {
                        activated = false;
                    }
                    else if (dialogue.getAction() == Dialogue.Action.TRADE) {
                        gameSystem.getInventoryWindow().openTradeWindow();
                    }
                } else {
                    if (currentDialogue.getAction() == Dialogue.Action.GOTO) {
                        currentDialogue = findDialogueById(currentDialogue.getActionArgument());

                        setDialogueChoices();
                    }
                }
            }
            
            if (dialogueNonPlayerCharacter != null) {
                if (!activated) {

                    activated = true;

                    currentDialogue = findDialogueById(dialogueNonPlayerCharacter.getDialogueStartId());


                    setDialogueChoices();
                }
            }



        }

        if (currentDialogueChoices != null && !gameSystem.getInventoryWindow().isTrading()) {
            if (keyInput.isKeyPressed(KeyInput.Key.W)) {
                if (currentSelected > 0) {
                    currentSelected--;
                }
            }
            if (keyInput.isKeyPressed(KeyInput.Key.S)) {
                if (currentSelected < currentDialogueChoices.size() - 1) {
                    currentSelected++;
                }
            }
        }

        dialogueNonPlayerCharacter = null;
    }

    private Dialogue findDialogueById(String dialogueId) {
        for (Dialogue dialogue : dialogueList) {
            if (dialogue.getId().equals(dialogueId)) {
                return dialogue;
            }
        }
        return null;
    }

    public void setDialogueChoices() {
        currentSelected = 0;

        if (currentDialogue.getAction() == Dialogue.Action.CHOICE) {
            currentDialogueChoices = new LinkedList<>();

            int dialogueIndex = dialogueList.indexOf(currentDialogue);

            for (int i = dialogueIndex + 1; i < dialogueIndex + 1 + Integer.parseInt(currentDialogue.getActionArgument()); i++) {
                currentDialogueChoices.add(dialogueList.get(i));
            }
        }
        else {
            currentDialogueChoices = null;
        }
    }

    public void setDialogueNonPlayerCharacter(NonPlayerCharacter nonPlayerCharacter) {
        dialogueNonPlayerCharacter = nonPlayerCharacter;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setTraderInventory(List<Item> traderInventory) {
        gameSystem.getInventoryWindow().setTraderInventory(traderInventory);
    }
}
