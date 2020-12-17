package com.kamilkurp.systems;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.areagate.AreaGate;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.NonPlayerCharacter;
import com.kamilkurp.creatures.PlayerCharacter;
import com.kamilkurp.dialogue.DialogueWindow;
import com.kamilkurp.gui.Hud;
import com.kamilkurp.gui.LootOptionWindow;
import com.kamilkurp.gui.MainMenu;
import com.kamilkurp.items.InventoryWindow;
import com.kamilkurp.items.Item;
import com.kamilkurp.items.ItemType;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.spawn.PlayerRespawnPoint;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.terrain.CurrentAreaHolder;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GameSystem {
    private CurrentAreaHolder currentAreaHolder;
    private InventoryWindow inventoryWindow;
    private DialogueWindow dialogueWindow;
    private LootSystem lootSystem;
    private LootOptionWindow lootOptionWindow;
    private Map<String, Area> areas;
    private List<AreaGate> gateList;
    private List<Creature> creaturesToMove;
    private Camera camera;
    private Hud hud;
    private PlayerCharacter playerCharacter;
    private Creature cameraFocusedCreature;
    private boolean markRespawnAreaForReset;
    private MainMenu mainMenu;

    private GameState state;
    private boolean escRecently;

    public GameSystem() throws SlickException {
        init();

        state = GameState.MAIN_MENU;

        mainMenu = new MainMenu(this);
    }

    private void init() throws SlickException {
        inventoryWindow = new InventoryWindow(this);

        lootOptionWindow = new LootOptionWindow(this);

        dialogueWindow = new DialogueWindow(this, Globals.getAssetsLocation() + "dialogues/dialogues.txt");

        currentAreaHolder = new CurrentAreaHolder();

        lootSystem = new LootSystem(this);

        areas = new HashMap<>();
        areas.put("area1", new Area(this, "area1", Assets.grassyTileset, Assets.area1Layout, Assets.area1Enemies));
        areas.put("area2", new Area(this,"area2", Assets.dungeonTileset, Assets.area2Layout, Assets.area2Enemies));

        areas.get("area1").addRespawnPoint(new PlayerRespawnPoint(this, 400, 500, areas.get("area1")));
        areas.get("area1").addRespawnPoint(new PlayerRespawnPoint(this, 3650, 4909, areas.get("area1")));

        areas.get("area2").addRespawnPoint(new PlayerRespawnPoint(this, 594, 133, areas.get("area2")));
        areas.get("area2").addRespawnPoint(new PlayerRespawnPoint(this, 1342, 2099, areas.get("area2")));

        playerCharacter = new PlayerCharacter(this, "Protagonist");
        areas.get("area1").addNewCreature(playerCharacter, 400f, 400f);

        lootSystem.placeTreasure(areas.get("area1"), 1920, 8, ItemType.getItemType("leatherArmor"));
        lootSystem.placeTreasure(areas.get("area1"), 3551, 3840, ItemType.getItemType("woodenSword"));
        lootSystem.placeTreasure(areas.get("area1"), 3145, 2952, ItemType.getItemType("lifeRing"));
        lootSystem.placeTreasure(areas.get("area1"), 1332, 2833, ItemType.getItemType("ironSword"));
        lootSystem.placeTreasure(areas.get("area2"), 3100, 2654, ItemType.getItemType("crossbow"));
        lootSystem.placeTreasure(areas.get("area2"), 168, 3024, ItemType.getItemType("trident"));
        lootSystem.placeTreasure(areas.get("area1"), 600, 500, ItemType.getItemType("healingPowder"));


        cameraFocusedCreature = playerCharacter;

        NonPlayerCharacter nonPlayerCharacter = new NonPlayerCharacter(this, "Johnny", "a1", true, Assets.male2SpriteSheet);
        areas.get("area1").addNewCreature(nonPlayerCharacter, 1512f, 11f);
        NonPlayerCharacter nonPlayerCharacter2 = new NonPlayerCharacter(this, "Rita", "a1", true, Assets.female1SpriteSheet);
        areas.get("area2").addNewCreature(nonPlayerCharacter2, 183f, 95f);
        inventoryWindow.setPlayerCharacter(playerCharacter);

        camera = new Camera();

        hud = new Hud(this);

        gateList = new LinkedList<>();

        gateList.add(new AreaGate(areas.get("area1"), 20, 3960, areas.get("area2"), 3690, 262));

        creaturesToMove = new LinkedList<>();

        markRespawnAreaForReset = false;
    }

    public CurrentAreaHolder getCurrentAreaHolder() {
        return currentAreaHolder;
    }

    public void setCurrentAreaHolder(CurrentAreaHolder currentAreaHolder) {
        this.currentAreaHolder = currentAreaHolder;
    }

    public InventoryWindow getInventoryWindow() {
        return inventoryWindow;
    }

    public void setInventoryWindow(InventoryWindow inventoryWindow) {
        this.inventoryWindow = inventoryWindow;
    }

    public DialogueWindow getDialogueWindow() {
        return dialogueWindow;
    }

    public void setDialogueWindow(DialogueWindow dialogueWindow) {
        this.dialogueWindow = dialogueWindow;
    }

    public LootSystem getLootSystem() {
        return lootSystem;
    }

    public void setLootSystem(LootSystem lootSystem) {
        this.lootSystem = lootSystem;
    }

    public LootOptionWindow getLootOptionWindow() {
        return lootOptionWindow;
    }

    public void setLootOptionWindow(LootOptionWindow lootOptionWindow) {
        this.lootOptionWindow = lootOptionWindow;
    }

    public Map<String, Area> getAreas() {
        return areas;
    }

    public void setAreas(Map<String, Area> areas) {
        this.areas = areas;
    }

    public List<AreaGate> getGateList() {
        return gateList;
    }

    public void setGateList(List<AreaGate> gateList) {
        this.gateList = gateList;
    }

    public List<Creature> getCreaturesToMove() {
        return creaturesToMove;
    }

    public void setCreaturesToMove(List<Creature> creaturesToMove) {
        this.creaturesToMove = creaturesToMove;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Hud getHud() {
        return hud;
    }

    public void setHud(Hud hud) {
        this.hud = hud;
    }

    public PlayerCharacter getPlayerCharacter() {
        return playerCharacter;
    }

    public void setPlayerCharacter(PlayerCharacter playerCharacter) {
        this.playerCharacter = playerCharacter;
    }

    public Creature getCameraFocusedCreature() {
        return cameraFocusedCreature;
    }

    public void setCameraFocusedCreature(Creature cameraFocusedCreature) {
        this.cameraFocusedCreature = cameraFocusedCreature;
    }

    public Area getCurrentArea() {
        return currentAreaHolder.getCurrentArea();
    }

    public void update(GameContainer gc, int i, KeyInput keyInput) throws SlickException {
        keyInput.readKeyPresses(gc.getInput());

        escRecently = false;

        if (state == GameState.MAIN_MENU) {
            mainMenu.update(keyInput);
//            if (gc.getInput().isKeyPressed(Input.KEY_ENTER)) {
//                state = GameState.GAMEPLAY;
//            }
        }

        else if (state == GameState.GAMEPLAY) {
            Globals.updateTimers(i);

            Area currentArea = currentAreaHolder.getCurrentArea();

            creaturesToMove.clear();

            // for current area
            updateCreatures(gc, i, keyInput);

            // for all areas
            for (Area area : areas.values()) {
                area.getCreaturesManager().processAreaChanges(creaturesToMove);
            }

            for (Creature creature : creaturesToMove) {
                if (creature.getPendingArea() != null) {
                    Area oldArea = creature.getArea();
                    Area newArea = creature.getPendingArea();

                    if (oldArea != null) {
                        oldArea.removeCreature(creature.getId());
                    }

                    newArea.moveInCreature(creature, creature.getPendingX(), creature.getPendingY());


                    creature.setArea(newArea);
                }
            }

            if (markRespawnAreaForReset) {
                markRespawnAreaForReset = false;

                playerCharacter.getRespawnArea().reset();
            }



            camera.update(gc, cameraFocusedCreature.getRect());


            inventoryWindow.update(keyInput);

            dialogueWindow.update(keyInput);


            lootSystem.update(keyInput, playerCharacter);

            gateList.forEach(gate -> gate.update(this));

            currentArea.getCreaturesManager().updateRenderPriorityQueue();

            hud.update();

            if (keyInput.isKeyPressed(KeyInput.Key.ESC)) {
                if (!escRecently) {

                    if (!inventoryWindow.isInventoryOpen() && !lootOptionWindow.isActivated()) {

                        escRecently = true;
                        setState(GameState.MAIN_MENU);
                    }
                }
            }

        }


    }

    public void render(Graphics g) {
        if (state == GameState.MAIN_MENU) {
            mainMenu.render(g);
            //g.drawString("Press enter to continue..." , 300, 300);
        }
        else if (state == GameState.GAMEPLAY) {
            g.setWorldClip(0, 0, Globals.SCREEN_WIDTH, Globals.SCREEN_HEIGHT);

            Area currentArea = currentAreaHolder.getCurrentArea();

            if (cameraFocusedCreature.getArea() == currentArea) {

                currentArea.render(g, camera);

                currentArea.renderSpawns(g, camera);

                currentArea.getRespawnList().forEach(respawnPoint -> respawnPoint.render(g, camera));

                gateList.forEach(gate -> gate.render(g, camera, currentArea));

                currentArea.getCreaturesManager().renderCreatures(g, camera);

                currentArea.getArrowList().forEach(arrow -> arrow.render(g, camera));

                lootSystem.render(g, camera);
                inventoryWindow.render(g);

                if (playerCharacter.isRespawning()) {
                    Assets.verdanaHugeTtf.drawString(175, 175, "YOU DIED", Color.red);
                }
            }

            hud.render(g);

            dialogueWindow.render(g);

            lootOptionWindow.render(g);
        }

    }

    public void updateCreatures(GameContainer gc, int i, KeyInput keyInput) {
        Map<String, Creature> areaCreatures = getCurrentArea().getCreatures();

        areaCreatures.values().forEach(creature -> creature.update(gc, i, keyInput, this));


    }

    public boolean isEscRecently() {
        return escRecently;
    }

    public void setEscRecently(boolean escRecently) {
        this.escRecently = escRecently;
    }

    public void resetArea() {
        markRespawnAreaForReset = true;
    }

    public void setState(GameState state) {
        this.state = state;
    }


    public void saveGame() {
        try {
            FileWriter writer = new FileWriter("saves/savegame.sav");

            for (Area area : getAreas().values()) {
                area.getCreaturesManager().saveToFile(writer);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileWriter writer = new FileWriter("saves/inventory.sav");

            for (Map.Entry<Integer, Item> inventoryItem : getInventoryWindow().getInventoryItems().entrySet()) {
                if (inventoryItem.getValue() != null) {
                    int slotId = inventoryItem.getKey();
                    String damage = inventoryItem.getValue().getDamage() == null ? "0" : "" + inventoryItem.getValue().getDamage().intValue();
                    String armor = inventoryItem.getValue().getArmor() == null ? "0" : "" + inventoryItem.getValue().getArmor().intValue();
                    String quantity = inventoryItem.getValue().getQuantity() == null ? "0" : "" + inventoryItem.getValue().getQuantity();

                    writer.write("inventory_item " + slotId + " " + inventoryItem.getValue().getItemType().getId() + " " + damage + " " + armor + " " + quantity + "\n");
                }
            }

            writer.write("gold " + getInventoryWindow().getGold() + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileWriter writer = new FileWriter("saves/respawn_points.sav");

            writer.write("respawnPoint " + getPlayerCharacter().getRespawnArea().getId() + " "
                    + getPlayerCharacter().getRespawnArea().getRespawnList().indexOf(getPlayerCharacter().getCurrentRespawnPoint()));

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadGame() {
        try {
            init();
        } catch (SlickException e) {
            e.printStackTrace();
        }

        Creature creature = null;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("saves/savegame.sav"));
            String line = reader.readLine();
            while (line != null) {
                String[] s = line.split(" ");
                if(s[0].equals("creature")) {
                    Creature foundCreature = null;
                    for (Area area : getAreas().values()) {
                        foundCreature = area.getCreaturesManager().getCreatureById(s[1]);
                        if (foundCreature != null) break;
                    }

                    creature = foundCreature;

                }
                if(s[0].equals("pos")) {
                    if (creature != null) {
                        if (creature.getArea() == null) {
                            throw new RuntimeException("position cannot be set before creature is spawned in area");
                        }
                        creature.getRect().setX(Float.parseFloat(s[1]));
                        creature.getRect().setY(Float.parseFloat(s[2]));

                    }

                }
                if(s[0].equals("area")) {
                    if (creature != null) {
                        creature.setArea(getAreas().get(s[1]));

                        getAreas().get(s[1]).moveInCreature(creature, 0f, 0f);

                        if (creature instanceof PlayerCharacter) {
                            getCurrentAreaHolder().setCurrentArea(getAreas().get(s[1]));
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
                        equipmentItems.put(Integer.parseInt(s[1]), new Item(ItemType.getItemType(s[2]), null, (s[3].equals("0") ? null : (float)(Integer.parseInt(s[3]))), (s[4].equals("0") ? null : (float)(Integer.parseInt(s[4]))), 1));
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
                        Map<Integer, Item> inventoryItems = getInventoryWindow().getInventoryItems();
                        inventoryItems.put(
                                Integer.parseInt(s[1]),
                                new Item(ItemType.getItemType(s[2]),
                                        null, (s[3].equals("0") ? null : (float)(Integer.parseInt(s[3]))),
                                        (s[4].equals("0") ? null : (float)(Integer.parseInt(s[4]))),
                                        (s[5].equals("0") ? null : (Integer.parseInt(s[5])))
                                )
                        );
                    }

                }
                if(s[0].equals("gold")) {
                    getInventoryWindow().setGold(Integer.parseInt(s[1]));
                }

                line = reader.readLine();

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            reader = new BufferedReader(new FileReader("saves/respawn_points.sav"));
            String line = reader.readLine();
            while (line != null) {
                String[] s = line.split(" ");

                if(s[0].equals("respawnPoint")) {
                    PlayerRespawnPoint respawnPoint = getAreas().get(s[1]).getRespawnList().get(Integer.parseInt(s[2]));
                    getPlayerCharacter().setCurrentRespawnPoint(respawnPoint);
                }

                line = reader.readLine();

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            reader = new BufferedReader(new FileReader("saves/treasure_collected.sav"));
            String line = reader.readLine();
            while (line != null) {
                String[] s = line.split(" ");

                if (s[0].equals("treasure")) {
                    Area area = getAreas().get(s[1]);
                    area.getRemainingTreasureList().remove(area.getTreasureList().get(Integer.parseInt(s[2])));
                }

                line = reader.readLine();

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if(getCurrentArea() == null) {
            getCurrentAreaHolder().setCurrentArea(getAreas().get("area1"));
        }

        getCurrentArea().onEntry();

    }
}
