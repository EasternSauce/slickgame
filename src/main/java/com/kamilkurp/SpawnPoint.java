package com.kamilkurp;

import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.Enemy;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.utils.Camera;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SpawnPoint implements Renderable {
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

    public SpawnPoint(int posX, int posY, int spawnedLimit, Map<String, Creature> creatures, List<Creature> creaturesList, LootSystem lootSystem) throws SlickException {
        this.posX = posX;
        this.posY = posY;
        this.creatures = creatures;
        this.creaturesList = creaturesList;
        this.lootSystem = lootSystem;

        range = 150;

        this.spawnedLimit = spawnedLimit;

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

        Enemy enemy = new Enemy("skellie"+Math.abs(Globals.random.nextInt()), randX, randY, creatures, creaturesList, lootSystem);
        enemy.updateAttackType();

        spawnedList.add(enemy);
    }

    public void update() throws SlickException {
        int spawnedAlive = 0;

        for (Creature creature : spawnedList) {
            if (creature.getHealthPoints() > 0f) {
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
