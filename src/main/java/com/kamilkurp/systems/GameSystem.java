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
import com.kamilkurp.items.InventoryWindow;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.projectile.Arrow;
import com.kamilkurp.spawn.PlayerRespawnPoint;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.terrain.CurrentAreaHolder;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

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

    public GameSystem() throws SlickException {
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

        cameraFocusedCreature = playerCharacter;

        NonPlayerCharacter nonPlayerCharacter = new NonPlayerCharacter(this, "Johnny", "a1", true);
        areas.get("area1").addNewCreature(nonPlayerCharacter, 1512f, 11f);
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
        Globals.updateTimers(i);

        keyInput.readKeyPresses(gc.getInput());

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



        camera.update(gc, cameraFocusedCreature.getRect());


        inventoryWindow.update(keyInput);

        dialogueWindow.update(keyInput);


        lootSystem.update(keyInput, playerCharacter);


        for (AreaGate areaGate : gateList) {
            areaGate.update(this);
        }

        currentArea.getCreaturesManager().updateRenderPriorityQueue();

        hud.update();
    }

    public void render(Graphics g) {
        Area currentArea = currentAreaHolder.getCurrentArea();

        if (cameraFocusedCreature.getArea() == currentArea) {

            currentArea.render(g, camera);

            currentArea.renderSpawns(g, camera);

            for (PlayerRespawnPoint playerRespawnPoint : currentArea.getRespawnList()) {
                playerRespawnPoint.render(g, camera);
            }

            for (AreaGate areaGate : gateList) {
                areaGate.render(g, camera, currentArea);
            }

            currentArea.getCreaturesManager().renderCreatures(g, camera);

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

    public void updateCreatures(GameContainer gc, int i, KeyInput keyInput) {
        Map<String, Creature> areaCreatures = getCurrentArea().getCreatures();

        for (Creature creature : areaCreatures.values()) {
//            if (creature instanceof PlayerCharacter) {
//                if (!getInventoryWindow().isInventoryOpen() && !getLootOptionWindow().isActivated() && !getDialogueWindow().isActivated()) {
//                    creature.update(gc, i, keyInput);
//
//                    creature.areaGateLogic(getGateList());
//                }
//            }

            creature.update(gc, i, keyInput, this);

        }

        if (markRespawnAreaForReset) {
            markRespawnAreaForReset = false;

            playerCharacter.getRespawnArea().reset();
        }
    }

    public void resetArea() {
        markRespawnAreaForReset = true;
    }

}
