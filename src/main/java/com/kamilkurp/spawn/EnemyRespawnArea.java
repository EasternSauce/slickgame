package com.kamilkurp.spawn;

import com.kamilkurp.Globals;
import com.kamilkurp.Renderable;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.Skeleton;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.utils.Camera;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EnemyRespawnArea implements Renderable {
    private int posX;
    private int posY;
    private List<Creature> spawnedList;
    private int range;
    private Map<String, Creature> creatures;
    private List<Creature> creaturesList;
    private LootSystem lootSystem;
    private int spawnedLimit;
    private int spawnTimeout;
    private Timer spawnTimer;
    private boolean spawning;
    private Area area;

    public EnemyRespawnArea(int posX, int posY, int spawnedLimit, Area area, LootSystem lootSystem) throws SlickException {
        this.posX = posX;
        this.posY = posY;
        this.lootSystem = lootSystem;

        range = 150;

        this.spawnedLimit = spawnedLimit;

        this.area = area;

        spawnedList = new LinkedList<>();
        spawnTimeout = 5000;
        spawnTimer = new Timer();

        spawning = false;

        for (int i = 0; i < spawnedLimit; i++) {
            spawn();
        }
    }

    public void spawn() throws SlickException {
        int randX = Globals.randInt(posX - range, posX + range);
        int randY = Globals.randInt(posY - range, posY + range);

        Skeleton enemy = new Skeleton("skellie"+Math.abs(Globals.random.nextInt()), randX, randY, area, lootSystem);
        enemy.updateAttackType();

        spawnedList.add(enemy);
    }

    public void update() throws SlickException {
        int spawnedAlive = 0;

        for (Creature creature : spawnedList) {
            if (creature.isAlive()) {
                spawnedAlive++;
            }
        }

        if (spawnedAlive < spawnedLimit) {
            if (!spawning) {
                spawning = true;
                spawnTimer.reset();
            }
            else {
                if (spawnTimer.getTime() > spawnTimeout) {
                    spawning = false;
                    spawn();
                }
            }
        }
    }


    @Override
    public void render(Graphics g, Camera camera) {
        g.setColor(Color.white);
        g.drawRect(posX - range - camera.getPosX(), posY - range - camera.getPosY(), range*2, range*2);
    }
}
