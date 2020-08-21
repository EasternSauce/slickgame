package com.kamilkurp.creatures;

import com.kamilkurp.KeyInput;
import com.kamilkurp.Renderable;
import com.kamilkurp.animations.AttackAnimation;
import com.kamilkurp.animations.WalkAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.items.Item;
import com.kamilkurp.items.ItemType;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.projectile.Arrow;
import com.kamilkurp.terrain.TerrainTile;
import com.kamilkurp.utils.Camera;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.*;

public abstract class Creature implements Renderable {

    protected Rectangle rect;

    protected Rectangle hitbox;

    protected String id;

    protected boolean running = false;

    protected boolean attacking = false;



    protected int direction = 0;

    protected int attackingDirection = 0;

    private Sound swordAttackSound = Assets.attackSound;
    private Sound bowAttackSound = Assets.arrowWhizzSound;

    protected Timer runningTimer;
    protected Timer attackingTimer;
    protected Timer swordAttackCooldownTimer;
    protected Timer bowAttackCooldownTimer;

    protected boolean moving;

    protected int totalDirections;

    protected int dirX;
    protected int dirY;

    protected float speed = 0f;

    protected float maxHealthPoints = 100f;
    protected float healthPoints = maxHealthPoints;

    protected Rectangle swordAttackRect;

    protected Timer immunityTimer;

    protected boolean immune;

    protected LootSystem lootSystem;

    protected Vector2f facingVector;
    protected double facingAngle;
    protected Vector2f attackingVector;
    protected double attackingAngle;




    protected WalkAnimation walkAnimation;
    protected AttackAnimation swordAttackAnmation;


    protected AttackType currentAttackType;

    protected Map<Integer, Item> equipmentItems;



    public Creature(String id, int posX, int posY, Map<String, Creature> creatures, List<Creature> creaturesList, LootSystem lootSystem) throws SlickException {
        this.id = id;
        this.lootSystem = lootSystem;

        rect = new Rectangle(posX, posY, 64, 64);
        hitbox = new Rectangle(2, 2, 60, 60);

        walkAnimation = new WalkAnimation(Assets.male1SpriteSheet, 3, 100, new int [] {3,1,0,2}, 1);

        runningTimer = new Timer();
        attackingTimer = new Timer();
        immunityTimer = new Timer();
        swordAttackCooldownTimer = new Timer();
        bowAttackCooldownTimer = new Timer();

        swordAttackRect = new Rectangle(-999, -999, 1, 1);

        swordAttackAnmation = new AttackAnimation(Assets.betterSlashSpriteSheet, 6, 50);

        facingAngle = 0.0f;
        facingVector = new Vector2f(0f, 0f);

        creatures.put(this.getId(), this);
        creaturesList.add(this);

        currentAttackType = AttackType.BOW;

        equipmentItems = new TreeMap<>();

        equipmentItems.put(0, new Item(ItemType.getItemType("woodenSword"), null));

        updateAttackType();


    }

    @Override
    public void render(Graphics g, Camera camera) {
        Image sprite = walkAnimation.getRestPosition(direction);

        if (!running) {
            if (healthPoints == 0f) {
                sprite.rotate(90f);
            }
            sprite.draw((int)rect.getX() - (int)camera.getPosX(), (int)rect.getY() - (int)camera.getPosY(), rect.getWidth(), rect.getHeight());
        }
        else {
            walkAnimation.getAnimation(direction).draw((int)rect.getX() - (int)camera.getPosX(), (int)rect.getY() - (int)camera.getPosY(), rect.getWidth(), rect.getHeight());
        }

        Assets.verdanaTtf.drawString((int)rect.getX() - (int)camera.getPosX(), (int)rect.getY() - (int)camera.getPosY() - 30f, (int)healthPoints + "/" + (int)maxHealthPoints, Color.red);;
    }

    public void renderAttackAnimation(Graphics g, Camera camera) {
        if (attacking) {
            if (currentAttackType == AttackType.SWORD) {
                Image image = swordAttackAnmation.getAnimation().getCurrentFrame();
                image.setRotation((float) attackingAngle);

                g.drawImage(image, swordAttackRect.getX() - camera.getPosX(), swordAttackRect.getY() - camera.getPosY());
            } else if (currentAttackType == AttackType.BOW) {
                // shooting bow animation?
            }


        }
    }

    public void update(GameContainer gc, int i, List<TerrainTile> tiles, List<Creature> creatures, KeyInput keyInput, List<Arrow> arrowList) {


        beforeMovement(i);

        if (healthPoints > 0f) performActions(gc, creatures, keyInput, arrowList, tiles);

        afterMovement(tiles);

        setFacingDirection(gc);

        swordAttackLogic(i, creatures);
    }

    protected abstract void setFacingDirection(GameContainer gc);

    private void swordAttackLogic(int i, Collection<Creature> creatures) {
        if (attacking) {
            if (currentAttackType == AttackType.SWORD) {
                for (Creature creature : creatures) {
                    if (creature == this) continue;
                    if (swordAttackRect.intersects(creature.rect)) {
                        if (!(this instanceof Enemy && creature instanceof Enemy)) {
                            creature.takeDamage(equipmentItems.get(0).getItemType().getDamage());
                        }
                    }
                }

                float attackRange = 60f;

                float attackShiftX = attackingVector.getNormal().getX() * attackRange;
                float attackShiftY = attackingVector.getNormal().getY() * attackRange;

                int attackWidth = 40;
                int attackHeight = 40;

                float attackRectX = attackShiftX + rect.getCenterX() - attackWidth / 2f;
                float attackRectY = attackShiftY + rect.getCenterY() - attackHeight / 2f;

                swordAttackRect = new Rectangle(attackRectX, attackRectY, attackWidth, attackHeight);


                swordAttackAnmation.getAnimation().update(i);
            }


        }

    }

    public void takeDamage(float damage) {
        if (!immune) {

            float beforeHP = healthPoints;

            if (healthPoints - damage > 0) healthPoints -= damage;
            else healthPoints = 0f;

            if (beforeHP != healthPoints && healthPoints == 0f) {
                onDeath();
            }

            immunityTimer.reset();
            immune = true;
        }

    }

    protected abstract void onDeath();


    public void move(float dx, float dy) {
        rect.setX(rect.getX() + dx);
        rect.setY(rect.getY() + dy);
    }

    public boolean isCollidingX(List<TerrainTile> tiles, float newPosX, float newPosY) {
        for(TerrainTile tile : tiles) {
            if (tile.isPassable()) continue;

            Rectangle tileRect = tile.getRect();
            Rectangle rect1 = new Rectangle(tileRect.getX(), tileRect.getY(), tileRect.getWidth(), tileRect.getHeight());

            Rectangle rect2 = new Rectangle(newPosX + hitbox.getX(), rect.getY() + hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());

            if(rect1.intersects(rect2)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCollidingY(List<TerrainTile> tiles, float newPosX, float newPosY) {
        for(TerrainTile tile : tiles) {
            if (tile.isPassable()) continue;

            Rectangle tileRect = tile.getRect();
            Rectangle rect1 = new Rectangle(tileRect.getX(), tileRect.getY(), tileRect.getWidth(), tileRect.getHeight());

            Rectangle rect2 = new Rectangle(rect.getX() + hitbox.getX(), newPosY + hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());

            if(rect1.intersects(rect2)) {
                return true;
            }
        }
        return false;
    }



    public void beforeMovement(int i) {
        moving = false;

        totalDirections = 0;

        dirX = 0;
        dirY = 0;

        speed = 0.2f * i;
    }

    public void moveUp() {
        dirY = -1;
        moving = true;
        direction = 0;
        totalDirections++;
    }

    public void moveLeft() {
        dirX = -1;
        moving = true;
        direction = 1;
        totalDirections++;
    }

    public void moveDown() {
        dirY = 1;
        moving = true;
        direction = 2;
        totalDirections++;
    }

    public void moveRight() {
        dirX = 1;
        moving = true;
        direction = 3;
        totalDirections++;
    }

    public void attack(List<Arrow> arrowList, List<TerrainTile> tiles, List<Creature> creatures) {
        if (currentAttackType == AttackType.SWORD) {
            if (swordAttackCooldownTimer.getTime() > 800f) {
                swordAttackSound.play(1.0f, 0.1f);

                if (!attacking) { // on start attack
                    swordAttackAnmation.restart();

                    attacking = true;
                    attackingTimer.reset();
                    attackingAngle = facingAngle;
                    attackingVector = facingVector;
                }
                swordAttackCooldownTimer.reset();
            }
        } else if (currentAttackType == AttackType.BOW) {

            if (bowAttackCooldownTimer.getTime() > 1200f) {
                if (!attacking) { // on start attack
                    bowAttackSound.play(1.0f, 0.1f);

                    attacking = true;
                    attackingTimer.reset();
                    attackingAngle = facingAngle;
                    attackingVector = facingVector;

                    if (!facingVector.equals(new Vector2f(0.f, 0f))) {
                        Arrow arrow = new Arrow(rect.getX(), rect.getY(), facingVector, arrowList, tiles, creatures, this);
                        arrowList.add(arrow);
                    }


                }
                bowAttackCooldownTimer.reset();

            }
        }


    }

    public void afterMovement(List<TerrainTile> tiles) {
        if (totalDirections > 1) {
            speed /= Math.sqrt(2);
        }

        float newPosX = rect.getX() + speed * dirX;
        float newPosY = rect.getY() + speed * dirY;

        if (!isCollidingX(tiles, newPosX, newPosY) && newPosX >= 0 && newPosX < tiles.get(tiles.size() - 1).getRect().getX()) {
            move(speed * dirX, 0);
        }

        if (!isCollidingY(tiles, newPosX, newPosY) && newPosY >= 0 && newPosY < tiles.get(tiles.size() - 1).getRect().getY()) {
            move(0, speed * dirY);
        }

        if (moving) {
            runningTimer.reset();
            running = true;
        }
    }

    public abstract void performActions(GameContainer gc, List<Creature> creatures, KeyInput keyInput, List<Arrow> arrowList, List<TerrainTile> tiles);


    public String getId() {
        return id;
    }

    public Rectangle getRect() {
        return rect;
    }

    public float getHealthPoints() {
        return healthPoints;
    }

    public float getMaxHealthPoints() {
        return maxHealthPoints;
    }

    public enum AttackType {NONE, SWORD, BOW};


    public void updateAttackType() {
        String currentWeaponName = equipmentItems.get(0).getItemType().getId();
        if (currentWeaponName == null) {
            currentAttackType = AttackType.NONE;
        } else if (currentWeaponName.equals("woodenSword") || currentWeaponName.equals("ironSword")) {
            currentAttackType = AttackType.SWORD;
        } else if (currentWeaponName.equals("crossbow")) {
            currentAttackType = AttackType.BOW;
        }
    }

    public Map<Integer, Item> getEquipmentItems() {
        return equipmentItems;
    }
}
