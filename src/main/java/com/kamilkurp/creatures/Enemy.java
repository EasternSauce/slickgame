package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.utils.Timer;
import com.kamilkurp.terrain.TerrainTile;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import java.util.*;

public class Enemy extends Creature {

    private Timer actionTimer;

    private int currentDirection = 0;

    private boolean stayInPlace = false;

    public Random random = new Random();

    private Map<String, Float> dropTable;

    public Enemy(String id, int posX, int posY, Map<String, Creature> creatures, LootSystem lootSystem) throws SlickException {
        super(id, posX, posY, creatures, lootSystem);

        actionTimer = new Timer();

        dropTable = new HashMap<>();
        dropTable.put("ringmailGreaves", 0.9f);
        dropTable.put("skinTunic", 0.2f);
        dropTable.put("hideGloves", 0.1f);
    }

    @Override
    public void update(GameContainer gc, int i, List<TerrainTile> tiles, Collection<Creature> creatures, KeyInput keyInput) {
        super.update(gc, i ,tiles, creatures, keyInput);

        if (runningTimer.getTime() > 200) {
            running = false;
        }

        if (attackingTimer.getTime() > 200) {
            attacking = false;
        }

        if (immunityTimer.getTime() > 500) {
            immune = false;
        }
    }

    @Override
    protected void onDeath() {
        lootSystem.spawn(rect.getCenterX(), rect.getCenterY(), dropTable);
    }

    @Override
    public void performActions(GameContainer gc, Collection<Creature> creatures, KeyInput keyInput) {

        Creature aggroed = null;
        for (Creature creature : creatures) {
            if (creature instanceof Enemy) continue;
            if (Globals.distance(creature.rect, rect) < 130 && creature.healthPoints > 0) {
                aggroed = creature;
                break;
            }
        }

        int shortestI = 0;
        int shortestJ = 0;

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
            Rectangle closestRect = null;


            int[] t1 = {0,1,0,-1};
            int[] t2 = {1,0,-1,0};

            for(int i = 0; i < 4; i++) {
                //System.out.println(i + " " + j);
                Rectangle rect = new Rectangle(aggroed.rect.getCenterX() + t1[i] * walkUpDistance, aggroed.rect.getCenterY() + t2[i] * walkUpDistance, 1, 1);

                float dist = Globals.distance(this.rect, rect);

                if (dist < shortestDist) {
                    shortestDist = dist;
                    closestRect = rect;
                    shortestI = t1[i];
                    shortestJ = t2[i];
                }
            }



            //System.out.println(shortestI + " " + shortestJ);




            goTo(closestRect.getCenterX(), closestRect.getCenterY());




        }

        //System.out.println(actionTimer.getTime());

        if (actionTimer.getTime() > 1500  ) {

            //System.out.println("curr dir is" + currentDirection);

//            System.out.println(currentDirection);
            if (aggroed != null && Globals.distance(aggroed.rect, rect) < 100f) {
                if (shortestI == 0 && shortestJ == 1) {
                    moveUp();
                }
                if (shortestI == 1 && shortestJ == 0) {
                    moveLeft();
                }
                if (shortestI == 0 && shortestJ == -1) {
                    moveDown();
                }
                if (shortestI == -1 && shortestJ == 0) {
                    moveRight();
                }
                attack();
            }
            else {
                currentDirection = Math.abs(random.nextInt())%4;
                stayInPlace = Math.abs(random.nextInt()) % 10 < 8;
            }
            actionTimer.reset();
        }
    }

    void goTo(float gotoPosX, float gotoPosY) {
        if (Globals.distanceX(gotoPosX, rect.getCenterX()) > Globals.distanceY(gotoPosY, rect.getCenterY())) {
            if (rect.getCenterX() < gotoPosX - 5f) {
                moveRight();
            }
            else if (rect.getCenterX() > gotoPosX + 5f) {
                moveLeft();
            }
        }
        else {
            if (rect.getCenterY() < gotoPosY - 5f) {
                moveDown();
            }
            else if (rect.getCenterY() > gotoPosY + 5f) {
                moveUp();
            }
        }
    }
}
