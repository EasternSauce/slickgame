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
    private int tileWidth;
    private int tileHeight;

    private int tilesetRows;
    private int tilesetColumns;

    private int terrainColumns;
    private int terrainRows;

    private int scale;

    private SpriteSheet spriteSheet;
    private String[][] passable;

    private int passableColumns;
    private int passableRows;

    private String[][] layout;

    private List<TerrainTile> tiles;

    private Map<String, TerrainImage> terrainImages;


    public Terrain(int tileWidth, int tileHeight, int tilesetColumns, int tilesetRows, int scale) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tilesetRows = tilesetRows;
        this.tilesetColumns = tilesetColumns;
        this.scale = scale;

        terrainImages = new HashMap<>();

        tiles = new LinkedList<>();

    }

    @Override
    public void render(Graphics g, Camera camera) {

        g.setColor(Color.white);
        for(TerrainTile tile : tiles) {
            tile.render(g, camera);
        }
    }

    public void loadSpriteSheet(String path) throws SlickException {
        Image image = new Image(path);
        spriteSheet = new SpriteSheet(image, tileWidth, tileHeight);

        for (int i = 0; i < tilesetRows; i++) {
            for (int j = 0; j < tilesetColumns; j++) {
                String code = String.format ("%03d%03d", i, j);
                if (i < passableRows && j < passableColumns) {
                    addTerrainImage(code, j, i, passable[i][j].equals("0"));
                }
                else {
                    addTerrainImage(code, j, i, true);
                }
            }
        }


    }

    public void loadLayout() {
        int layoutWidth = layout[0].length;
        int layoutHeight = layout.length;

        for(int i=0; i < layoutHeight; i++) {
            for(int j=0; j < layoutWidth; j++) {
                Rectangle rect = new Rectangle(300 + j * 64,300 + i*64,64, 64);

                TerrainImage tileImage = terrainImages.get(layout[i][j]);
                tiles.add(new TerrainTile(rect, tileImage));
            }

        }
    }


    private void addTerrainImage(String id, int posX, int posY, boolean passable) {
        Image image = spriteSheet.getSprite(posX,posY);
        image.setFilter(Image.FILTER_NEAREST);
        TerrainImage terrainImage = new TerrainImage(image, posX, posY, passable);
        terrainImages.put(id, terrainImage);

    }

    public List<TerrainTile> getTiles() {
        return tiles;
    }

    public void loadTerrain(String fileName) {
        BufferedReader reader;
        String [][] myArray = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            String[] s = line.split(" ");
            int columns = Integer.parseInt(s[0]);
            int rows = Integer.parseInt(s[1]);
            myArray = new String[rows][columns];
            line = reader.readLine();
            while (line != null) {
                for (int i=0; i<myArray.length; i++) {
                    String[] s1 = line.split(" ");
                    for (int j = 0; j < s1.length; j++) {
                        myArray[i][j] = s1[j];
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
        layout = myArray;


    }

    public void loadPassable(String fileName) {
        BufferedReader reader;
        String [][] myArray = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            String[] s = line.split(" ");
            passableColumns = Integer.parseInt(s[0]);
            passableRows = Integer.parseInt(s[1]);
            myArray = new String[passableRows][passableColumns];
            line = reader.readLine();
            while (line != null) {
                for (int i=0; i<myArray.length; i++) {
                    String[] s1 = line.split(" ");
                    for (int j = 0; j < s1.length; j++) {
                        myArray[i][j] = s1[j];
                    }
                    line = reader.readLine();

                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        passable = myArray;


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

    public SpriteSheet getSpriteSheet() {
        return spriteSheet;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getTilesetRows() {
        return tilesetRows;
    }

    public int getTilesetColumns() {
        return tilesetColumns;
    }

    public void setTile(int x, int y, String id) {

        layout[y][x] = id;
    }

    public Map<String, TerrainImage> getTerrainImages() {
        return terrainImages;
    }
}

