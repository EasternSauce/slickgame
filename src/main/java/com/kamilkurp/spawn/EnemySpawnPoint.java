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

    public EnemySpawnPoint(int posX, int posY, Area area, LootSystem lootSystem) throws SlickException {
        this.posX = posX;
        this.posY = posY;

        this.lootSystem = lootSystem;

        this.area = area;

        spawn();
    }


    public void spawn() throws SlickException {
        spawnedCreature = new Skeleton("skellie"+Math.abs(Globals.random.nextInt()), posX, posY, area, lootSystem);
        spawnedCreature.updateAttackType();
    }

}
