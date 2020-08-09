package com.kamilkurp;

import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.Character;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.NPC;
import com.kamilkurp.dialogue.DialogueWindow;
import com.kamilkurp.gui.Hud;
import com.kamilkurp.gui.LootOptionWindow;
import com.kamilkurp.items.InventoryWindow;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleSlickGame extends BasicGame {

    private Area area1;
    private Area area2;

    private Area currentArea;

    private Camera camera;

    private Hud hud;

    private Map<String, Creature> creatures;

    private Character character;

    private InventoryWindow inventoryWindow;

    private DialogueWindow dialogueWindow;

    private LootSystem lootSystem;

    private LootOptionWindow lootOptionWindow;

    private KeyInput keyInput;

    private SpawnPoint spawnPoint1;
    private SpawnPoint spawnPoint2;

    private Music townMusic;

    public SimpleSlickGame(String gamename) {
        super(gamename);
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        gc.getGraphics().setAntiAlias(true);

        Assets.loadAssets();

        creatures = new TreeMap<>();

        inventoryWindow = new InventoryWindow();

        lootOptionWindow = new LootOptionWindow(inventoryWindow);

        dialogueWindow = new DialogueWindow(Globals.getAssetsLocation() + "dialogues/dialogues.txt", inventoryWindow);


        lootSystem = new LootSystem(lootOptionWindow);


        character = new Character("protagonist", 400, 400, creatures, lootSystem);
        NPC npc = new NPC("johnny", 600, 600, creatures, lootSystem, dialogueWindow, "a1", true);


        area1 = new Area(Assets.grassyTileset, Assets.area1Layout);

        area2 = new Area(Assets.dungeonTileset, Assets.area2Layout);


        currentArea = area1;


        camera = new Camera();



        hud = new Hud();


        spawnPoint1 = new SpawnPoint(1000, 1400, 3, creatures, lootSystem);
        spawnPoint2 = new SpawnPoint(1900, 1900, 1, creatures, lootSystem);


        keyInput = new KeyInput();

        loadGame();

        townMusic = Assets.townMusic;

//        townMusic.loop(1.0f, 0.5f);
    }

    @Override
    public void update(GameContainer gc, int i) throws SlickException {
        Globals.updateTimers(i);

        keyInput.readKeyPresses(gc.getInput());

        int enemyAlive = 0;


        for (Creature creature : creatures.values()) {
            if (creature instanceof Character) {
                if (!inventoryWindow.isInventoryOpen() && !lootOptionWindow.isActivated() && !dialogueWindow.isActivated()) {
                    creature.update(gc, i, currentArea.getTiles(), creatures.values(), keyInput);
                }
            }
            else {
                creature.update(gc, i, currentArea.getTiles(), creatures.values(), keyInput);
            }

        }

        camera.update(gc, character.getRect());


        inventoryWindow.update(keyInput);

        dialogueWindow.update(keyInput);


        lootSystem.update(keyInput, character);

        spawnPoint1.update();
        spawnPoint2.update();
    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        currentArea.render(g, camera);

        spawnPoint1.render(g, camera);
        spawnPoint2.render(g, camera);

        for (Creature creature : creatures.values()) {
            creature.render(g, camera);
        }

        lootSystem.render(g, camera);
        inventoryWindow.render(g, camera);

        hud.render(g);

        dialogueWindow.render(g);

        lootOptionWindow.render(g, camera);

    }

    public static void main(String[] args) {
        try {
            AppGameContainer appgc;
            appgc = new AppGameContainer(new SimpleSlickGame("Simple Slick Game"));
            appgc.setDisplayMode(Globals.SCREEN_WIDTH, Globals.SCREEN_HEIGHT, false);
            appgc.setVSync(true);

            appgc.start();
        }
        catch (SlickException ex) {
            Logger.getLogger(SimpleSlickGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean closeRequested() {

        saveGame();

        System.exit(0);
        return true;
    }

    private void saveGame() {
        try {
            FileWriter writer = new FileWriter("saves/savegame.sav");

            for (Creature creature : creatures.values()) {
                writer.write("creature " + creature.getId() + "\n");
                writer.write("pos " + creature.getRect().getX() + " " + creature.getRect().getY() + "\n");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void loadGame() {
        Creature creature = null;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("saves/savegame.sav"));
            String line = reader.readLine();
            while (line != null) {
                String[] s = line.split(" ");
                if(s[0].equals("creature")) {
                    creature = creatures.get(s[1]);
                }
                if(s[0].equals("pos")) {
                    if (creature != null) {
                        creature.getRect().setX(Float.parseFloat(s[1]));
                        creature.getRect().setY(Float.parseFloat(s[2]));
                    }

                }
                line = reader.readLine();

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}