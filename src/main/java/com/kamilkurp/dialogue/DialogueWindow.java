package com.kamilkurp.dialogue;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DialogueWindow {
    List<Dialogue> dialogueText;

    private boolean activated;
    private boolean recentlyDeactivated;

    int currentDialogue = -1;

    List<Dialogue> currentDialogueChoices = null;

    private int currentSelected = 0;

    Timer dialogueTimer = new Timer();

    public DialogueWindow(String filename) {
        dialogueText = new ArrayList<>();

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);

                String[] split = line.split(";");

                Dialogue.Action action = null;
                int actionArgument = -1;

                if (split.length > 1) {
                    if (split[1].startsWith("g")) {
                        action = Dialogue.Action.GOTO;
                        actionArgument = Integer.parseInt(split[1].substring(1));
                    } else if (split[1].startsWith("t")) {
                        action = Dialogue.Action.TRADE;
                    } else if (split[1].startsWith("c")) {
                        action = Dialogue.Action.CHOICE;
                        actionArgument = Integer.parseInt(split[1].substring(1));

                    }
                }


                Dialogue dialogue = new Dialogue(split[0].startsWith(">") ? split[0].substring(1) : split[0], action, actionArgument);

                dialogueText.add(dialogue);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        activated = false;
        recentlyDeactivated = false;

    }

    public void render(Graphics g) {
        g.setColor(Color.white);


        if (activated) {
            g.drawString(dialogueText.get(currentDialogue).getText(), 10, Globals.SCREEN_HEIGHT * Globals.SCREEN_PROPORTION + 10);
            if (currentDialogueChoices != null) {
                for (int i = 0; i < currentDialogueChoices.size(); i++) {
                    String text = currentDialogueChoices.get(i).getText();
                    g.drawString(((currentSelected == i) ? ">" : "") + text, 10, Globals.SCREEN_HEIGHT * Globals.SCREEN_PROPORTION + 10 + 30 * (i+1));
                }
            }

        }
    }

    public void update(KeyInput keyInput) {
        recentlyDeactivated = false;

        if (keyInput.isKeyPressed(KeyInput.Key.ESC)) {
            if (activated) {
                activated = false;
                recentlyDeactivated = true;
            }

//            System.out.println("toggle activated, now activated=" + activated);

        }

        if (keyInput.isKeyPressed(KeyInput.Key.E)) {
            if (activated) {

            }

        }

        if (currentDialogueChoices != null) {
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


//        if (dialogueTimer.getTime() > 2000) {
//            currentDialogue = -1;
//        }

    }

    public void showDialogue(int dialogueId) {
//        System.out.println("trying to show dialugue, activated before = " + activated);
        System.out.println("choose dialogue. curr_dialogue=" + dialogueId);
        currentDialogue = dialogueId;
        
        if (!recentlyDeactivated) {
            activated = true;

            System.out.println("pressed E");
            Dialogue dialogue = dialogueText.get(currentDialogue);

            if (dialogue.getAction() == Dialogue.Action.CHOICE) {
                System.out.println("setting choices");
                currentDialogueChoices = new LinkedList<>();
                for (int i = currentDialogue + 1; i < currentDialogue + 1 + dialogue.getActionArgument(); i++) {
                    currentDialogueChoices.add(dialogueText.get(i));
                }
                System.out.println("setting " + currentDialogueChoices.size() + " dialogue choices");
            }
            else {
                currentDialogueChoices = null;
                System.out.println("choices is null");
            }
        }


            //dialogueTimer.reset();
    }

    public boolean isActivated() {
        return activated;
    }

}
