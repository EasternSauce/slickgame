package com.kamilkurp.spawn;

import com.kamilkurp.Globals;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.Skeleton;
import com.kamilkurp.creatures.Wolf;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.terrain.Area;
import org.newdawn.slick.Game;
import org.newdawn.slick.SlickException;

import java.util.List;
import java.util.Map;

public class EnemySpawnPoint {
    private final float posX;
    private final float posY;
    private Creature spawnedCreature;

    private Map<String, Creature> creatures;
    private List<Creature> creaturesList;

    private final Area area;

    private boolean isToBeRespawned = false;

    private final String creatureType;

    private GameSystem gameSystem;

    public EnemySpawnPoint(GameSystem gameSystem, int posX, int posY, Area area, String creatureType) {
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

            if (creatureType.equals("skeleton")) {
                spawnedCreature = new Skeleton(gameSystem, "skellie"+Math.abs(Globals.random.nextInt()), posX, posY, area);
            }

            if (creatureType.equals("wolf")) {
                spawnedCreature = new Wolf(gameSystem, "wolfie"+Math.abs(Globals.random.nextInt()), posX, posY, area);
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
