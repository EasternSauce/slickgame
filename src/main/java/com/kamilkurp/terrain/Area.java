package com.kamilkurp.terrain;

import com.kamilkurp.Renderable;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.spawn.EnemyRespawnArea;
import com.kamilkurp.spawn.EnemySpawnPoint;
import com.kamilkurp.spawn.SpawnLocationsContainer;
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
import java.util.Map;
import java.util.TreeMap;

public class Area implements Renderable {

    private List<TerrainTile> tiles;

    private TerrainTileset terrainTileset;
    private TerrainLayout terrainLayout;
    private SpawnLocationsContainer spawnLocationsContainer;

    private List<EnemyRespawnArea> enemyRespawnAreaList;
    private List<EnemySpawnPoint> enemySpawnPointList;

    private Map<String, Creature> creaturesMap;
    private List<Creature> creaturesList;

    private LootSystem lootSystem;

    private String id;

    public Area(String id, TerrainTileset terrainTileset, TerrainLayout terrainLayout, SpawnLocationsContainer spawnsContainer, LootSystem lootSystem) throws SlickException {
        this.lootSystem = lootSystem;

        this.terrainTileset = terrainTileset;
        this.terrainLayout = terrainLayout;
        this.spawnLocationsContainer = spawnsContainer;

        this.id = id;


        creaturesMap = new TreeMap<>();
        creaturesList = new LinkedList<>();

        tiles = new LinkedList<>();

        enemyRespawnAreaList = new LinkedList<>();
        enemySpawnPointList = new LinkedList<>();

        loadLayoutTiles();

        if (creaturesMap != null && creaturesList != null && lootSystem != null) {
            loadSpawns();

        }

    }

    private void loadSpawns() throws SlickException {
        for (SpawnLocationsContainer.SpawnLocation spawnLocation : spawnLocationsContainer.getSpawnLocationList()) {
            int posX = spawnLocation.getPosX();
            int posY = spawnLocation.getPosY();

            if (spawnLocation.getSpawnType().equals("respawnArea")) {
                if (spawnLocation.getEnemyType().equals("skeleton")) {
                    enemyRespawnAreaList.add(new EnemyRespawnArea(posX, posY, 3, this, lootSystem));
                }
            } else if (spawnLocation.getSpawnType().equals("spawnPoint")) {
                if (spawnLocation.getEnemyType().equals("skeleton")) {
                    enemySpawnPointList.add(new EnemySpawnPoint(posX, posY, this, lootSystem));
                }
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

    public Map<String, Creature> getCreaturesMap() {
        return creaturesMap;
    }

    public List<Creature> getCreaturesList() {
        return creaturesList;
    }

    public String getId() {
        return id;
    }


}

