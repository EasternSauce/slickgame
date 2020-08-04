package com.kamilkurp.assets;

import com.kamilkurp.terrain.TerrainTileset;
import org.newdawn.slick.SlickException;

public class Assets {
    public static TerrainTileset grassyTileset;
    public static TerrainTileset dungeonTileset;

    public static void loadAssets() throws SlickException {
        grassyTileset = new TerrainTileset(16,16,12,16,4, "Tilemapnew.png", "grassyTileset_passable.txt");
        dungeonTileset = new TerrainTileset(16,16,10,10,4, "tileset.png", "tileset_passable.txt");
    }
}
