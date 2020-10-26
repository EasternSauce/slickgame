package com.kamilkurp.creatures;

import com.kamilkurp.KeyInput;
import com.kamilkurp.Renderable;
import com.kamilkurp.animations.WalkAnimation;
import com.kamilkurp.areagate.AreaGate;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.behavior.BowAttackAbility;
import com.kamilkurp.behavior.UnarmedAttackAbility;
import com.kamilkurp.behavior.SwordAttackAbility;
import com.kamilkurp.items.Item;
import com.kamilkurp.projectile.Arrow;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.terrain.TerrainTile;
import com.kamilkurp.utils.Camera;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class Creature implements Renderable {

    protected Rectangle rect;

    protected Rectangle hitbox;

    protected String id;

    protected boolean running = false;

//    protected boolean attacking = false;

    private final Sound gruntSound = Assets.gruntSound;

    protected int direction = 0;


    protected Timer runningTimer;

    protected boolean moving;

    protected int totalDirections;

    protected int dirX;
    protected int dirY;

    protected float speed = 0f;

    protected float maxHealthPoints = 100f;
    protected float healthPoints = maxHealthPoints;


    protected Timer immunityTimer;

    protected boolean immune;

    protected Vector2f facingVector;
    protected Vector2f attackingVector;


    protected WalkAnimation walkAnimation;


    protected AttackType currentAttackType;

    protected Map<Integer, Item> equipmentItems;

    protected float healthRegen = 0.3f;

    protected Timer healthRegenTimer;

    protected Area area;

    protected boolean passedGateRecently = false;

    protected Area pendingArea;

    protected boolean toBeRemoved;

    protected Float pendingX;
    protected Float pendingY;

    protected GameSystem gameSystem;


    protected boolean immobilized = false;

    protected BowAttackAbility bowAttackAbility;
    protected UnarmedAttackAbility unarmedAttackAbility;
    protected SwordAttackAbility swordAttackAbility;

    protected float unarmedDamage;

    public Creature(GameSystem gameSystem, String id) {
        this.gameSystem = gameSystem;
        this.id = id;
        rect = new Rectangle(0, 0, 64, 64);
        hitbox = new Rectangle(2, 2, 60, 60);

        walkAnimation = new WalkAnimation(Assets.male1SpriteSheet, 3, 100, new int[]{3, 1, 0, 2}, 1);

        runningTimer = new Timer();
        immunityTimer = new Timer();
        healthRegenTimer = new Timer();

        facingVector = new Vector2f(0f, 0f);

        currentAttackType = AttackType.UNARMED;

        equipmentItems = new TreeMap<>();

        toBeRemoved = false;

        pendingX = 0.0f;
        pendingY = 0.0f;

        bowAttackAbility = new BowAttackAbility(this);
        unarmedAttackAbility = new UnarmedAttackAbility(this);
        swordAttackAbility = new SwordAttackAbility(this);

        unarmedDamage = 5f;

    }

    public abstract void onInit();

    @Override
    public void render(Graphics g, Camera camera) {
        Image sprite = walkAnimation.getRestPosition(direction);

        if (!running) {
            if (!isAlive()) {
                sprite.rotate(90f);
            }
            sprite.draw((int) rect.getX() - (int) camera.getPosX(), (int) rect.getY() - (int) camera.getPosY(), rect.getWidth(), rect.getHeight());
        } else {
            walkAnimation.getAnimation(direction).draw((int) rect.getX() - (int) camera.getPosX(), (int) rect.getY() - (int) camera.getPosY(), rect.getWidth(), rect.getHeight());
        }

        Assets.verdanaTtf.drawString((int) rect.getX() - (int) camera.getPosX(), (int) rect.getY() - (int) camera.getPosY() - 30f, (int) healthPoints + "/" + (int) getMaxHealthPoints(), Color.red);
    }

    public void renderAbilities(Graphics g, Camera camera) {
        swordAttackAbility.render(g, camera);
        unarmedAttackAbility.render(g, camera);
        bowAttackAbility.render(g, camera);

    }

    public void update(GameContainer gc, int i, KeyInput keyInput) {

        if (isAlive()) {
            onUpdateStart(i);

            performActions(gc, keyInput);

            regenerateHealth();

            executeMovementLogic();

            setFacingDirection(gc);

//            meleeAttackLogic(i);
        }

        swordAttackAbility.update(i);
        unarmedAttackAbility.update(i);
        bowAttackAbility.update(i);


    }

    public void areaGateLogic(List<AreaGate> gatesList) {
        if (passedGateRecently) {

            boolean leftGate = true;
            for (AreaGate areaGate : gatesList) {
                if (areaGate.getAreaFrom() == area) {
                    if (rect.intersects(areaGate.getFromRect())) {
                        leftGate = false;
                        break;
                    }
                }
                if (areaGate.getAreaTo() == area) {
                    if (rect.intersects(areaGate.getToRect())) {
                        leftGate = false;
                        break;
                    }
                }
            }

            passedGateRecently = !leftGate;

        }
    }

    public void regenerateHealth() {
        if (healthRegenTimer.getTime() > 500f) {
            if (getHealthPoints() < getMaxHealthPoints()) {
                float afterRegen = getHealthPoints() + healthRegen;
                healthPoints = Math.min(afterRegen, getMaxHealthPoints());
            }
            healthRegenTimer.reset();
        }
    }

    protected abstract void setFacingDirection(GameContainer gc);


    public void takeDamage(float damage) {
        if (!immune && isAlive()) {

            float beforeHP = healthPoints;

            float actualDamage = damage * 100f/(100f + getTotalArmor());

            if (healthPoints - actualDamage > 0) healthPoints -= actualDamage;
            else healthPoints = 0f;

            if (beforeHP != healthPoints && healthPoints == 0f) {
                onDeath();
            }

            immunityTimer.reset();
            immune = true;
            gruntSound.play(1.0f, 0.1f);
        }

    }

    public float getTotalArmor() {
        float totalArmor = 0.0f;
        for (Map.Entry<Integer, Item> equipmentItem : equipmentItems.entrySet()) {
            if (equipmentItem.getValue() != null && equipmentItem.getValue().getItemType().getMaxArmor() != null) {
                totalArmor += equipmentItem.getValue().getItemType().getMaxArmor();
            }
        }
        return totalArmor;
    }

    public abstract void onDeath();


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



    public void onUpdateStart(int i) {
        moving = false;

        totalDirections = 0;

        dirX = 0;
        dirY = 0;

        speed = 0.2f * i;

        performAbilityOnUpdateStart(i);
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

        if (currentAttackType == AttackType.UNARMED) {
            unarmedAttackAbility.tryPerforming();
        } else if (currentAttackType == AttackType.SWORD) {
            swordAttackAbility.tryPerforming();
        } else if (currentAttackType == AttackType.BOW) {
            bowAttackAbility.tryPerforming();
        }


    }

    public void executeMovementLogic() {
        List<TerrainTile> tiles = area.getTiles();

        if (!immobilized) {
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

        performAbilityMovement();

    }

    public void performAbilityMovement() {
        swordAttackAbility.performMovement();
        unarmedAttackAbility.performMovement();
        bowAttackAbility.performMovement();
    }

    protected void performAbilityOnUpdateStart(int i) {
        swordAttackAbility.performOnUpdateStart(i);
        unarmedAttackAbility.performOnUpdateStart(i);
        bowAttackAbility.performOnUpdateStart(i);
    }

    public abstract void performActions(GameContainer gc, KeyInput keyInput);


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
        if (equipmentItems.get(4) != null && equipmentItems.get(4).getItemType().getId().equals("lifeRing")) return maxHealthPoints * 1.1f;
        return maxHealthPoints;
    }

    public void kill() {
        healthPoints = 0f;
    }

    public void moveToArea(Area area, float posX, float posY) {
        pendingArea = area;
        pendingX = posX;
        pendingY = posY;
    }

    public void setImmobilized(boolean immobilized) {
        this.immobilized = immobilized;
    }

    public enum AttackType {UNARMED, SWORD, BOW}


    public void updateAttackType() {
        if (equipmentItems.get(0) == null) {
            currentAttackType = AttackType.UNARMED;
            return;
        };

        String currentWeaponName = equipmentItems.get(0).getItemType().getId();
        if (currentWeaponName.equals("woodenSword") || currentWeaponName.equals("ironSword")) {
            currentAttackType = AttackType.SWORD;
        } else if (currentWeaponName.equals("crossbow")) {
            currentAttackType = AttackType.BOW;
        }
    }

    public Map<Integer, Item> getEquipmentItems() {
        return equipmentItems;
    }

    public void setMaxHealthPoints(float maxHealthPoints) {
        this.maxHealthPoints = maxHealthPoints;
    }

    public void setHealthPoints(float healthPoints) {
        this.healthPoints = healthPoints;
    }


    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
        pendingArea = null;

    }

    public boolean isAlive() {
        return healthPoints > 0f;
    }

    public void setPassedGateRecently(boolean passedGateRecently) {
        this.passedGateRecently = passedGateRecently;
    }

    public boolean isPassedGateRecently() {
        return passedGateRecently;
    }

    public Area getPendingArea() {
        return pendingArea;
    }

    public void markForDeletion() {
        toBeRemoved = true;
    }

    public boolean isToBeRemoved() {
        return toBeRemoved;
    }

    abstract public String getCreatureType();

    public Float getPendingX() {
        return pendingX;
    }

    public Float getPendingY() {
        return pendingY;
    }


    public Vector2f getFacingVector() {
        return facingVector;
    }

    public void setFacingVector(Vector2f facingVector) {
        this.facingVector = facingVector;
    }

    public Vector2f getAttackingVector() {
        return attackingVector;
    }

    public void setAttackingVector(Vector2f attackingVector) {
        this.attackingVector = attackingVector;
    }

    public float getUnarmedDamage() {
        return unarmedDamage;
    }
}
