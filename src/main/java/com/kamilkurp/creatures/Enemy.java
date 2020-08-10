package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.terrain.TerrainTile;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.*;

public class Enemy extends Creature {

    private Timer actionTimer;

    private int currentDirection = 0;

    private boolean stayInPlace = false;

    public Random random = new Random();

    private Map<String, Float> dropTable;

    private Timer findNewDestinationTimer;
    private Rectangle closestRect;

    private Creature aggroed;


    public Enemy(String id, int posX, int posY, Map<String, Creature> creatures, LootSystem lootSystem) throws SlickException {
        super(id, posX, posY, creatures, lootSystem);

        actionTimer = new Timer();

        dropTable = new HashMap<>();
        dropTable.put("ringmailGreaves", 0.9f);
        dropTable.put("skinTunic", 0.2f);
        dropTable.put("hideGloves", 0.1f);

        findNewDestinationTimer = new Timer();
    }

    @Override
    public void update(GameContainer gc, int i, List<TerrainTile> tiles, Collection<Creature> creatures, KeyInput keyInput) {
        super.update(gc, i ,tiles, creatures, keyInput);

        if (runningTimer.getTime() > 200) {
            running = false;
        }

        if (attackingTimer.getTime() > 300) {
            attacking = false;
        }

        if (immunityTimer.getTime() > 500) {
            immune = false;
        }
    }

    @Override
    protected void setFacingDirection(GameContainer gc) {
        if (aggroed != null) {
            facingVector = new Vector2f(aggroed.getRect().getCenterX() - rect.getCenterX(), aggroed.getRect().getCenterY() - rect.getCenterY());
            facingAngle = facingVector.getTheta();
        }
    }

    @Override
    protected void onDeath() {
        lootSystem.spawnLootPile(rect.getCenterX(), rect.getCenterY(), dropTable);
    }

    @Override
    public void performActions(GameContainer gc, Collection<Creature> creatures, KeyInput keyInput) {

        int aggroDistance = 200;
        aggroed = null;
        for (Creature creature : creatures) {
            if (creature instanceof Enemy) continue;
            if (Globals.distance(creature.rect, rect) < aggroDistance && creature.healthPoints > 0) {
                aggroed = creature;
                break;
            }
        }

        if (actionTimer.getTime() > 1500) {
            currentDirection = Math.abs(random.nextInt())%4;
            stayInPlace = Math.abs(random.nextInt()) % 10 < 8;
            actionTimer.reset();
        }

        if (aggroed == null) {
            if (!stayInPlace) {
                if (currentDirection == 0) {
                    moveUp();
                }
                if (currentDirection == 1) {
                    moveLeft();
                }
                if (currentDirection == 2) {
                    moveDown();
                }
                if (currentDirection == 3) {
                    moveRight();
                }
            }
        }
        else {
            float walkUpDistance = 70f;

            float shortestDist = Float.MAX_VALUE;


            int[] t1 = {0,1,0,-1};
            int[] t2 = {1,0,-1,0};

            if (findNewDestinationTimer.getTime() > 300f) {
                for (int i = 0; i < 4; i++) {
                    Rectangle rect = new Rectangle(aggroed.rect.getCenterX() + t1[i] * walkUpDistance, aggroed.rect.getCenterY() + t2[i] * walkUpDistance, 1, 1);

                    float dist = Globals.distance(this.rect, rect);

                    if (dist < shortestDist) {
                        shortestDist = dist;
                        closestRect = rect;
                    }
                }
                findNewDestinationTimer.reset();
            }

            if (closestRect != null) {
                goTo(closestRect.getCenterX(), closestRect.getCenterY());
            }


            float attackDistance = 100f;
            if (Globals.distance(aggroed.rect, rect) < attackDistance) {
                float maxDist = 0.0f;
                int dir = 0;
                if (rect.getCenterX() < aggroed.rect.getCenterX()) {
                    float dist = Math.abs(rect.getCenterX() - aggroed.rect.getCenterX());
                    if (dist > maxDist) {
                        maxDist = dist;
                        dir = 3;
                    }
                }
                if (rect.getCenterX() > aggroed.rect.getCenterX()) {
                    float dist = Math.abs(rect.getCenterX() - aggroed.rect.getCenterX());
                    if (dist > maxDist) {
                        maxDist = dist;
                        dir = 1;
                    }
                }

                if (rect.getCenterY() < aggroed.rect.getCenterY()) {
                    float dist = Math.abs(rect.getCenterY() - aggroed.rect.getCenterY());
                    if (dist > maxDist) {
                        maxDist = dist;
                        dir = 2;
                    }
                }
                if (rect.getCenterY() > aggroed.rect.getCenterY()) {
                    float dist = Math.abs(rect.getCenterY() - aggroed.rect.getCenterY());
                    if (dist > maxDist) {
                        maxDist = dist;
                        dir = 0;

                    }
                }
                direction = dir;
                attack();
            }

        }




    }

    void goTo(float gotoPosX, float gotoPosY) {
        if (rect.getCenterX() < gotoPosX - 5f) {
            moveRight();
        }
        else if (rect.getCenterX() > gotoPosX + 5f) {
            moveLeft();
        }

        if (rect.getCenterY() < gotoPosY - 5f) {
            moveDown();
        }
        else if (rect.getCenterY() > gotoPosY + 5f) {
            moveUp();
        }


        float distX = Math.abs(rect.getCenterX() - gotoPosX);
        float distY = Math.abs(rect.getCenterY() - gotoPosY);
        if (distX - distY < 20f) {
            if (rect.getCenterX() < gotoPosX) {
                direction = 3;
            }
            else {
                direction = 1;
            }
        }
    }
}
