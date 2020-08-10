package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.Renderable;
import com.kamilkurp.animations.AttackAnimation;
import com.kamilkurp.animations.WalkAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.terrain.TerrainTile;
import com.kamilkurp.utils.Camera;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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

//        int attackWidth = 40;
//        int attackHeight = 40;
//
//        g.setColor(Color.red);
//        int shiftX = 0;
//        int shiftY = 0;
//        if (attackingDirection == 0) {
//            shiftX = -attackWidth/2;
//            shiftY = -60;
//        }
//        if (attackingDirection == 1) {
//            shiftX = -60;
//            shiftY = -attackHeight/2;
//        }
//        if (attackingDirection == 2) {
//            shiftX = -attackWidth/2;
//            shiftY = 60-attackHeight/2;
//        }
//        if (attackingDirection == 3) {
//            shiftX = 60-attackWidth;
//            shiftY = -attackHeight/2;
//        }

        if (attacking) {
            Image image = attackAnimation.getAnimation().getCurrentFrame();
            image.setRotation((float) attackingAngle);

            g.drawImage(image, attackRect.getX() - camera.getPosX(), attackRect.getY() - camera.getPosY());
            //g.drawRect(attackShiftX + rect.getCenterX() - camera.getPosX() - attackWidth / 2f, attackShiftY + rect.getCenterY() - camera.getPosY() - attackHeight / 2f, attackWidth, attackHeight);



        }


        //if (attacking) {
           // g.drawAnimation(attackAnimation.getAnimation(attackingDirection), rect.getCenterX() + shiftX - camera.getPosX(), rect.getCenterY() + shiftY - camera.getPosY());
        //}

        //attackAnimation.getAnimation(3




    }

    public void update(GameContainer gc, int i, List<TerrainTile> tiles, Collection<Creature> creatures, KeyInput keyInput) {




//        System.out.println(direction);


        //attackRect = new Rectangle(rect.getCenterX() + shiftX, rect.getCenterY() + shiftY, attackWidth, attackHeight);


        beforeMovement(i);

        if (healthPoints > 0f) performActions(gc, creatures, keyInput);

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

        if (!isCollidingX(tiles, newPosX, newPosY)) {
            move(speed * dirX, 0);
        }

        if (!isCollidingY(tiles, newPosX, newPosY)) {
            move(0, speed * dirY);
        }

        if (moving) {
            runningTimer.reset();
            running = true;
        }
    }

    public abstract void performActions(GameContainer gc, Collection<Creature> creatures, KeyInput keyInput);


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
