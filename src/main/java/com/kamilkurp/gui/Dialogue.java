package com.kamilkurp.gui;

import com.kamilkurp.Globals;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Dialogue {
    List<String> dialogues;

    int currentDialogue = -1;

    Timer dialogueTimer = new Timer();

    public Dialogue(String filename) {
        dialogues = new ArrayList<>();

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                dialogues.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void render(Graphics g) {
        g.setColor(Color.white);


        if (currentDialogue != -1) {

            g.drawString(dialogues.get(currentDialogue), 10, Globals.SCREEN_HEIGHT * Globals.SCREEN_PROPORTION + 10);
        }
    }

    public void update(int i) {
        dialogueTimer.update(i);

        if (dialogueTimer.getTime() > 2000) {
            currentDialogue = -1;
        }

    }

    public void showDialogue(int dialogue) {
        currentDialogue = dialogue;
        dialogueTimer.reset();
    }
}
