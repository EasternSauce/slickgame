package com.kamilkurp.terrain;

import com.kamilkurp.utils.Camera;
import com.kamilkurp.Renderable;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Terrain implements Renderable {




    private String[][] layout;

    private List<TerrainTile> tiles;


    private TerrainTileset terrainTileset;

    private int terrainColumns;
    private int terrainRows;

    public Terrain(TerrainTileset terrainTileset, String layoutFileName) {

        tiles = new LinkedList<>();

        this.terrainTileset = terrainTileset;

        loadLayout(layoutFileName);

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

    public void loadLayout(String fileName) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            String[] s = line.split(" ");
            int columns = Integer.parseInt(s[0]);
            int rows = Integer.parseInt(s[1]);
            layout = new String[rows][columns];
            line = reader.readLine();
            while (line != null) {
                for (int i=0; i<layout.length; i++) {
                    String[] s1 = line.split(" ");
                    for (int j = 0; j < s1.length; j++) {
                        layout[i][j] = s1[j];
                    }
                    line = reader.readLine();

                }
            }
            terrainColumns = columns;
            terrainRows = rows;

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int layoutWidth = layout[0].length;
        int layoutHeight = layout.length;

        for(int i=0; i < layoutHeight; i++) {
            for(int j=0; j < layoutWidth; j++) {
                Rectangle rect = new Rectangle(300 + j * 64,300 + i*64,64, 64);

                TerrainImage tileImage = terrainTileset.getTerrainImages().get(layout[i][j]);
                tiles.add(new TerrainTile(rect, tileImage));
            }

        }


    }


    public void saveTerrain(String fileName) throws IOException {
        FileWriter fileWriter = new FileWriter(fileName);
        PrintWriter printWriter = new PrintWriter(fileWriter);

        printWriter.printf("%d %d\n", terrainColumns, terrainRows);

        for (int i = 0; i < terrainRows; i++) {
            for (int j = 0; j < terrainColumns; j++) {
                printWriter.print(layout[i][j] + (j != terrainColumns - 1 ? " ": ""));
            }

            printWriter.println();
        }
        printWriter.close();
    }

    public int getTerrainColumns() {
        return terrainColumns;
    }

    public int getTerrainRows() {
        return terrainRows;
    }



    public void setTile(int x, int y, String id) {

        layout[y][x] = id;
    }

    public TerrainTileset getTerrainTileset() {
        return terrainTileset;
    }
}

