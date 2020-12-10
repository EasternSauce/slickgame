package com.kamilkurp.gui;

import com.kamilkurp.KeyInput;
import com.kamilkurp.systems.GameState;
import com.kamilkurp.systems.GameSystem;
import org.newdawn.slick.Graphics;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class MainMenu {

    private int currentSelected = 0;

    private GameSystem gameSystem;

    private List<String> optionList;

    private boolean startMenu;

    public MainMenu(GameSystem gameSystem) {
        this.gameSystem = gameSystem;

        optionList = new LinkedList<>();

        startMenu = true;

        String contents = null;
        File f = new File("saves/savegame.sav");
        if(f.exists() && !f.isDirectory()) {
            try {
                contents = new String(Files.readAllBytes(Paths.get("saves/savegame.sav")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (contents != null && !contents.trim().isEmpty()) {
            optionList.add("Continue");
        }

        optionList.add("New game");
        optionList.add("Exit");
    }

    public void render(Graphics g) {

        for (int i = 0; i < Math.min(4, optionList.size()); i++) {
            g.drawString((currentSelected == i ? ">" : "") + optionList.get(i), 100, 100 + 30 * i);
        }

    }

    public void update(KeyInput keyInput) {

        if (keyInput.isKeyPressed(KeyInput.Key.E)) {
            if (optionList.get(currentSelected).equals("Continue")) {
                gameSystem.setState(GameState.GAMEPLAY);

                if (startMenu) {
                    startMenu = false;

                    optionList = new LinkedList<>();

                    optionList.add("Continue");
                    optionList.add("New game");
                    optionList.add("Save game");
                    optionList.add("Exit");

                    gameSystem.loadGame();

                }


            }
            else if (optionList.get(currentSelected).equals("New game")) {
                gameSystem.setState(GameState.GAMEPLAY);

                if (startMenu) {
                    startMenu = false;

                    optionList = new LinkedList<>();

                    optionList.add("Continue");
                    optionList.add("New game");
                    optionList.add("Save game");
                    optionList.add("Exit");
                }

                try {
                    BufferedWriter writer = Files.newBufferedWriter(Paths.get("saves/savegame.sav"));
                    writer.write("");
                    writer.flush();
                    writer = Files.newBufferedWriter(Paths.get("saves/inventory.sav"));
                    writer.write("");
                    writer.flush();
                    writer = Files.newBufferedWriter(Paths.get("saves/respawn_points.sav"));
                    writer.write("");
                    writer.flush();
                    writer = Files.newBufferedWriter(Paths.get("saves/treasure_collected.sav"));
                    writer.write("");
                    writer.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                gameSystem.loadGame();

            }
            else if (optionList.get(currentSelected).equals("Save game")) {
                gameSystem.saveGame();

            }
            else if (optionList.get(currentSelected).equals("Exit")) {
                System.exit(0);
            }
        }
        if (keyInput.isKeyPressed(KeyInput.Key.W)) {
            if (currentSelected > 0) {
                currentSelected--;
            }
        }
        if (keyInput.isKeyPressed(KeyInput.Key.S)) {
            if (currentSelected < optionList.size() - 1) {
                currentSelected++;
            }
        }

        if (keyInput.isKeyPressed(KeyInput.Key.ESC)) {
            if (!gameSystem.isEscRecently()) {
                if (!gameSystem.getInventoryWindow().isInventoryOpen() && !gameSystem.getLootOptionWindow().isActivated()) {

                    if (!startMenu) {
                        gameSystem.setState(GameState.GAMEPLAY);
                    }

                    gameSystem.setEscRecently(true);
                }
            }
        }

    }

}
