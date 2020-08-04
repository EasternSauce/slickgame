package com.kamilkurp.assets;

import com.kamilkurp.Globals;
import com.kamilkurp.terrain.TerrainLayout;
import com.kamilkurp.terrain.TerrainTileset;
import org.newdawn.slick.SlickException;

public class Assets {
    public static TerrainTileset grassyTileset;
    public static TerrainTileset dungeonTileset;
    public static TerrainLayout area1Layout;
    public static TerrainLayout area2Layout;


    public static void loadAssets() throws SlickException {
        String assetsLocation = Globals.getAssetsLocation();
        grassyTileset = new TerrainTileset(16,16,12,16,4, assetsLocation + "grassy_terrain/tileset.png", assetsLocation + "grassy_terrain/terrain_passable.txt");
        dungeonTileset = new TerrainTileset(16,16,10,10,4, assetsLocation + "dungeon_terrain/tileset.png", assetsLocation + "dungeon_terrain/terrain_passable.txt");
        area1Layout = new TerrainLayout(assetsLocation + "area_layouts/area1_layout.txt");
        area2Layout = new TerrainLayout(assetsLocation + "area_layouts/area2_layout.txt");
    }
}
