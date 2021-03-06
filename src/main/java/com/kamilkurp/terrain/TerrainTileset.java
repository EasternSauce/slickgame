package com.kamilkurp.terrain;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TerrainTileset {
    private final int tileWidth;
    private final int tileHeight;

    private final int tilesetColumns;
    private final int tilesetRows;

    private final int scale;

    private SpriteSheet spriteSheet;
    private String[][] passable;

    private int passableColumns;
    private int passableRows;

    private final Map<String, TerrainImage> terrainImages;


    public TerrainTileset(int tileWidth, int tileHeight, int tilesetColumns, int tilesetRows, int scale, String spritesheetFileName, String passableFileName) throws SlickException {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tilesetColumns = tilesetColumns;
        this.tilesetRows = tilesetRows;
        this.scale = scale;

        terrainImages = new HashMap<>();

        loadSpriteSheet(spritesheetFileName, passableFileName);

    }

    public void loadSpriteSheet(String path, String passableFileName) throws SlickException {
        loadPassable(passableFileName);


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

    public void loadPassable(String fileName) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            String[] s = line.split(" ");
            passableColumns = Integer.parseInt(s[0]);
            passableRows = Integer.parseInt(s[1]);
            passable = new String[passableRows][passableColumns];
            line = reader.readLine();
            while (line != null) {
                for (int i=0; i<passable.length; i++) {
                    String[] s1 = line.split(" ");
                    System.arraycopy(s1, 0, passable[i], 0, s1.length);
                    line = reader.readLine();

                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void addTerrainImage(String id, int posX, int posY, boolean passable) {
        Image image = spriteSheet.getSprite(posX,posY);
        image.setFilter(Image.FILTER_NEAREST);
        TerrainImage terrainImage = new TerrainImage(image, posX, posY, passable);
        terrainImages.put(id, terrainImage);

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

    public Map<String, TerrainImage> getTerrainImages() {
        return terrainImages;
    }

}
