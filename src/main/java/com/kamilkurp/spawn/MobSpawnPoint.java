package com.kamilkurp.spawn;

import com.kamilkurp.Globals;
import com.kamilkurp.creatures.*;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.terrain.Area;
import org.newdawn.slick.SlickException;

import java.util.List;
import java.util.Map;

public class MobSpawnPoint {
    private final float posX;
    private final float posY;
    private Creature spawnedCreature;

    private Map<String, Creature> creatures;
    private List<Creature> creaturesList;

    private final Area area;

    private boolean isToBeRespawned = false;

    private final String creatureType;

    private GameSystem gameSystem;


    private Blockade blockade;

    public MobSpawnPoint(GameSystem gameSystem, int posX, int posY, Area area, String creatureType) {
        this.gameSystem = gameSystem;

        this.posX = posX;
        this.posY = posY;

        this.area = area;

        this.creatureType = creatureType;

        markForRespawn();
    }


    public void update() throws SlickException {
        if (isToBeRespawned) {
            if (spawnedCreature != null) {
                spawnedCreature.kill();
            }

            if (creatureType.equals("skeletonSword")) {
                spawnedCreature = new Skeleton(gameSystem, this, "skellie"+Math.abs(Globals.random.nextInt()), "woodenSword");
                area.moveInCreature(spawnedCreature, posX, posY);
            }

            if (creatureType.equals("skeletonCrossbow")) {
                spawnedCreature = new Skeleton(gameSystem, this, "skellie"+Math.abs(Globals.random.nextInt()), "crossbow");
                area.moveInCreature(spawnedCreature, posX, posY);
            }

            if (creatureType.equals("wolf")) {
                spawnedCreature = new Wolf(gameSystem, this, "wolfie"+Math.abs(Globals.random.nextInt()));
                area.moveInCreature(spawnedCreature, posX, posY);
            }

            if (creatureType.equals("ghost")) {
                spawnedCreature = new Ghost(gameSystem, this, "ghost"+Math.abs(Globals.random.nextInt()), "woodenSword");
                area.moveInCreature(spawnedCreature, posX, posY);
            }

            if (creatureType.equals("goblin")) {
                spawnedCreature = new Goblin(gameSystem, this, "goblin"+Math.abs(Globals.random.nextInt()), "poisonDagger");
                area.moveInCreature(spawnedCreature, posX, posY);
            }

            if (creatureType.equals("fireDemon")) {
                spawnedCreature = new FireDemon(gameSystem, this, "firedemon"+Math.abs(Globals.random.nextInt()), "demonTrident");
                area.moveInCreature(spawnedCreature, posX, posY);
            }

            spawnedCreature.setStartingPosX(posX);
            spawnedCreature.setStartingPosY(posY);

            spawnedCreature.moveToArea(area, posX, posY);

            spawnedCreature.onInit();

            isToBeRespawned = false;
        }


    }

    public void markForRespawn() {
        isToBeRespawned = true;
    }

    public Blockade getBlockade() {
        return blockade;
    }

    public void setBlockade(Blockade blockade) {
        this.blockade = blockade;
    }
}
