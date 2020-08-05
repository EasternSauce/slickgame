package com.kamilkurp.assets;

import com.kamilkurp.Globals;
import com.kamilkurp.terrain.TerrainLayout;
import com.kamilkurp.terrain.TerrainTileset;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class Assets {
    public static TerrainTileset grassyTileset;
    public static TerrainTileset dungeonTileset;
    public static TerrainLayout area1Layout;
    public static TerrainLayout area2Layout;
    public static SpriteSheet skeletonSpriteSheet;
    public static SpriteSheet slashSpriteSheet;


    public static void loadAssets() throws SlickException {
        String assetsLocation = Globals.getAssetsLocation();
        grassyTileset = new TerrainTileset(16,16,12,16,4, assetsLocation + "grassy_terrain/tileset.png", assetsLocation + "grassy_terrain/terrain_passable.txt");
        dungeonTileset = new TerrainTileset(16,16,10,10,4, assetsLocation + "dungeon_terrain/tileset.png", assetsLocation + "dungeon_terrain/terrain_passable.txt");
        area1Layout = new TerrainLayout(assetsLocation + "area_layouts/area1_layout.txt");
        area2Layout = new TerrainLayout(assetsLocation + "area_layouts/area2_layout.txt");
        skeletonSpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "creature_animations/skeleton.png", 64, 64);
        slashSpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "attack_animations/slash.png", 40, 40);
    }
}
