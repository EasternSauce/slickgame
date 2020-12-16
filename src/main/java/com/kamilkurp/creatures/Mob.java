package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.abilities.Ability;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Vector2f;

import java.util.Map;
import java.util.Random;

public abstract class Mob extends Creature {

    protected Timer actionTimer;

    protected int currentDirection = 0;

    protected boolean stayInPlace = false;

    public Random random = Globals.random;

    protected Map<String, Float> dropTable;

    protected Timer findNewDestinationTimer;

    protected Creature aggroed;


    protected float destinationX;
    protected float destinationY;
    protected boolean hasDestination;

    protected Timer attackOrHoldTimer;
    protected float attackOrHoldTime = 500f;
    protected boolean hold;

    protected Timer circlingDirectionTimer;
    protected float circlingDirectionTime = 500f;
    protected boolean circling;
    protected int circlingDir;

    public Mob(GameSystem gameSystem, String id) {
        super(gameSystem, id);

        attackOrHoldTimer = new Timer();
        hold = false;

        circlingDirectionTimer = new Timer();
        circling = false;
        circlingDir = 0;
    }

    @Override
    public void update(GameContainer gc, int i, KeyInput keyInput, GameSystem gameSystem) {
        super.update(gc, i , keyInput, gameSystem);

        if (runningTimer.getTime() > 200) {
            running = false;
        }

//        if (attackingTimer.getTime() > 300) {
//            attacking = false;
//        }

        if (immunityTimer.getTime() > 500) {
            immune = false;
        }
    }

    @Override
    protected void setFacingDirection(GameContainer gc) {
        if (aggroed != null) {
            facingVector = new Vector2f(aggroed.getRect().getCenterX() - rect.getCenterX(), aggroed.getRect().getCenterY() - rect.getCenterY());
        }
    }



    @Override
    public void onDeath() {
        gameSystem.getLootSystem().spawnLootPile(area, rect.getCenterX(), rect.getCenterY(), dropTable);

        for (Ability ability : abilityList) {
            ability.stopAbility();
        }
    }

    @Override
    public void performActions(GameContainer gc, KeyInput keyInput) {

        Map<String, Creature> areaCreatures = area.getCreatures();

        int aggroDistance = 400;
        aggroed = null;
        for (Creature creature : areaCreatures.values()) {
            if (creature instanceof Mob || creature instanceof NonPlayerCharacter) continue;
            if (Globals.distance(creature.rect, rect) < aggroDistance && creature.healthPoints > 0) {
                aggroed = creature;
                break;
            }
        }

        if (actionTimer.getTime() > 500f) {
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

            if (attackOrHoldTimer.getTime() > attackOrHoldTime) {
                hold = Globals.randFloat() < 0.8f;
                attackOrHoldTimer.reset();
            }

            if (circlingDirectionTimer.getTime() > circlingDirectionTime) {
                circling = Globals.randFloat() < 0.8f;
                if (circling) {
                    if (Globals.randFloat() < 0.5f) {
                        circlingDir = 0;
                    }
                    else {
                        circlingDir = 1;
                    }
                }
                circlingDirectionTimer.reset();
            }

            float walkUpDistance = 300f;
            float minimumDistance = 100f;
            float attackDistance = 130f;
            float holdDistance = 175f;

            if (currentAttackType == AttackType.UNARMED) {
                minimumDistance = 100f;
                walkUpDistance = 300f;
                holdDistance = 175f;
                attackDistance = 130f;
            } else
            if (currentAttackType == AttackType.SWORD) {
                minimumDistance = 100f;
                walkUpDistance = 300f;
                holdDistance = 175f;
                attackDistance = 130f;
            }
            else if (currentAttackType == AttackType.BOW) {
                minimumDistance = 300f;
                walkUpDistance = 300f;
                holdDistance = 300f;
                attackDistance = 300f;
            }
            else if (currentAttackType == AttackType.TRIDENT) {
                minimumDistance = 180f;
                walkUpDistance = 400f;
                holdDistance = 220f;
                attackDistance = 200f;
            }

            if (findNewDestinationTimer.getTime() > 200f) {

                float dist = Globals.distance(this.rect, aggroed.rect);


                if (dist < holdDistance) {
                    if (hold) {
                        if (circling) {
                            if (circlingDir == 0) {
                                destinationX = aggroed.rect.getCenterX();
                                destinationY = aggroed.rect.getCenterY();
                                Vector2f destinationVector = new Vector2f(destinationX - rect.getCenterX(), destinationY - rect.getCenterY());

                                Vector2f perpendicular = destinationVector.getPerpendicular();

                                destinationX = aggroed.rect.getCenterX() + perpendicular.getX();
                                destinationY = aggroed.rect.getCenterY() + perpendicular.getY();

                                hasDestination = true;
                            }
                            else {
                                destinationX = aggroed.rect.getCenterX();
                                destinationY = aggroed.rect.getCenterY();
                                Vector2f destinationVector = new Vector2f(destinationX - rect.getCenterX(), destinationY - rect.getCenterY());

                                Vector2f perpendicular = destinationVector.getPerpendicular().negate();

                                destinationX = rect.getCenterX() + perpendicular.getX();
                                destinationY = rect.getCenterY() + perpendicular.getY();

                                hasDestination = true;
                            }
                        }
                        else {
                            hasDestination = false;
                        }
                    }
                    else {
                        destinationX = aggroed.rect.getCenterX();
                        destinationY = aggroed.rect.getCenterY();
                        hasDestination = true;
                    }
                }
                else if (dist < walkUpDistance) {
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
                attack();
            }

            if (Globals.distance(aggroed.rect, rect) < minimumDistance) {
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
