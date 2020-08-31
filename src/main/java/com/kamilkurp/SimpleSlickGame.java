package com.kamilkurp;

import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.Character;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.NPC;
import com.kamilkurp.dialogue.DialogueWindow;
import com.kamilkurp.gui.Hud;
import com.kamilkurp.gui.LootOptionWindow;
import com.kamilkurp.items.InventoryWindow;
import com.kamilkurp.items.Item;
import com.kamilkurp.items.ItemType;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.projectile.Arrow;
import com.kamilkurp.spawn.PlayerRespawnPoint;
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

    private Area currentArea;

    private Camera camera;

    private Hud hud;

    private Character character;

    private InventoryWindow inventoryWindow;

    private DialogueWindow dialogueWindow;

    private LootSystem lootSystem;

    private LootOptionWindow lootOptionWindow;

    private KeyInput keyInput;

//    private EnemyRespawnArea enemyRespawnArea1;
//    private EnemyRespawnArea enemyRespawnArea2;
//
//    private EnemySpawnPoint enemySpawnPoint;

    private PlayerRespawnPoint playerRespawnPoint;

    private Music townMusic;

    private Queue<Creature> renderPriorityQueue;

    private List<Arrow> arrowList;

    private Map<String, Area> areaMap;

    public SimpleSlickGame(String gamename) {
        super(gamename);
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        gc.getGraphics().setAntiAlias(true);

        Assets.loadAssets();



        inventoryWindow = new InventoryWindow();

        lootOptionWindow = new LootOptionWindow(inventoryWindow);

        dialogueWindow = new DialogueWindow(Globals.getAssetsLocation() + "dialogues/dialogues.txt", inventoryWindow);


        lootSystem = new LootSystem(lootOptionWindow);

        playerRespawnPoint = new PlayerRespawnPoint(400, 400);

        areaMap = new HashMap<>();
        areaMap.put("area1", new Area("area1", Assets.grassyTileset, Assets.area1Layout, Assets.area1Enemies, lootSystem));
        areaMap.put("area2", new Area("area2", Assets.dungeonTileset, Assets.area2Layout, Assets.area2Enemies, lootSystem));


        character = new Character("protagonist", 400, 400, areaMap.get("area1"), lootSystem, playerRespawnPoint);
        NPC npc = new NPC("johnny", 600, 600, areaMap.get("area1"), lootSystem, dialogueWindow, "a1", true);

        inventoryWindow.setCharacter(character);

        currentArea = areaMap.get("area1");


        camera = new Camera();



        hud = new Hud();


//        enemyRespawnArea1 = new EnemyRespawnArea(1000, 1400, 1, creatures, creaturesList, lootSystem);
//        //spawnPoint2 = new SpawnPoint(1900, 1900, 1, creatures, lootSystem);
//
//        enemySpawnPoint = new EnemySpawnPoint(1600, 2000, creatures, creaturesList, lootSystem);


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

        for (Creature creature : currentArea.getCreaturesMap().values()) {
            if (creature instanceof Character) {
                if (!inventoryWindow.isInventoryOpen() && !lootOptionWindow.isActivated() && !dialogueWindow.isActivated()) {
                    creature.update(gc, i, currentArea.getTiles(), currentArea.getCreaturesList(), keyInput, arrowList);
                }
            }
            else {
                creature.update(gc, i, currentArea.getTiles(), currentArea.getCreaturesList(), keyInput, arrowList);
            }

        }

        camera.update(gc, character.getRect());


        inventoryWindow.update(keyInput);

        dialogueWindow.update(keyInput);


        lootSystem.update(keyInput, character);

        currentArea.updateSpawns();

//        enemyRespawnArea1.update();
        //spawnPoint2.update();


        playerRespawnPoint.update();

        renderPriorityQueue = new PriorityQueue<>((o1, o2) -> {
            if (o1.getHealthPoints() <= 0.0f) return -1;
            if (o2.getHealthPoints() <= 0.0f) return 1;
            if (o1.getRect().getY() == o2.getRect().getY()) return 0;
            return (o1.getRect().getY() - o2.getRect().getY() > 0.0f) ? 1 : -1;
        });

        renderPriorityQueue.addAll(currentArea.getCreaturesMap().values());


    }

    public void render(GameContainer gc, Graphics g) {
        currentArea.render(g, camera);

        currentArea.renderSpawns(g, camera);

        playerRespawnPoint.render(g, camera);


        //spawnPoint2.render(g, camera);



        if (renderPriorityQueue != null) {
            while(!renderPriorityQueue.isEmpty()) {
                Creature creature = renderPriorityQueue.poll();

                creature.render(g, camera);
            }

        }


        for (Creature creature : currentArea.getCreaturesMap().values()) {
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

            for (Creature creature : currentArea.getCreaturesMap().values()) {
                if (creature.getClass() != Character.class && creature.getClass() != NPC.class) continue;
                writer.write("creature " + creature.getId() + "\n");
                writer.write("pos " + creature.getRect().getX() + " " + creature.getRect().getY() + "\n");
                writer.write("area " + creature.getArea().getId() + "\n");
                writer.write("health " + creature.getHealthPoints() + "\n");

                Map<Integer, Item> equipmentItems = creature.getEquipmentItems();

                for (Map.Entry<Integer, Item> equipmentItem : equipmentItems.entrySet()) {
                    if (equipmentItem.getValue() != null) {
                        String damage = equipmentItem.getValue().getDamage() == null ? "0" : "" + equipmentItem.getValue().getDamage().intValue();

                        String armor = equipmentItem.getValue().getArmor() == null ? "0" : "" + equipmentItem.getValue().getArmor().intValue();
                        writer.write("equipment_item " + equipmentItem.getKey() + " " + equipmentItem.getValue().getItemType().getId() + " " + damage + " " + armor + "\n");
                    }
                }
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

                    Map<String,Creature> allCreatures = new HashMap<>();

                    for (Map.Entry<String, Area> areaEntry : areaMap.entrySet()) {
                        allCreatures.putAll(areaEntry.getValue().getCreaturesMap());
                    }

                    creature = allCreatures.get(s[1]);

//                    if (creature == null) {
//                        throw new RuntimeException("creature not found!");
//                    }
                }
                if(s[0].equals("pos")) {
                    if (creature != null) {
                        creature.getRect().setX(Float.parseFloat(s[1]));
                        creature.getRect().setY(Float.parseFloat(s[2]));
                    }

                }
                if(s[0].equals("area")) {
                    if (creature != null) {
                        creature.setArea(areaMap.get(s[1]));
                    }

                }
                if(s[0].equals("health")) {
                    if (creature != null) {
                        creature.setHealthPoints(Float.parseFloat(s[1]));
                    }

                }
                if(s[0].equals("equipment_item")) {
                    if (creature != null) {
                        Map<Integer, Item> equipmentItems = creature.getEquipmentItems();
                        equipmentItems.put(Integer.parseInt(s[1]), new Item(ItemType.getItemType(s[2]), null, (s[3].equals("0") ? null : (float)(Integer.parseInt(s[3]))), (s[4].equals("0") ? null : (float)(Integer.parseInt(s[4])))));
                    }

                }
                line = reader.readLine();

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Creature creature1 : currentArea.getCreaturesList()) {
            creature1.updateAttackType();
        }
    }

}