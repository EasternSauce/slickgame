package com.kamilkurp.creatures;

import com.kamilkurp.KeyInput;
import com.kamilkurp.Renderable;
import com.kamilkurp.animations.AttackAnimation;
import com.kamilkurp.animations.WalkAnimation;
import com.kamilkurp.assets.Assets;
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

    private Sound attackSound = Assets.attackSound;

    protected Timer runningTimer;
    protected Timer attackingTimer;
    protected Timer attackCooldownTimer;

    protected boolean moving;

    protected int totalDirections;

    protected int dirX;
    protected int dirY;

    protected float speed = 0f;

    protected float healthPoints = 100f;

    protected Rectangle attackRect;

    protected Timer immunityTimer;

    protected boolean immune;

    protected LootSystem lootSystem;

    protected Vector2f facingVector;
    protected double facingAngle;
    protected Vector2f attackingVector;
    protected double attackingAngle;




    WalkAnimation walkAnimation;
    AttackAnimation attackAnimation;


    public Creature(String id, int posX, int posY, Map<String, Creature> creatures, LootSystem lootSystem) throws SlickException {
        this.id = id;
        this.lootSystem = lootSystem;

        rect = new Rectangle(posX, posY, 64, 64);
        hitbox = new Rectangle(2, 2, 60, 60);

        //walkAnimation = new WalkAnimation(Assets.skeletonSpriteSheet, 9, 100, new int [] {0,1,2,3}, 0);
        walkAnimation = new WalkAnimation(Assets.male1SpriteSheet, 3, 100, new int [] {3,1,0,2}, 1);

        runningTimer = new Timer();
        attackingTimer = new Timer();
        immunityTimer = new Timer();
        attackCooldownTimer = new Timer();

        attackRect = new Rectangle(-999, -999, 1, 1);

        attackAnimation = new AttackAnimation(Assets.betterSlashSpriteSheet, 6, 50);

        facingAngle = 0.0f;
        facingVector = new Vector2f(0f, 0f);

        creatures.put(this.getId(), this);

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
    }

    public void renderAttackAnimation(Graphics g, Camera camera) {
        if (attacking) {
            Image image = attackAnimation.getAnimation().getCurrentFrame();
            image.setRotation((float) attackingAngle);

            g.drawImage(image, attackRect.getX() - camera.getPosX(), attackRect.getY() - camera.getPosY());

        }
    }

    public void update(GameContainer gc, int i, List<TerrainTile> tiles, Collection<Creature> creatures, KeyInput keyInput, List<Arrow> arrowList) {


        beforeMovement(i);

        if (healthPoints > 0f) performActions(gc, creatures, keyInput, arrowList, tiles);

        afterMovement(tiles);

        setFacingDirection(gc);

        attackLogic(i, creatures);
    }

    protected abstract void setFacingDirection(GameContainer gc);

    private void attackLogic(int i, Collection<Creature> creatures) {
        if (attacking) {
            for (Creature creature : creatures) {
                if (creature == this) continue;
                if (attackRect.intersects(creature.rect)) {
                    if (!(this instanceof Enemy && creature instanceof Enemy)) {
                        creature.takeDamage();
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

            attackRect = new Rectangle(attackRectX, attackRectY, attackWidth, attackHeight);


            attackAnimation.getAnimation().update(i);
        }

    }

    public void takeDamage() {
        if (!immune) {

            float beforeHP = healthPoints;

            float damage = 100f;
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

    public void attack() {
        if (attackCooldownTimer.getTime() > 800f) {
            attackSound.play(1.0f, 0.1f);

            if (!attacking) { // on start attack
                attackAnimation.restart();

                attacking = true;
                attackingTimer.reset();
                attackingAngle = facingAngle;
                attackingVector = facingVector;
            }
            attackCooldownTimer.reset();
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

    public abstract void performActions(GameContainer gc, Collection<Creature> creatures, KeyInput keyInput, List<Arrow> arrowList, List<TerrainTile> tiles);


    public String getId() {
        return id;
    }

    public Rectangle getRect() {
        return rect;
    }

    public float getHealthPoints() {
        return healthPoints;
    }
}
