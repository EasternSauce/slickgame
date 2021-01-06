package com.kamilkurp.spawn;

import com.kamilkurp.Globals;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.utils.Camera;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EnemyRespawnArea {
    private final int posX;
    private final int posY;
    private final List<Creature> spawnedList;
    private final int range;
    private Map<String, Creature> creatures;
    private List<Creature> creaturesList;
    private final int spawnedLimit;
    private final int spawnTimeout;
    private final Timer spawnTimer;
    private boolean spawning;
    private final Area area;

    private GameSystem gameSystem;

    private String creatureType;

    public EnemyRespawnArea(GameSystem gameSystem, int posX, int posY, int spawnedLimit, Area area, String creatureType) throws SlickException {
        this.posX = posX;
        this.posY = posY;
        this.gameSystem = gameSystem;
        this.creatureType = creatureType;

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

        Creature spawnedCreature = null;
//        if (creatureType.equals("skeletonSword")) {
//            spawnedCreature = new Skeleton(gameSystem, "skellie"+Math.abs(Globals.random.nextInt()), "woodenSword");
//        }
//
//        if (creatureType.equals("skeletonCrossbow")) {
//            spawnedCreature = new Skeleton(gameSystem, "skellie"+Math.abs(Globals.random.nextInt()), "crossbow");
//        }
//
//        if (creatureType.equals("wolf")) {
//            spawnedCreature = new Wolf(gameSystem, "wolfie"+Math.abs(Globals.random.nextInt()));
//        }
//
//        if (creatureType.equals("fireDemon")) {
//            spawnedCreature = new FireDemon(gameSystem, "firedemon"+Math.abs(Globals.random.nextInt()), "demonTrident");
//        }

        area.moveInCreature(spawnedCreature, randX, randY);
        spawnedCreature.updateAttackType();

        spawnedList.add(spawnedCreature);
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
                if (spawnTimer.getElapsed() > spawnTimeout) {
                    spawning = false;
                    spawn();
                }
            }
        }
    }

    public void render(Graphics g, Camera camera) {
        g.setColor(Color.white);
        g.drawRect(posX - range - camera.getPosX(), posY - range - camera.getPosY(), range*2, range*2);
    }
}
