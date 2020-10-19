package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.animations.WalkAnimation;
import com.kamilkurp.areagate.AreaGate;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.items.Item;
import com.kamilkurp.items.ItemType;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.projectile.Arrow;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.terrain.TerrainTile;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wolf extends Mob {




    public Wolf(GameSystem gameSystem, String id, float posX, float posY, Area area) throws SlickException {
        super(gameSystem, id, posX, posY, area);

        actionTimer = new Timer();

        dropTable = new HashMap<>();
        dropTable.put("ringmailGreaves", 0.9f);
        dropTable.put("leatherArmor", 0.2f);
        dropTable.put("hideGloves", 0.1f);
        dropTable.put("crossbow", 0.05f);
        dropTable.put("ironSword", 0.05f);
        dropTable.put("leatherHelmet", 0.15f);
        dropTable.put("lifeRing", 0.05f);


        findNewDestinationTimer = new Timer();

        walkAnimation = new WalkAnimation(Assets.wolfSpriteSheet, 4, 100, new int [] {3,1,0,2}, 0);

        destinationX = 0.0f;
        destinationY = 0.0f;
        hasDestination = false;

        hitbox = new Rectangle(17, 15, 30, 46);

        setMaxHealthPoints(1f);
        setHealthPoints(getMaxHealthPoints());


        equipmentItems.put(0, new Item(ItemType.getItemType("woodenSword"), null));

        updateAttackType();



    }

    @Override
    public void update(GameContainer gc, int i, List<TerrainTile> tiles, Map<String, Creature> creatures, KeyInput keyInput, List<Arrow> arrowList, List<AreaGate> gatesList) throws SlickException {
        super.update(gc, i ,tiles, creatures, keyInput, arrowList, gatesList);
    }

    @Override
    public String getCreatureType() {
        return "wolf";
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

            float dashDistance = 250f;

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

                if (dashCooldownTimer.getTime() > 3000f) {
                    if (Globals.distance(aggroed.rect, rect) < dashDistance) {
                        //start dash, start dash cooldown
                        System.out.println("start dash");
                        dashing = true;

                        dashVector = new Vector2f(destinationX - rect.getX(), destinationY - rect.getY()).normalise();

                        dashCooldownTimer.reset();
                        dashTimer.reset();
                    }

                }
            }

            if (dashing) {
                //end dash
                if (dashTimer.getTime() > 1000f) {
                    System.out.println("end dash");

                    dashing = false;
                }
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

}
