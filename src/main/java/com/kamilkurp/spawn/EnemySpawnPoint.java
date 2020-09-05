package com.kamilkurp.spawn;

import com.kamilkurp.Globals;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.Skeleton;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.terrain.Area;
import org.newdawn.slick.SlickException;

import java.util.List;
import java.util.Map;

public class EnemySpawnPoint {
    private int posX;
    private int posY;
    private Creature spawnedCreature;

    private Map<String, Creature> creatures;
    private List<Creature> creaturesList;
    private LootSystem lootSystem;

    private Area area;

    private boolean isToBeRespawned = false;

    public EnemySpawnPoint(int posX, int posY, Area area, LootSystem lootSystem) throws SlickException {
        this.posX = posX;
        this.posY = posY;

        this.lootSystem = lootSystem;

        this.area = area;

        markForRespawn();
    }


    public void update() throws SlickException {
        if (isToBeRespawned) {
            if (spawnedCreature != null) {
                spawnedCreature.kill();
            }

            spawnedCreature = new Skeleton("skellie"+Math.abs(Globals.random.nextInt()), posX, posY, area, lootSystem);
            spawnedCreature.updateAttackType();
            spawnedCreature.setAreaToMoveTo(area);

            isToBeRespawned = false;
        }


    }

    public void markForRespawn() {
        isToBeRespawned = true;
    }

}
