package com.kamilkurp.assets;

import com.kamilkurp.Globals;
import com.kamilkurp.spawn.SpawnLocationsContainer;
import com.kamilkurp.terrain.TerrainLayout;
import com.kamilkurp.terrain.TerrainTileset;
import org.newdawn.slick.Image;
import org.newdawn.slick.*;

import java.awt.Font;

public class Assets {
    public static TerrainTileset grassyTileset;
    public static TerrainTileset dungeonTileset;
    public static TerrainLayout area1Layout;
    public static TerrainLayout area2Layout;
    public static SpriteSheet skeletonSpriteSheet;
    public static SpriteSheet wolfSpriteSheet;
    public static SpriteSheet ghostSpriteSheet;
    public static SpriteSheet goblinSpriteSheet;
    public static SpriteSheet slashSpriteSheet;
    public static SpriteSheet explosionSpriteSheet;
    public static SpriteSheet betterSlashSpriteSheet;
    public static SpriteSheet slashWindupSpriteSheet;
    public static SpriteSheet tridentThrustSpriteSheet;
    public static SpriteSheet tridentThrustWindupSpriteSheet;
    public static SpriteSheet fireDemonSpriteSheet;
    public static SpriteSheet explosionWindupSpriteSheet;
    public static SpriteSheet fistSlamSpriteSheet;
    public static SpriteSheet fistSlamWindupSpriteSheet;

    public static Sound stepSound;
    public static Sound gruntSound;
    public static Sound attackSound;
    public static Sound dogBarkSound;
    public static Sound dogWhimperSound;
    public static Sound punchSound;
    public static Sound strongPunchSound;
    public static Sound explosionSound;
    public static Sound bowPullSound;
    public static Sound bowReleaseSound;
    public static Sound boneClickSound;
    public static Sound flybySound;
    public static Sound darkLaughSound;
    public static Sound glassBreakSound;
    public static Sound bloodSquirtSound;
    public static Sound monsterGrowlSound;

    public static Music townMusic;
    public static Music abandonedPlains;
    public static Music fireDemon;
    public static SpriteSheet itemIcons;
    public static SpriteSheet male1SpriteSheet;
    public static SpriteSheet male2SpriteSheet;
    public static SpriteSheet female1SpriteSheet;
    public static SpriteSheet projectileSpriteSheet;
    public static SpriteSheet niceItemIcons;
    public static Font verdanaFont;
    public static TrueTypeFont verdanaTtf;
    public static Font verdanaHugeFont;
    public static TrueTypeFont verdanaHugeTtf;

    public static Sound arrowWhizzSound;
    public static Sound boneCrushSound;
    public static Sound painSound;
    public static Sound roarSound;
    public static Sound evilYellingSound;

    public static SpawnLocationsContainer area1Enemies;
    public static SpawnLocationsContainer area2Enemies;

    public static Image cursor;

    public static void loadAssets() throws SlickException {
        String assetsLocation = Globals.getAssetsLocation();
        grassyTileset = new TerrainTileset(16,16,12,16,4, assetsLocation + "grassy_terrain/tileset.png", assetsLocation + "grassy_terrain/terrain_passable.txt");
        dungeonTileset = new TerrainTileset(16,16,10,10,4, assetsLocation + "dungeon_terrain/tileset.png", assetsLocation + "dungeon_terrain/terrain_passable.txt");
        area1Layout = new TerrainLayout(assetsLocation + "areas/area1/layout.txt");
        area1Enemies = new SpawnLocationsContainer(assetsLocation + "areas/area1/spawns.txt");
        area2Layout = new TerrainLayout(assetsLocation + "areas/area2/layout.txt");
        area2Enemies = new SpawnLocationsContainer(assetsLocation + "areas/area2/spawns.txt");
        skeletonSpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "creature_animations/skeleton.png", 64, 64);
        wolfSpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "creature_animations/wolf.png", 50, 35);
        ghostSpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "creature_animations/ghost.png", 32, 32);
        goblinSpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "creature_animations/goblin.png", 32, 32);
        slashSpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "attack_animations/slash.png", 40, 40);
        explosionSpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "attack_animations/explosion.png", 64, 64);
        explosionWindupSpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "attack_animations/explosion_windup.png", 64, 64);
        betterSlashSpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "attack_animations/slash_hd.png", 40, 40);
        slashWindupSpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "attack_animations/slash_windup.png", 40, 40);
        tridentThrustSpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "attack_animations/trident_thrust.png", 64, 32);
        tridentThrustWindupSpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "attack_animations/trident_thrust_windup.png", 64, 32);
        fireDemonSpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "creature_animations/taurus.png", 80, 80);
        fistSlamSpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "attack_animations/fist_slam.png", 40, 160);
        fistSlamWindupSpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "attack_animations/fist_slam_windup.png", 40, 160);

        stepSound = new Sound(Globals.getAssetsLocation() + "sounds/running.wav");
        gruntSound = new Sound(Globals.getAssetsLocation() + "sounds/grunt.wav");
        attackSound = new Sound(Globals.getAssetsLocation() + "sounds/swoosh.wav");
        dogBarkSound = new Sound(Globals.getAssetsLocation() + "sounds/dogbark.wav");
        dogWhimperSound = new Sound(Globals.getAssetsLocation() + "sounds/dogwhine.wav");
        townMusic = new Music(Globals.getAssetsLocation() + "music/town_song.wav");
        abandonedPlains = new Music(Globals.getAssetsLocation() + "music/abandoned_plains.wav");
        fireDemon = new Music(Globals.getAssetsLocation() + "music/fire_demon.wav");
        itemIcons = new SpriteSheet(Globals.getAssetsLocation() + "items/item_icons.png", 40, 40);
        arrowWhizzSound = new Sound(Globals.getAssetsLocation() + "sounds/arrow-whizz.wav");
        punchSound = new Sound(Globals.getAssetsLocation() + "sounds/punch.wav");
        strongPunchSound = new Sound(Globals.getAssetsLocation() + "sounds/strong-punch.wav");
        explosionSound = new Sound(Globals.getAssetsLocation() + "sounds/explosion.wav");
        boneCrushSound = new Sound(Globals.getAssetsLocation() + "sounds/bone-crush.wav");
        bowPullSound = new Sound(Globals.getAssetsLocation() + "sounds/bow-pull.wav");
        bowReleaseSound = new Sound(Globals.getAssetsLocation() + "sounds/bow-release.wav");
        boneClickSound = new Sound(Globals.getAssetsLocation() + "sounds/bone-click.wav");
        flybySound = new Sound(Globals.getAssetsLocation() + "sounds/flyby.wav");
        darkLaughSound = new Sound(Globals.getAssetsLocation() + "sounds/dark-laugh.wav");
        painSound = new Sound(Globals.getAssetsLocation() + "sounds/pain.wav");
        roarSound = new Sound(Globals.getAssetsLocation() + "sounds/roar.wav");
        evilYellingSound = new Sound(Globals.getAssetsLocation() + "sounds/evil-yelling.wav");
        glassBreakSound = new Sound(Globals.getAssetsLocation() + "sounds/glass-break.wav");
        bloodSquirtSound = new Sound(Globals.getAssetsLocation() + "sounds/blood-squirt.wav");
        monsterGrowlSound = new Sound(Globals.getAssetsLocation() + "sounds/monster-growl.wav");

        male1SpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "creature_animations/male1.png", 32, 32);
        male2SpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "creature_animations/male2.png", 32, 32);
        female1SpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "creature_animations/female1.png", 32, 32);


        projectileSpriteSheet = new SpriteSheet(Globals.getAssetsLocation() + "projectiles/arrow.png", 40, 40);

        niceItemIcons = new SpriteSheet(Globals.getAssetsLocation() + "items/nice_icons.png", 32, 32);

        verdanaFont = new Font("Verdana", Font.BOLD, 16);
        verdanaTtf = new TrueTypeFont(verdanaFont, true);
        verdanaHugeFont = new Font("Verdana", Font.BOLD, 46);
        verdanaHugeTtf = new TrueTypeFont(verdanaHugeFont, true);

        cursor = new Image(Globals.getAssetsLocation() + "cursor/cursor.png");

    }
}
