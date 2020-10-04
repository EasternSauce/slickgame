package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.areagate.AreaGate;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.projectile.Arrow;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.terrain.TerrainTile;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class Mob extends Creature {

    protected Timer actionTimer;

    protected int currentDirection = 0;

    protected boolean stayInPlace = false;

    public Random random = new Random();

    protected Map<String, Float> dropTable;

    protected Timer findNewDestinationTimer;

    protected Creature aggroed;


    protected float destinationX;
    protected float destinationY;
    protected boolean hasDestination;

    public Mob(String id, int posX, int posY, Area area, LootSystem lootSystem) throws SlickException {
        super(id, posX, posY, area, lootSystem);
    }

    @Override
    public void update(GameContainer gc, int i, List<TerrainTile> tiles, Map<String, Creature> creatures, KeyInput keyInput, List<Arrow> arrowList, List<AreaGate> gatesList) throws SlickException {
        super.update(gc, i ,tiles, creatures, keyInput, arrowList, gatesList);

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
    public void onDeath() {
        lootSystem.spawnLootPile(rect.getCenterX(), rect.getCenterY(), dropTable);
    }

    @Override
    public void performActions(GameContainer gc, Map<String, Creature> creatures, KeyInput keyInput, List<Arrow> arrowList, List<TerrainTile> tiles) {

        int aggroDistance = 400;
        aggroed = null;
        for (Creature creature : creatures.values()) {
            if (creature instanceof Mob) continue;
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
            float walkUpDistance = 0f;

            float attackDistance = 0f;
            if (currentAttackType == AttackType.UNARMED) {
                attackDistance = 100f;
                walkUpDistance = 100f;
            } else
            if (currentAttackType == AttackType.SWORD) {
                attackDistance = 100f;
                walkUpDistance = 100f;
            }
            else if (currentAttackType == AttackType.BOW) {
                attackDistance = 300f;
                walkUpDistance = 300f;
            }

            if (findNewDestinationTimer.getTime() > 300f) {

                float dist = Globals.distance(this.rect, aggroed.rect);

                if (dist > walkUpDistance) {
                    destinationX = aggroed.rect.getCenterX();
                    destinationY = aggroed.rect.getCenterY();
                    hasDestination = true;
                }
                else {
                    hasDestination = false;
                }

                findNewDestinationTimer.reset();
            }

            if (hasDestination) {
                goTo(destinationX, destinationY);
            }

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

                attack(arrowList, tiles, creatures);
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