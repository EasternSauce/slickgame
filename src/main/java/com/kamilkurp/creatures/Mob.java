package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.abilities.Ability;
import com.kamilkurp.items.Item;
import com.kamilkurp.items.ItemType;
import com.kamilkurp.spawn.MobSpawnPoint;
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

    protected float aggroDistance;

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
    protected MobSpawnPoint mobSpawnPoint;

    protected boolean isBoss;

    protected Float attackDistance;
    protected Float walkUpDistance;

    public Mob(GameSystem gameSystem, MobSpawnPoint mobSpawnPoint, String id) {
        super(gameSystem, id);

        attackOrHoldTimer = new Timer(true);
        hold = false;

        circlingDirectionTimer = new Timer(true);
        circling = false;
        circlingDir = 0;
        this.mobSpawnPoint = mobSpawnPoint;

        aggroDistance = 400;

        isBoss = false;

        findNewDestinationTimer = new Timer(true);
    }

    @Override
    public void update(GameContainer gc, int i, KeyInput keyInput, GameSystem gameSystem) {
        super.update(gc, i , keyInput, gameSystem);

        if (runningTimer.getElapsed() > 200) {
            running = false;
        }

//        if (attackingTimer.getTime() > 300) {
//            attacking = false;
//        }

        if (immunityTimer.getElapsed() > 500) {
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

        currentAttack.stopAbility();
    }

    @Override
    public void performActions(GameContainer gc, KeyInput keyInput) {

        Map<String, Creature> areaCreatures = area.getCreatures();

        aggroed = null;
        for (Creature creature : areaCreatures.values()) {
            if (creature instanceof Mob || creature instanceof NonPlayerCharacter) continue;
            if (Globals.distance(creature.rect, rect) < aggroDistance && creature.healthPoints > 0) {
                aggroed = creature;

                onAggroed();

                break;
            }
        }

        if (aggroed == null) {
            performIdleBehavior();
        }
        else {
            performAggroedBehavior();
        }

    }

    public void performAggroedBehavior() {
        if (attackOrHoldTimer.getElapsed() > attackOrHoldTime) {
            hold = Globals.randFloat() < 0.8f;
            attackOrHoldTimer.reset();
        }

        if (circlingDirectionTimer.getElapsed() > circlingDirectionTime) {
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

        AttackType attackType = currentAttack.getAttackType();

        if (findNewDestinationTimer.getElapsed() > 200f) {

            float dist = Globals.distance(this.rect, aggroed.rect);

            if (dist < attackType.holdDistance) {
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
            else if (dist < (walkUpDistance == null ? attackType.walkUpDistance : walkUpDistance)) {
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
            walkTowards(destinationX, destinationY);
        }

        if (!immobilized) {
            if (Globals.distance(aggroed.getRect(), this.getRect()) < (attackDistance == null ? attackType.attackDistance : attackDistance)) {
                performCombatAbilities();
            }
        }

        if (Globals.distance(aggroed.rect, rect) < attackType.minimumDistance) {
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

    public void performIdleBehavior() {
        if (actionTimer.getElapsed() > 500f) {
            currentDirection = Math.abs(random.nextInt())%4;
            stayInPlace = Math.abs(random.nextInt()) % 10 < 8;
            actionTimer.reset();
        }

        if (!stayInPlace && !immobilized) {
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


    void walkTowards(float gotoPosX, float gotoPosY) {
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

    public MobSpawnPoint getMobSpawnPoint() {
        return mobSpawnPoint;
    }

    public void grantWeapon(String weaponName) {
        ItemType weaponItemType = ItemType.getItemType(weaponName);
        equipmentItems.put(0, new Item(weaponItemType, null));
    }

    public void performCombatAbilities() {
        if (currentAttack.canPerform()) {
            currentAttack.perform();
        }
    }
}
