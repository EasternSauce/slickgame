package com.kamilkurp.terrain;

import com.kamilkurp.Renderable;
import com.kamilkurp.creatures.AreaCreaturesHolder;
import com.kamilkurp.items.LootPile;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.projectile.Arrow;
import com.kamilkurp.spawn.EnemyRespawnArea;
import com.kamilkurp.spawn.EnemySpawnPoint;
import com.kamilkurp.spawn.PlayerRespawnPoint;
import com.kamilkurp.spawn.SpawnLocationsContainer;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

public class Area implements Renderable {

    private final List<TerrainTile> tiles;

    private final TerrainTileset terrainTileset;
    private final TerrainLayout terrainLayout;
    private final SpawnLocationsContainer spawnLocationsContainer;

    private final List<EnemyRespawnArea> enemyRespawnAreaList;
    private final List<EnemySpawnPoint> enemySpawnPointList;

    private final AreaCreaturesHolder areaCreaturesHolder;


    private final String id;

    private final List <PlayerRespawnPoint> respawnList;

    private final List<Arrow> arrowList;

    private final List<LootPile> lootPileList;

    private GameSystem gameSystem;

    public Area(GameSystem gameSystem, String id, TerrainTileset terrainTileset, TerrainLayout terrainLayout, SpawnLocationsContainer spawnsContainer) throws SlickException {
        this.gameSystem = gameSystem;
        this.terrainTileset = terrainTileset;
        this.terrainLayout = terrainLayout;
        this.spawnLocationsContainer = spawnsContainer;

        this.id = id;


        areaCreaturesHolder = new AreaCreaturesHolder(this);

        tiles = new LinkedList<>();

        enemyRespawnAreaList = new LinkedList<>();
        enemySpawnPointList = new LinkedList<>();
        respawnList = new LinkedList<>();
        arrowList = new LinkedList<>();
        lootPileList = new LinkedList<>();

        loadLayoutTiles();

        if (gameSystem != null && gameSystem.getLootSystem() != null) {
            loadSpawns();

        }

    }

    private void loadSpawns() throws SlickException {
        for (SpawnLocationsContainer.SpawnLocation spawnLocation : spawnLocationsContainer.getSpawnLocationList()) {
            int posX = spawnLocation.getPosX();
            int posY = spawnLocation.getPosY();

            if (spawnLocation.getSpawnType().equals("respawnArea")) {
                if (spawnLocation.getCreatureType().equals("skeleton")) {
                    enemyRespawnAreaList.add(new EnemyRespawnArea(gameSystem, posX, posY, 3, this));
                }
            } else if (spawnLocation.getSpawnType().equals("spawnPoint")) {
                enemySpawnPointList.add(new EnemySpawnPoint(gameSystem, posX, posY, this, spawnLocation.getCreatureType()));
            }
        }
    }

    @Override
    public void render(Graphics g, Camera camera) {

        g.setColor(Color.white);
        for(TerrainTile tile : tiles) {
            tile.render(g, camera);
        }
    }



    public List<TerrainTile> getTiles() {
        return tiles;
    }

    public void updateSpawns() throws SlickException {
        for (EnemyRespawnArea enemyRespawnArea : enemyRespawnAreaList) {
            enemyRespawnArea.update();
        }

        for (EnemySpawnPoint enemySpawnPoint : enemySpawnPointList) {
            enemySpawnPoint.update();
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

        for (EnemySpawnPoint enemySpawnPoint : enemySpawnPointList) {
            enemySpawnPoint.markForRespawn();
        }



    }

    public void onEntry() {

        areaCreaturesHolder.onAreaChange();

    }

    public List<Arrow> getArrowList() {
        return arrowList;
    }

    public List<LootPile> getLootPileList() {
        return lootPileList;
    }

    public AreaCreaturesHolder getAreaCreaturesHolder() {
        return areaCreaturesHolder;
    }
}

