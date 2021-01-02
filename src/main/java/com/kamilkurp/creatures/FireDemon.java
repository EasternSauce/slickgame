package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.abilities.Ability;
import com.kamilkurp.abilities.AbilityState;
import com.kamilkurp.abilities.MeteorRainAbility;
import com.kamilkurp.animations.WalkAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.items.Item;
import com.kamilkurp.items.ItemType;
import com.kamilkurp.spawn.MobSpawnPoint;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.HashMap;
import java.util.Map;

public class FireDemon extends Mob {

    protected MeteorRainAbility meteorRainAbility;

    protected boolean bossBattleStarted;

    protected Music fireDemonMusic = Assets.fireDemon;

    protected Sound roarSound = Assets.roarSound;

    public FireDemon(GameSystem gameSystem, MobSpawnPoint mobSpawnPoint, String id, String weapon) throws SlickException {
        super(gameSystem, mobSpawnPoint, id);


        scale = 2.0f;

        rect = new Rectangle(0, 0, 80 * scale, 80 * scale);
        hitbox = new Rectangle(0, 0, 80 * scale, 80 * scale);

        actionTimer = new Timer();

        dropTable = new HashMap<>();
        dropTable.put("ironSword", 0.3f);
        dropTable.put("poisonDagger", 0.3f);
        dropTable.put("steelArmor", 0.8f);
        dropTable.put("steelHelmet", 0.5f);
        dropTable.put("thiefRing", 1.0f);

        findNewDestinationTimer = new Timer();

        walkAnimation = new WalkAnimation(Assets.fireDemonSpriteSheet, 4, 300, new int [] {3,1,0,2}, 0);

        destinationX = 0.0f;
        destinationY = 0.0f;
        hasDestination = false;

        setMaxHealthPoints(2500f);
        setHealthPoints(getMaxHealthPoints());

        equipmentItems.put(0, new Item(ItemType.getItemType(weapon), null));

        creatureType = "boss";

        bossBattleStarted = false;

    }

    @Override
    public void attack() {

        if (staminaPoints > 0f && meteorRainAbility.getState() == AbilityState.ABILITY_INACTIVE) {
            if (healthPoints < maxHealthPoints * 0.70) meteorRainAbility.tryPerforming();

            if (currentAttackType == AttackType.UNARMED) {
                unarmedAttackAbility.tryPerforming();
            } else if (currentAttackType == AttackType.SWORD) {
                swordAttackAbility.tryPerforming();
            } else if (currentAttackType == AttackType.BOW) {
                bowAttackAbility.tryPerforming();
            } else if (currentAttackType == AttackType.TRIDENT) {
                tridentAttackAbility.tryPerforming();
            }
        }

    }

    @Override
    public void onInit() {
        defineAbilities();

        tridentAttackAbility.setAttackRange(45f);
        tridentAttackAbility.setScale(2.5f);

        meteorRainAbility = new MeteorRainAbility(this);
        abilityList.add(meteorRainAbility);

        updateAttackType();
    }

    @Override
    public void update(GameContainer gc, int i, KeyInput keyInput, GameSystem gameSystem) {
        super.update(gc, i, keyInput, gameSystem);
    }


    @Override
    public String getCreatureType() {
        return "fireDemon";
    }

    @Override
    public void onUpdateStart(int i) {
        moving = false;

        totalDirections = 0;

        knockbackSpeed = knockbackPower * i;

        dirX = 0;
        dirY = 0;

        speed = 0.35f * i;

        if (isAttacking) {
            speed = speed / 2f;
        }

    }

    @Override
    public void takeDamage(float damage, boolean immunityFrames, float knockbackPower, float sourceX, float sourceY) {
        if (isAlive()) {

            float beforeHP = healthPoints;

            float actualDamage = damage * 100f/(100f + getTotalArmor());

            if (healthPoints - actualDamage > 0) healthPoints -= actualDamage;
            else healthPoints = 0f;

            if (beforeHP != healthPoints && healthPoints == 0f) {
                onDeath();
            }

            if (immunityFrames) {
                immunityTimer.reset();
                immune = true;
            }

//            if (!knockback && knockbackPower > 0f) {
//                this.knockbackPower = knockbackPower;
//
//                knockbackVector = new Vector2f(rect.getX() - sourceX, rect.getY() - sourceY).getNormal();
//                knockback = true;
//                knockbackTimer.reset();
//
//            }

            roarSound.play(1.0f, 0.1f);

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

                if (!bossBattleStarted) {
                    bossBattleStarted = true;

                    fireDemonMusic.loop(1.0f, Globals.MUSIC_VOLUME);

                    mobSpawnPoint.getBlockade().setActive(true);
                }

                break;
            }
        }

        if (actionTimer.getTime() > 500f) {
            currentDirection = Math.abs(random.nextInt())%4;
            stayInPlace = Math.abs(random.nextInt()) % 10 < 8;
            actionTimer.reset();
        }

        if (aggroed != null) {

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

    @Override
    public void onDeath() {
        gameSystem.getLootSystem().spawnLootPile(area, rect.getCenterX(), rect.getCenterY(), dropTable);

        for (Ability ability : abilityList) {
            ability.stopAbility();
        }

        fireDemonMusic.stop();
        mobSpawnPoint.getBlockade().setActive(false);
    }
}
