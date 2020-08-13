package com.kamilkurp.assets;

import com.kamilkurp.Globals;
import com.kamilkurp.terrain.TerrainLayout;
import com.kamilkurp.terrain.TerrainTileset;
import org.newdawn.slick.*;

public class Assets {
    public static TerrainTileset grassyTileset;
    public static TerrainTileset dungeonTileset;
    public static TerrainLayout area1Layout;
    public static TerrainLayout area2Layout;
    public static SpriteSheet skeletonSpriteSheet;
    public static SpriteSheet slashSpriteSheet;
    public static SpriteSheet betterSlashSpriteSheet;
    public static Sound stepSound;
    public static Sound gruntSound;
    public static Sound attackSound;
    public static Music townMusic;
    public static SpriteSheet itemIcons;
    public static SpriteSheet male1SpriteSheet;
    public static SpriteSheet projectileSpriteSheet;

    public static void loadAssets() throws SlickException {
        String assetsLocation = Globals.getAssetsLocation();
        grassyTileset = new TerrainTileset(16,16,12,16,4, assetsLocation + "grassy_terrain/tileset.png", assetsLocation + "grassy_terrain/terrain_passable.txt");
        dungeonTileset = new TerrainTileset(16,16,10,10,4, assetsLocation + "dungeon_terrain/tileset.png", assetsLocation + "dungeon_terrain/terrain_passable.txt");
        area1Layout = new TerrainLayout(assetsLocation + "area_layouts/area1_layout.txt");
        area2Layout = new TerrainLayout(assetsLocation + "area_layouts/area2_layout.txt");
        skeletonSpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "creature_animations/skeleton.png", 64, 64);
        slashSpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "attack_animations/slash.png", 40, 40);
        betterSlashSpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "attack_animations/slash_hd.png", 40, 40);
        stepSound = new Sound(Globals.getAssetsLocation() + "sounds/running.wav");
        gruntSound = new Sound(Globals.getAssetsLocation() + "sounds/grunt.wav");
        attackSound = new Sound(Globals.getAssetsLocation() + "sounds/swoosh.wav");
        townMusic = new Music(Globals.getAssetsLocation() + "music/town_song.wav");
        itemIcons = new SpriteSheet(Globals.getAssetsLocation() + "items/item_icons.png", 40, 40);

        male1SpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "creature_animations/male1.png", 32, 32);

        projectileSpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "projectiles/arrow.png", 40, 40);

    }
}