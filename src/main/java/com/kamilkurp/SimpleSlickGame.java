package com.kamilkurp;

import com.kamilkurp.areagate.AreaGate;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.PlayerCharacter;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.NonPlayerCharacter;
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
import com.kamilkurp.terrain.CurrentAreaManager;
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

    private CurrentAreaManager currentAreaManager;

    private Camera camera;

    private Hud hud;

    private PlayerCharacter playerCharacter;

    private InventoryWindow inventoryWindow;

    private DialogueWindow dialogueWindow;

    private LootSystem lootSystem;

    private LootOptionWindow lootOptionWindow;

    private KeyInput keyInput;

    private Music townMusic;

    private Map<String, Area> areaMap;

    private List<AreaGate> gateList;

    private List<Creature> creaturesToMove;

    private Creature cameraFocusedCreature;


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

        currentAreaManager = new CurrentAreaManager();

        lootSystem = new LootSystem(lootOptionWindow, currentAreaManager);

        areaMap = new HashMap<>();
        areaMap.put("area1", new Area("area1", Assets.grassyTileset, Assets.area1Layout, Assets.area1Enemies, lootSystem));
        areaMap.put("area2", new Area("area2", Assets.dungeonTileset, Assets.area2Layout, Assets.area2Enemies, lootSystem));

        areaMap.get("area1").addRespawnPoint(new PlayerRespawnPoint(500, 500, areaMap.get("area1")));




        playerCharacter = new PlayerCharacter("protagonist", 400, 400, areaMap.get("area1"), lootSystem, currentAreaManager);

        cameraFocusedCreature = playerCharacter;

        NonPlayerCharacter nonPlayerCharacter = new NonPlayerCharacter("johnny", 600, 600, areaMap.get("area1"), lootSystem, dialogueWindow, "a1", true);


        inventoryWindow.setPlayerCharacter(playerCharacter);



        camera = new Camera();



        hud = new Hud();


        keyInput = new KeyInput();

        loadGame();



        townMusic = Assets.townMusic;


        gateList = new LinkedList<>();

        gateList.add(new AreaGate(areaMap.get("area1"), 1855, 2300, areaMap.get("area2"), 150, 150));

        creaturesToMove = new LinkedList<>();
//        townMusic.loop(1.0f, 0.5f);
    }

    @Override
    public void update(GameContainer gc, int i) throws SlickException {
        Globals.updateTimers(i);

        keyInput.readKeyPresses(gc.getInput());

        Area currentArea = currentAreaManager.getCurrentArea();

        creaturesToMove.clear();

        // for current area
        currentArea.getAreaCreaturesHolder().updateCreatures(inventoryWindow, lootOptionWindow, dialogueWindow, gc, i, keyInput, gateList);

        // for all areas
        for (Area area : areaMap.values()) {
            area.getAreaCreaturesHolder().processAreaChanges(creaturesToMove);
        }

        for (Creature creature : creaturesToMove) {
            creature.transportLogic();
        }



        camera.update(gc, cameraFocusedCreature.getRect());


        inventoryWindow.update(keyInput);

        dialogueWindow.update(keyInput);


        lootSystem.update(keyInput, playerCharacter);


        for (AreaGate areaGate : gateList) {
            areaGate.update(areaMap, currentAreaManager);
        }

        currentArea.getAreaCreaturesHolder().updateRenderPriorityQueue();
    }

    public void render(GameContainer gc, Graphics g) {
        Area currentArea = currentAreaManager.getCurrentArea();

        if (cameraFocusedCreature.getArea() == currentArea) {

            currentArea.render(g, camera);

            currentArea.renderSpawns(g, camera);

            for (PlayerRespawnPoint playerRespawnPoint : currentArea.getRespawnList()) {
                playerRespawnPoint.render(g, camera);
            }

            for (AreaGate areaGate : gateList) {
                areaGate.render(g, camera, currentArea);
            }

            currentArea.getAreaCreaturesHolder().renderCreatures(g, camera);

            for (Arrow arrow : currentArea.getArrowList()) {
                arrow.render(g, camera);
            }


            lootSystem.render(g, camera);
            inventoryWindow.render(g, camera);

            if (playerCharacter.isRespawning()) {
                Assets.verdanaHugeTtf.drawString(175, 175, "YOU DIED", Color.red);
            }
        }

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

            for (Area area : areaMap.values()) {
                area.getAreaCreaturesHolder().saveToFile(writer);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileWriter writer = new FileWriter("saves/inventory.sav");

            for (Map.Entry<Integer, Item> inventoryItem : inventoryWindow.getInventoryItems().entrySet()) {
                if (inventoryItem.getValue() != null) {
                    int slotId = inventoryItem.getKey();
                    String damage = inventoryItem.getValue().getDamage() == null ? "0" : "" + inventoryItem.getValue().getDamage().intValue();

                    String armor = inventoryItem.getValue().getArmor() == null ? "0" : "" + inventoryItem.getValue().getArmor().intValue();
                    writer.write("inventory_item " + slotId + " " + inventoryItem.getValue().getItemType().getId() + " " + damage + " " + armor + "\n");
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

                    Creature foundCreature = null;
                    for (Area area : areaMap.values()) {
                        foundCreature = area.getAreaCreaturesHolder().getCreatureById(s[1]);
                        if (foundCreature != null) break;
                    }

                    creature = foundCreature;

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

                        areaMap.get(s[1]).getAreaCreaturesHolder().insertCreature(creature);

                        if (creature instanceof PlayerCharacter) {
                            currentAreaManager.setCurrentArea(areaMap.get(s[1]));
                        }
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

                if (creature instanceof PlayerCharacter) {
                  if (creature.getHealthPoints() <= 0f) {
                    creature.onDeath();
                  }
                }

                line = reader.readLine();

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            reader = new BufferedReader(new FileReader("saves/inventory.sav"));
            String line = reader.readLine();
            while (line != null) {
                String[] s = line.split(" ");

                if(s[0].equals("inventory_item")) {
                    if (creature != null) {
                        Map<Integer, Item> inventoryItems = inventoryWindow.getInventoryItems();
                        inventoryItems.put(Integer.parseInt(s[1]), new Item(ItemType.getItemType(s[2]), null, (s[3].equals("0") ? null : (float)(Integer.parseInt(s[3]))), (s[4].equals("0") ? null : (float)(Integer.parseInt(s[4])))));
                    }

                }

                line = reader.readLine();

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



        if(currentAreaManager.getCurrentArea() == null) {
            currentAreaManager.setCurrentArea(areaMap.get("area1"));
        }

        currentAreaManager.getCurrentArea().getAreaCreaturesHolder().updateAttackTypes();

    }

}