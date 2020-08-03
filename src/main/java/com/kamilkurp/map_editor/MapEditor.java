package com.kamilkurp.map_editor;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;

import com.kamilkurp.gui.HUD;
import com.kamilkurp.terrain.Terrain;
import com.kamilkurp.terrain.TerrainImage;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.*;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public class MapEditor extends BasicGame {

    private Terrain terrain;

    private Camera camera;

    private com.kamilkurp.gui.HUD HUD;


    private KeyInput keyInput;

    private int currentPosX;
    private int currentPosY;

    private int currentSpreadsheetPosX;
    private int currentSpreadsheetPosY;

    private boolean selectMode = false;
    String terrainFile;


    public MapEditor(String gamename) {
        super(gamename);
    }

    @Override
    public void init(GameContainer gc) throws SlickException {
        gc.getGraphics().setAntiAlias(true);

        terrainFile = "grassyTerrain.txt";

        terrain = new Terrain(16,16,12,16,4);
        terrain.loadTerrain(terrainFile);
        terrain.loadPassable("grassyTileset_passable.txt");
        terrain.loadSpriteSheet("Tilemapnew.png");
        terrain.loadLayout();

        camera = new Camera();

        HUD = new HUD();

        keyInput = new KeyInput();

        currentPosX = 0;
        currentPosY = 0;

        currentSpreadsheetPosX = 0;
        currentSpreadsheetPosY = 0;

    }

    @Override
    public void update(GameContainer gc, int i) throws SlickException {
        Globals.updateTimers(i);

        keyInput.readKeyPresses(gc.getInput());

        if (!selectMode) {
            if (keyInput.isKeyPressed(KeyInput.Key.W)) {
                if (currentPosY > 0) currentPosY--;
            }
            if (keyInput.isKeyPressed(KeyInput.Key.S)) {
                if (currentPosY < terrain.getTerrainRows() - 1) currentPosY++;

            }
            if (keyInput.isKeyPressed(KeyInput.Key.A)) {
                if (currentPosX > 0) currentPosX--;
            }
            if (keyInput.isKeyPressed(KeyInput.Key.D)) {
                if (currentPosX < terrain.getTerrainColumns() - 1) currentPosX++;
            }

            if (gc.getInput().isKeyDown(Input.KEY_LSHIFT) && gc.getInput().isKeyDown(Input.KEY_W)) {
                if (currentPosY > 0) currentPosY--;
            }
            if (gc.getInput().isKeyDown(Input.KEY_LSHIFT) && gc.getInput().isKeyDown(Input.KEY_S)) {
                if (currentPosY < terrain.getTerrainRows() - 1) currentPosY++;

            }
            if (gc.getInput().isKeyDown(Input.KEY_LSHIFT) && gc.getInput().isKeyDown(Input.KEY_A)) {
                if (currentPosX > 0) currentPosX--;
            }
            if (gc.getInput().isKeyDown(Input.KEY_LSHIFT) && gc.getInput().isKeyDown(Input.KEY_D)) {
                if (currentPosX < terrain.getTerrainColumns() - 1) currentPosX++;
            }

            if (keyInput.isKeyPressed(KeyInput.Key.SPACE)) {

                String id = null;
                TerrainImage terrainImage = null;
                for (Map.Entry<String, TerrainImage> entry : terrain.getTerrainImages().entrySet()) {
                    if (entry.getValue().getX() == currentSpreadsheetPosX && entry.getValue().getY() == currentSpreadsheetPosY) {
                        id = entry.getKey();
                        terrainImage = entry.getValue();
                        break;
                    }
                }
                if (id == null) {
                    System.err.println("spritesheet tile not mapped");
                }
                else {
                    terrain.setTile(currentPosX, currentPosY, id);

                    terrain.getTiles().get(currentPosY * terrain.getTerrainColumns() + currentPosX).setImage(terrainImage);
                }



            }

        }
        else {
            if (keyInput.isKeyPressed(KeyInput.Key.W)) {
                if (currentSpreadsheetPosY > 0) currentSpreadsheetPosY--;
            }
            if (keyInput.isKeyPressed(KeyInput.Key.S)) {
                if (currentSpreadsheetPosY < terrain.getTerrainRows() - 1) currentSpreadsheetPosY++;

            }
            if (keyInput.isKeyPressed(KeyInput.Key.A)) {
                if (currentSpreadsheetPosX > 0) currentSpreadsheetPosX--;
            }
            if (keyInput.isKeyPressed(KeyInput.Key.D)) {
                if (currentSpreadsheetPosX < terrain.getTerrainColumns() - 1) currentSpreadsheetPosX++;
            }
        }

        if (keyInput.isKeyPressed(KeyInput.Key.ESC)) {
            selectMode = !selectMode;
        }

        if (keyInput.isKeyPressed(KeyInput.Key.F5)) {
            try {
                terrain.saveTerrain(terrainFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!selectMode) {
            int currentTileRectId = currentPosY * terrain.getTerrainColumns() + currentPosX;

            camera.update(gc, terrain.getTiles().get(currentTileRectId).getRect());

        }
        else {
            int scale = 4;
            Rectangle currentTileRect = new Rectangle(currentSpreadsheetPosX * terrain.getTileWidth() * scale, currentSpreadsheetPosY * terrain.getTileHeight() * scale, terrain.getTileWidth(), terrain.getTileHeight());

            camera.update(gc, currentTileRect);
        }
    }

    public void render(GameContainer gc, Graphics g) throws SlickException {
        if (!selectMode) {
            terrain.render(g, camera);


            g.setColor(Color.red);

            int currentTileRectId = currentPosY * terrain.getTerrainColumns() + currentPosX;

            Rectangle currentTileRect = terrain.getTiles().get(currentTileRectId).getRect();

            g.drawRect(currentTileRect.getX() - camera.getPosX(), currentTileRect.getY() - camera.getPosY(), currentTileRect.getWidth(), currentTileRect.getHeight());

        }
        else {



            int scale = 4;
            SpriteSheet spriteSheet = terrain.getSpriteSheet();
            for (int i = 0; i < terrain.getTilesetColumns(); i++) {
                for (int j = 0; j < terrain.getTilesetRows(); j++) {

                    Image image = spriteSheet.getSubImage(i,j);
                    float x = i * terrain.getTileWidth() * scale - camera.getPosX();
                    float y = j * terrain.getTileHeight() * scale - camera.getPosY();

                    g.setColor(Color.white);
                    g.texture(new Rectangle(x, y, terrain.getTileWidth() * scale, terrain.getTileHeight() * scale), image, true);

                }
            }

            g.setColor(Color.red);
            g.drawRect(currentSpreadsheetPosX * terrain.getTileWidth() * scale - camera.getPosX(), currentSpreadsheetPosY * terrain.getTileHeight() * scale - camera.getPosY(), terrain.getTileWidth() * scale, terrain.getTileHeight() * scale);

        }


        HUD.render(g);


    }

    public static void main(String[] args) {
        try {
            AppGameContainer appgc;
            appgc = new AppGameContainer(new MapEditor("Simple Slick Game"));
            appgc.setDisplayMode(Globals.SCREEN_WIDTH, Globals.SCREEN_HEIGHT, false);
            appgc.setVSync(true);

            appgc.start();
        }
        catch (SlickException ex) {
            Logger.getLogger(MapEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean closeRequested() {
        System.exit(0);
        return true;
    }


}