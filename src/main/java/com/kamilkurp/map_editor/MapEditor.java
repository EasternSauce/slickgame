package com.kamilkurp.map_editor;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.gui.Hud;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.terrain.TerrainImage;
import com.kamilkurp.terrain.TerrainTileset;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MapEditor extends BasicGame {

    private Area area;

    private Camera camera;

    private Hud HUD;


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

        Assets.loadAssets();

        String areaId = "area1";

        area = new Area(areaId, Assets.grassyTileset, Assets.area1Layout, Assets.area1Enemies, null);

        terrainFile = "assets/areas/" + areaId + "/layout.txt";

        camera = new Camera();

        HUD = new Hud();

        keyInput = new KeyInput();

        currentPosX = 0;
        currentPosY = 0;

        currentSpreadsheetPosX = 0;
        currentSpreadsheetPosY = 0;

    }

    @Override
    public void update(GameContainer gc, int i) {
        Globals.updateTimers(i);

        keyInput.readKeyPresses(gc.getInput());

        if (!selectMode) {
            if (keyInput.isKeyPressed(KeyInput.Key.W)) {
                if (currentPosY > 0) currentPosY--;
            }
            if (keyInput.isKeyPressed(KeyInput.Key.S)) {
                if (currentPosY < area.getTerrainRows() - 1) currentPosY++;

            }
            if (keyInput.isKeyPressed(KeyInput.Key.A)) {
                if (currentPosX > 0) currentPosX--;
            }
            if (keyInput.isKeyPressed(KeyInput.Key.D)) {
                if (currentPosX < area.getTerrainColumns() - 1) currentPosX++;
            }

            if (gc.getInput().isKeyDown(Input.KEY_LSHIFT) && gc.getInput().isKeyDown(Input.KEY_W)) {
                if (currentPosY > 0) currentPosY--;
            }
            if (gc.getInput().isKeyDown(Input.KEY_LSHIFT) && gc.getInput().isKeyDown(Input.KEY_S)) {
                if (currentPosY < area.getTerrainRows() - 1) currentPosY++;

            }
            if (gc.getInput().isKeyDown(Input.KEY_LSHIFT) && gc.getInput().isKeyDown(Input.KEY_A)) {
                if (currentPosX > 0) currentPosX--;
            }
            if (gc.getInput().isKeyDown(Input.KEY_LSHIFT) && gc.getInput().isKeyDown(Input.KEY_D)) {
                if (currentPosX < area.getTerrainColumns() - 1) currentPosX++;
            }

            if (keyInput.isKeyPressed(KeyInput.Key.SPACE)) {

                String id = null;
                TerrainImage terrainImage = null;
                for (Map.Entry<String, TerrainImage> entry : area.getTerrainTileset().getTerrainImages().entrySet()) {
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
                    area.setTile(currentPosX, currentPosY, id);

                    area.getTiles().get(currentPosY * area.getTerrainColumns() + currentPosX).setImage(terrainImage);
                }



            }

        }
        else {
            if (keyInput.isKeyPressed(KeyInput.Key.W)) {
                if (currentSpreadsheetPosY > 0) currentSpreadsheetPosY--;
            }
            if (keyInput.isKeyPressed(KeyInput.Key.S)) {
                if (currentSpreadsheetPosY < area.getTerrainRows() - 1) currentSpreadsheetPosY++;

            }
            if (keyInput.isKeyPressed(KeyInput.Key.A)) {
                if (currentSpreadsheetPosX > 0) currentSpreadsheetPosX--;
            }
            if (keyInput.isKeyPressed(KeyInput.Key.D)) {
                if (currentSpreadsheetPosX < area.getTerrainColumns() - 1) currentSpreadsheetPosX++;
            }
        }

        if (keyInput.isKeyPressed(KeyInput.Key.ESC)) {
            selectMode = !selectMode;
        }

        if (keyInput.isKeyPressed(KeyInput.Key.F5)) {
            try {
                area.saveTerrainLayoutToFile(terrainFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!selectMode) {
            int currentTileRectId = currentPosY * area.getTerrainColumns() + currentPosX;

            camera.update(gc, area.getTiles().get(currentTileRectId).getRect());

        }
        else {
            int scale = 4;
            Rectangle currentTileRect = new Rectangle(currentSpreadsheetPosX * area.getTerrainTileset().getTileWidth() * scale, currentSpreadsheetPosY * area.getTerrainTileset().getTileHeight() * scale, area.getTerrainTileset().getTileWidth(), area.getTerrainTileset().getTileHeight());

            camera.update(gc, currentTileRect);
        }
    }

    public void render(GameContainer gc, Graphics g) {
        if (!selectMode) {
            area.render(g, camera);


            g.setColor(Color.red);

            int currentTileRectId = currentPosY * area.getTerrainColumns() + currentPosX;

            Rectangle currentTileRect = area.getTiles().get(currentTileRectId).getRect();

            g.drawRect(currentTileRect.getX() - camera.getPosX(), currentTileRect.getY() - camera.getPosY(), currentTileRect.getWidth(), currentTileRect.getHeight());

        }
        else {



            int scale = 4;

            TerrainTileset terrainTileset = area.getTerrainTileset();

            SpriteSheet spriteSheet = terrainTileset.getSpriteSheet();
            for (int i = 0; i < terrainTileset.getTilesetColumns(); i++) {
                for (int j = 0; j < terrainTileset.getTilesetRows(); j++) {

                    Image image = spriteSheet.getSubImage(i,j);
                    float x = i * terrainTileset.getTileWidth() * scale - camera.getPosX();
                    float y = j * terrainTileset.getTileHeight() * scale - camera.getPosY();

                    g.setColor(Color.white);
                    g.texture(new Rectangle(x, y, terrainTileset.getTileWidth() * scale, terrainTileset.getTileHeight() * scale), image, true);

                }
            }

            g.setColor(Color.red);
            g.drawRect(currentSpreadsheetPosX * terrainTileset.getTileWidth() * scale - camera.getPosX(), currentSpreadsheetPosY * terrainTileset.getTileHeight() * scale - camera.getPosY(), terrainTileset.getTileWidth() * scale, terrainTileset.getTileHeight() * scale);

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