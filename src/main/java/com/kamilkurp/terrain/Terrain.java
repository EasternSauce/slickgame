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
    private int spriteWidth = 16;
    private int spriteHeight = 16;

    private int spritesheetWidth = 10;
    private int spritesheetHeight = 10;

    private int width;
    private int height;

    private int scale = 4;

    private SpriteSheet spriteSheet;
    private String[][] passable;

    private String[][] layout;

//            = {
//            {"a1", "t1", "t1", "t1", "t1", "t1", "t1", "a2", "a1", "t1", "t1", "t1", "t1", "t1", "a2"},
//            {"w1", "o1", "o1", "o1", "o1", "o1", "o1", "w2", "w1", "o1", "o1", "o1", "o1", "o1", "w2"},
//            {"w1", "o1", "o1", "o1", "o1", "o1", "o1", "w2", "w1", "o1", "o1", "o1", "o1", "o1", "w2"},
//            {"w1", "o1", "o1", "o1", "o1", "o1", "o1", "t1", "t1", "o1", "o1", "o1", "o1", "o1", "w2"},
//            {"w1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "w2"},
//            {"w1", "o1", "o1", "o1", "o1", "o1", "o1", "x1", "x2", "o1", "o1", "o1", "o1", "o1", "w2"},
//            {"w1", "o1", "o1", "o1", "o1", "o1", "o1", "w2", "w1", "o1", "o1", "o1", "o1", "o1", "w2"},
//            {"c1", "t2", "t2", "t2", "t2", "o1", "x1", "c2", "c1", "x2", "o1", "o1", "o1", "t2", "c2"},
//            {"a1", "t1", "t1", "t1", "t1", "o1", "t1", "t1", "t1", "t1", "o1", "o1", "o1", "t1", "a2"},
//            {"w1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "w2"},
//            {"w1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "w2"},
//            {"w1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "w2"},
//            {"w1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "w2"},
//            {"w1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "w2"},
//            {"w1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "o1", "w2"},
//            {"c1", "t2", "t2", "t2", "t2", "t2", "t2", "t2", "t2", "t2", "t2", "t2", "t2", "t2", "c2"}
//    };

    private List<TerrainTile> tiles;

    private Map<String, TerrainImage> terrainImages;


    public Terrain() {
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
        spriteSheet = new SpriteSheet(image, spriteWidth, spriteHeight);

//        addTerrainImage("a1", 0, 0, false);
//        addTerrainImage("t1", 1, 0, false);
//        addTerrainImage("a2", 5, 0, false);
//        addTerrainImage("w1", 0, 1, false);
//        addTerrainImage("o1", 2, 2, true);
//        addTerrainImage("w2", 5, 1, false);
//        addTerrainImage("c1", 0, 4, false);
//        addTerrainImage("t2", 1, 4, false);
//        addTerrainImage("c2", 5, 4, false);
//        addTerrainImage("x1", 0, 5, false);
//        addTerrainImage("x2", 3, 5, false);

        for (int i = 0; i < spritesheetHeight; i++) {
            for (int j = 0; j < spritesheetWidth; j++) {
                String code = String.format ("%03d%03d", i, j);
                addTerrainImage(code, j, i, passable[i][j].equals("0"));
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
            width = columns;
            height = rows;

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

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        passable = myArray;


    }

    public void saveTerrain(String fileName) throws IOException {
        FileWriter fileWriter = new FileWriter(fileName);
        PrintWriter printWriter = new PrintWriter(fileWriter);

        System.out.println("saving terrain");

        printWriter.printf("%d %d\n", width, height);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                printWriter.print(layout[i][j] + (j != width - 1 ? " ": ""));
                //System.out.print(layout[i][j] + (j != width - 1 ? " ": ""));
            }
            //System.out.println();

            printWriter.println();
        }
        printWriter.close();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public SpriteSheet getSpriteSheet() {
        return spriteSheet;
    }

    public int getSpriteWidth() {
        return spriteWidth;
    }

    public int getSpriteHeight() {
        return spriteHeight;
    }

    public int getSpritesheetWidth() {
        return spritesheetWidth;
    }

    public int getSpritesheetHeight() {
        return spritesheetHeight;
    }

    public void setTile(int x, int y, String id) {

        layout[y][x] = id;
    }

    public Map<String, TerrainImage> getTerrainImages() {
        return terrainImages;
    }
}

