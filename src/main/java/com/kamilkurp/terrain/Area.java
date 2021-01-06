package com.kamilkurp.terrain;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.NonPlayerCharacter;
import com.kamilkurp.creatures.PlayerCharacter;
import com.kamilkurp.items.LootPile;
import com.kamilkurp.items.Treasure;
import com.kamilkurp.projectile.Arrow;
import com.kamilkurp.spawn.*;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Area {

    private final List<TerrainTile> tiles;

    private final TerrainTileset terrainTileset;
    private final TerrainLayout terrainLayout;
    private final SpawnLocationsContainer spawnLocationsContainer;

    private final List<EnemyRespawnArea> enemyRespawnAreaList;
    private final List<MobSpawnPoint> mobSpawnPointList;

    private final CreaturesManager creaturesManager;


    private final String id;

    private final List <PlayerRespawnPoint> respawnList;

    private final List<Arrow> arrowList;

    private final List<LootPile> lootPileList;

    private final List<Treasure> treasureList;

    private final List<Treasure> remainingTreasureList;

    private GameSystem gameSystem;

    private Music abandonedPlains;

    private List<Blockade> blockadeList;


    public Area(GameSystem gameSystem, String id, TerrainTileset terrainTileset, TerrainLayout terrainLayout, SpawnLocationsContainer spawnsContainer) throws SlickException {
        this.gameSystem = gameSystem;
        this.terrainTileset = terrainTileset;
        this.terrainLayout = terrainLayout;
        this.spawnLocationsContainer = spawnsContainer;

        this.id = id;


        creaturesManager = new CreaturesManager(this, gameSystem);

        tiles = new ArrayList<>();

        enemyRespawnAreaList = new LinkedList<>();
        mobSpawnPointList = new LinkedList<>();
        respawnList = new LinkedList<>();
        arrowList = new LinkedList<>();
        lootPileList = new LinkedList<>();
        treasureList = new LinkedList<>();
        remainingTreasureList = new LinkedList<>();

        loadLayoutTiles();

        blockadeList = new LinkedList<>();

        
        if (gameSystem != null && gameSystem.getLootSystem() != null) {
            loadSpawns();

        }

        abandonedPlains = Assets.abandonedPlains;

    }

    private void loadSpawns() throws SlickException {
        for (SpawnLocation spawnLocation : spawnLocationsContainer.getSpawnLocationList()) {
            int posX = spawnLocation.getPosX();
            int posY = spawnLocation.getPosY();

            if (spawnLocation.getSpawnType().equals("respawnArea")) {
                enemyRespawnAreaList.add(new EnemyRespawnArea(gameSystem, posX, posY, 3, this, spawnLocation.getCreatureType()));


            } else if (spawnLocation.getSpawnType().equals("spawnPoint")) {

                MobSpawnPoint mobSpawnPoint = new MobSpawnPoint(gameSystem, posX, posY, this, spawnLocation.getCreatureType());
                mobSpawnPointList.add(mobSpawnPoint);

                if (spawnLocation.isHasBlockade()) {
                    addBlockade(mobSpawnPoint, spawnLocation.getBlockadePosX(), spawnLocation.getBlockadePosY());
                }
            }
        }
    }

    public void render(Graphics g, Camera camera) {

        g.setColor(Color.white);
        for(TerrainTile tile : tiles) {
            tile.render(g, camera);
        }

        for (Blockade blockade : blockadeList) {
            blockade.render(g, camera);
        }
    }



    public List<TerrainTile> getTiles() {
        return tiles;
    }

    public void updateSpawns() throws SlickException {
        for (EnemyRespawnArea enemyRespawnArea : enemyRespawnAreaList) {
            enemyRespawnArea.update();
        }

        for (MobSpawnPoint mobSpawnPoint : mobSpawnPointList) {
            mobSpawnPoint.update();
        }
    }




    public void saveTerrainLayoutToFile(String fileName) throws IOException {
        FileWriter fileWriter = new FileWriter(fileName);
        PrintWriter printWriter = new PrintWriter(fileWriter);

        printWriter.printf("%d %d\n", terrainLayout.getLayoutColumns(), terrainLayout.getLayoutRows());

        for (int i = 0; i < terrainLayout.getLayoutRows(); i++) {
            for (int j = 0; j < terrainLayout.getLayoutColumns(); j++) {
                printWriter.print(terrainLayout.getLayoutTile(j, i) + (j != terrainLayout.getLayoutColumns() - 1 ? " ": ""));
            }

            printWriter.println();
        }
        printWriter.close();
    }

    public int getTerrainColumns() {
        return terrainLayout.getLayoutColumns();
    }

    public int getTerrainRows() {
        return terrainLayout.getLayoutRows();
    }



    public void setTile(int x, int y, String id) {

        terrainLayout.setLayoutTile(x, y, id);
    }

    public TerrainTileset getTerrainTileset() {
        return terrainTileset;
    }

    public void loadLayoutTiles() {
        for(int i=0; i < terrainLayout.getLayoutRows(); i++) {
            for(int j=0; j < terrainLayout.getLayoutColumns(); j++) {
                Rectangle rect = new Rectangle(j * 64,i*64,64, 64);

                TerrainImage tileImage = terrainTileset.getTerrainImages().get(terrainLayout.getLayoutTile(j, i));
                tiles.add(new TerrainTile(rect, tileImage));
            }

        }
    }

    public void renderSpawns(Graphics g, Camera camera) {

        for (EnemyRespawnArea enemyRespawnArea : enemyRespawnAreaList) {
            enemyRespawnArea.render(g, camera);
        }
    }

    public String getId() {
        return id;
    }


    public void addRespawnPoint(PlayerRespawnPoint respawnPoint) {
        respawnList.add(respawnPoint);
    }

    public List<PlayerRespawnPoint> getRespawnList() {
        return respawnList;
    }

    public void onLeave() {
        arrowList.clear();
        lootPileList.clear();

        for (MobSpawnPoint mobSpawnPoint : mobSpawnPointList) {
            mobSpawnPoint.markForRespawn();
        }



    }

    public void onEntry() {
        abandonedPlains.stop();
        if (id.equals("area1")) {
            abandonedPlains.loop(1.0f, Globals.MUSIC_VOLUME);
        }

        creaturesManager.onAreaChange();

        reset();

        getCreaturesManager().initializeCreatures();

    }

    public List<Arrow> getArrowList() {
        return arrowList;
    }

    public List<LootPile> getLootPileList() {
        return lootPileList;
    }

    public CreaturesManager getCreaturesManager() {
        return creaturesManager;
    }

    public void moveInCreature(Creature creature, float x, float y) {
        creaturesManager.addCreature(creature);
        creature.setArea(this);

        creature.getRect().setX(x);
        creature.getRect().setY(y);
    }

    public void removeCreature(String id) {
        creaturesManager.removeCreature(id);
    }

    public Map<String, Creature> getCreatures() {
        return creaturesManager.getCreatures();
    }

    public void addNewCreature(Creature creature, float x, float y) {
        creaturesManager.addCreature(creature);
        creature.setArea(this);

        creature.getRect().setX(x);
        creature.getRect().setY(y);

        creature.setStartingPosX(x);
        creature.setStartingPosY(y);
    }

    public void reset() {
        arrowList.clear();
        lootPileList.clear();
        creaturesManager.clearRespawnableCreatures();

        for (MobSpawnPoint mobSpawnPoint : mobSpawnPointList) {
            mobSpawnPoint.markForRespawn();
        }

        for (Blockade blockade : blockadeList) {
            blockade.setActive(false);
        }

        getCreaturesManager().initializeCreatures();

    }

    public List<Treasure> getTreasureList() {
        return treasureList;
    }

    public List<Treasure> getRemainingTreasureList() {
        return remainingTreasureList;
    }

    public void softReset() {
        for (Creature creature : creaturesManager.getCreatures().values()) {
            if (creature.isAlive() && !(creature instanceof PlayerCharacter) && !(creature instanceof NonPlayerCharacter)) {
                creature.reset();
            }
        }
    }

    public void addBlockade(MobSpawnPoint mobSpawnPoint, int blockadePosX, int blockadePosY) {
        Blockade blockade = new Blockade(mobSpawnPoint, blockadePosX, blockadePosY);
        blockadeList.add(blockade);
        mobSpawnPoint.setBlockade(blockade);

    }

    public List<Blockade> getBlockadeList() {
        return blockadeList;
    }

    public void update(int i, GameContainer gc, KeyInput keyInput) {
        List<Arrow> toBeDeleted = new LinkedList<>();
        for (Arrow arrow : getArrowList()) {
            arrow.update(i);
            if (arrow.isMarkedForDeletion()) {
                toBeDeleted.add(arrow);
            }
        }

        getArrowList().removeAll(toBeDeleted);

        Map<String, Creature> areaCreatures = getCreatures();

        areaCreatures.values().forEach(creature -> creature.update(gc, i, keyInput, gameSystem));

    }
}

