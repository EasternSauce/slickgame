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
import com.kamilkurp.projectile.Arrow;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleSlickGame extends BasicGame {

    private Area area1;
    private Area area2;

    private Area currentArea;

    private Camera camera;

    private Hud hud;

    private Map<String, Creature> creatures;
    private List<Creature> creaturesList;

    private Character character;

    private InventoryWindow inventoryWindow;

    private DialogueWindow dialogueWindow;

    private LootSystem lootSystem;

    private LootOptionWindow lootOptionWindow;

    private KeyInput keyInput;

    private SpawnPoint spawnPoint1;
    private SpawnPoint spawnPoint2;

    private Music townMusic;

    private Queue<Creature> renderPriorityQueue;

    private List<Arrow> arrowList;


    public SimpleSlickGame(String gamename) {
        super(gamename);
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        gc.getGraphics().setAntiAlias(true);

        Assets.loadAssets();

        creatures = new TreeMap<>();

        creaturesList = new LinkedList<>();

        inventoryWindow = new InventoryWindow();

        lootOptionWindow = new LootOptionWindow(inventoryWindow);

        dialogueWindow = new DialogueWindow(Globals.getAssetsLocation() + "dialogues/dialogues.txt", inventoryWindow);


        lootSystem = new LootSystem(lootOptionWindow);


        character = new Character("protagonist", 400, 400, creatures, creaturesList, lootSystem, inventoryWindow.getEquipmentItems());
        NPC npc = new NPC("johnny", 600, 600, creatures, creaturesList, lootSystem, dialogueWindow, "a1", true);

        inventoryWindow.setCharacter(character);



        area1 = new Area(Assets.grassyTileset, Assets.area1Layout);

        area2 = new Area(Assets.dungeonTileset, Assets.area2Layout);


        currentArea = area1;


        camera = new Camera();



        hud = new Hud();


        spawnPoint1 = new SpawnPoint(1000, 1400, 1, creatures, creaturesList, lootSystem);
        //spawnPoint2 = new SpawnPoint(1900, 1900, 1, creatures, lootSystem);


        keyInput = new KeyInput();

        loadGame();

        townMusic = Assets.townMusic;

        arrowList = new LinkedList<>();

//        townMusic.loop(1.0f, 0.5f);
    }

    @Override
    public void update(GameContainer gc, int i) throws SlickException {
        Globals.updateTimers(i);

        keyInput.readKeyPresses(gc.getInput());

        for (Creature creature : creatures.values()) {
            if (creature instanceof Character) {
                if (!inventoryWindow.isInventoryOpen() && !lootOptionWindow.isActivated() && !dialogueWindow.isActivated()) {
                    creature.update(gc, i, currentArea.getTiles(), creaturesList, keyInput, arrowList);
                }
            }
            else {
                creature.update(gc, i, currentArea.getTiles(), creaturesList, keyInput, arrowList);
            }

        }

        camera.update(gc, character.getRect());


        inventoryWindow.update(keyInput);

        dialogueWindow.update(keyInput);


        lootSystem.update(keyInput, character);

        spawnPoint1.update();
        //spawnPoint2.update();

        renderPriorityQueue = new PriorityQueue<>((o1, o2) -> {
            if (o1.getHealthPoints() <= 0.0f) return -1;
            if (o2.getHealthPoints() <= 0.0f) return 1;
            if (o1.getRect().getY() == o2.getRect().getY()) return 0;
            return (o1.getRect().getY() - o2.getRect().getY() > 0.0f) ? 1 : -1;
        });

        renderPriorityQueue.addAll(creatures.values());


    }

    public void render(GameContainer gc, Graphics g) {
        currentArea.render(g, camera);

        spawnPoint1.render(g, camera);
        //spawnPoint2.render(g, camera);


        if (renderPriorityQueue != null) {
            while(!renderPriorityQueue.isEmpty()) {
                Creature creature = renderPriorityQueue.poll();

                creature.render(g, camera);
            }

        }


        for (Creature creature : creatures.values()) {
            creature.renderAttackAnimation(g, camera);
        }

        for (Arrow arrow : arrowList) {
            arrow.render(g, camera);
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