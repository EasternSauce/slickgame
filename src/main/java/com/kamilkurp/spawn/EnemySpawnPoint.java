package com.kamilkurp.spawn;

import com.kamilkurp.Globals;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.Skeleton;
import com.kamilkurp.creatures.Wolf;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.terrain.Area;
import org.newdawn.slick.SlickException;

import java.util.List;
import java.util.Map;

public class EnemySpawnPoint {
    private final float posX;
    private final float posY;
    private Creature spawnedCreature;

    private Map<String, Creature> creatures;
    private List<Creature> creaturesList;
    private final LootSystem lootSystem;

    private final Area area;

    private boolean isToBeRespawned = false;

    private final String creatureType;

    public EnemySpawnPoint(int posX, int posY, Area area, LootSystem lootSystem, String creatureType) {
        this.posX = posX;
        this.posY = posY;

        this.lootSystem = lootSystem;

        this.area = area;

        this.creatureType = creatureType;

        markForRespawn();
    }


    public void update() throws SlickException {
        if (isToBeRespawned) {
            if (spawnedCreature != null) {
                spawnedCreature.kill();
            }

            if (creatureType.equals("skeleton")) {
                spawnedCreature = new Skeleton("skellie"+Math.abs(Globals.random.nextInt()), posX, posY, area, lootSystem);
            }

            if (creatureType.equals("wolf")) {
                spawnedCreature = new Wolf("wolfie"+Math.abs(Globals.random.nextInt()), posX, posY, area, lootSystem);
            }

            spawnedCreature.updateAttackType();
            spawnedCreature.transport(area, posX, posY);

            isToBeRespawned = false;
        }


    }

    public void markForRespawn() {
        isToBeRespawned = true;
    }

}
