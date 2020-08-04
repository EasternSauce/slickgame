package com.kamilkurp.terrain;

import com.kamilkurp.Renderable;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

public class Area implements Renderable {

    private List<TerrainTile> tiles;

    private TerrainTileset terrainTileset;
    private TerrainLayout terrainLayout;

    public Area(TerrainTileset terrainTileset, TerrainLayout terrainLayout) {

        tiles = new LinkedList<>();

        this.terrainTileset = terrainTileset;
        this.terrainLayout = terrainLayout;


        loadLayoutTiles();

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
                Rectangle rect = new Rectangle(300 + j * 64,300 + i*64,64, 64);

                TerrainImage tileImage = terrainTileset.getTerrainImages().get(terrainLayout.getLayoutTile(j, i));
                tiles.add(new TerrainTile(rect, tileImage));
            }

        }
    }
}

