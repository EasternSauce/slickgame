package com.kamilkurp.creatures;

import com.kamilkurp.KeyInput;
import com.kamilkurp.Renderable;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.terrain.TerrainTile;
import com.kamilkurp.utils.Camera;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class Creature implements Renderable {
    protected SpriteSheet spriteSheet;
    protected SpriteSheet attackSheet;
    protected Animation[] walkingAnimation;
    protected Animation[] attackingAnimation;

    protected Rectangle rect;

    protected Rectangle hitbox;

    protected String id;

    protected boolean running = false;

    protected boolean attacking = false;



    protected int direction = 0;

    protected int attackingDirection = 0;

    private Sound attackSound = new Sound("swoosh.wav");

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


    public Creature(String id, int posX, int posY, Map<String, Creature> creatures, LootSystem lootSystem) throws SlickException {
        this.id = id;
        this.lootSystem = lootSystem;

        rect = new Rectangle(posX, posY, 64, 64);
        hitbox = new Rectangle(2, 2, 60, 60);


        runningTimer = new Timer();
        attackingTimer = new Timer();
        immunityTimer = new Timer();
        attackCooldownTimer = new Timer();

        attackRect = new Rectangle(-999, -999, 1, 1);


        loadSpriteSheets();
        loadAnimations();

        creatures.put(this.getId(), this);

    }

    @Override
    public void render(Graphics g, Camera camera) {
        Image sprite = spriteSheet.getSprite(0, direction);

        if (!running) {
            if (healthPoints == 0f) {
                sprite.rotate(90f);
            }
            g.drawImage(sprite, (int)rect.getX() - (int)camera.getPosX(), (int)rect.getY() - (int)camera.getPosY());
        }
        else {
            g.drawAnimation(walkingAnimation[direction], (int)rect.getX() - (int)camera.getPosX(), (int)rect.getY() - (int)camera.getPosY());
        }

        int attackWidth = 40;
        int attackHeight = 40;

        g.setColor(Color.red);
        int shiftX = 0;
        int shiftY = 0;
        if (attackingDirection == 0) {
            shiftX = -attackWidth/2;
            shiftY = -60;
        }
        if (attackingDirection == 1) {
            shiftX = -60;
            shiftY = -attackHeight/2;
        }
        if (attackingDirection == 2) {
            shiftX = -attackWidth/2;
            shiftY = 60-attackHeight/2;
        }
        if (attackingDirection == 3) {
            shiftX = 60-attackWidth;
            shiftY = -attackHeight/2;
        }

        attackRect = new Rectangle(rect.getCenterX() + shiftX, rect.getCenterY() + shiftY, attackWidth, attackHeight);

        if (attacking) {
            g.drawAnimation(attackingAnimation[attackingDirection], rect.getCenterX() + shiftX - camera.getPosX(), rect.getCenterY() + shiftY - camera.getPosY());
        }

    }

    public void update(GameContainer gc, int i, List<TerrainTile> tiles, Collection<Creature> creatures, KeyInput keyInput) {
        beforeMovement(i);

        if (healthPoints > 0f) performActions(gc, creatures, keyInput);

        afterMovement(tiles);

        attackLogic(creatures);
    }

    private void attackLogic(Collection<Creature> creatures) {
        if (attacking) {
            for (Creature creature : creatures) {
                if (creature == this) continue;
                if (attackRect.intersects(creature.rect)) {
                    if (!(this instanceof Enemy && creature instanceof Enemy)) {
                        creature.takeDamage();
                    }
                }
            }
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

    public void loadSpriteSheets() throws SlickException {
        Image image = new Image("spritesheet.png");
        spriteSheet = new SpriteSheet(image, (int)rect.getWidth(), (int)rect.getHeight());

        image = new Image("slash.png");
        attackSheet = new SpriteSheet(image, 40, 40);
    }

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

    public void loadAnimations() {
        walkingAnimation = new Animation[4];

        for (int i = 0; i < 4; i++) {
            walkingAnimation[i] = new Animation();
            for (int j = 0; j < 9; j++) {
                walkingAnimation[i].addFrame(spriteSheet.getSprite(j,i), 100);

            }
        }

        attackingAnimation = new Animation[4];

        // fix this :<

        attackingAnimation[0] = new Animation();
        for (int j = 0; j < 6; j++) {
            Image image = attackSheet.getSprite(j,0).copy();
            image.rotate(270f);
            attackingAnimation[0].addFrame(image, 30);
        }

        attackingAnimation[1] = new Animation();
        for (int j = 0; j < 6; j++) {
            Image image = attackSheet.getSprite(j,0).copy();
            image.rotate(180);
            attackingAnimation[1].addFrame(image, 30);
        }

        attackingAnimation[2] = new Animation();
        for (int j = 0; j < 6; j++) {
            Image image = attackSheet.getSprite(j,0).copy();
            image.rotate(90f);
            attackingAnimation[2].addFrame(image, 30);
        }

        attackingAnimation[3] = new Animation();
        for (int j = 0; j < 6; j++) {
            Image image = attackSheet.getSprite(j,0).copy();
            attackingAnimation[3].addFrame(image, 30);
        }

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

            if (!attacking) {
                attackingAnimation[0].restart();
                attackingAnimation[1].restart();
                attackingAnimation[2].restart();
                attackingAnimation[3].restart();

                attacking = true;
                attackingTimer.reset();
                attackingDirection = direction;
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
