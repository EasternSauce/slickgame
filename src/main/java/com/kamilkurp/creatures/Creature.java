package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.abilities.*;
import com.kamilkurp.animations.WalkAnimation;
import com.kamilkurp.areagate.AreaGate;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.items.Item;
import com.kamilkurp.items.ItemType;
import com.kamilkurp.spawn.Blockade;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.terrain.TerrainTile;
import com.kamilkurp.utils.Camera;
import com.kamilkurp.utils.Rect;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.*;

public abstract class Creature {

    protected Rectangle rect;

    protected Rectangle hitbox;

    protected String id;

    protected boolean running = false;

//    protected boolean attacking = false;

    private final Sound gruntSound = Assets.painSound;

    protected int direction = 0;


    protected Timer runningTimer;

    protected boolean moving;

    protected int totalDirections;

    protected int dirX;
    protected int dirY;

    protected float speed = 0f;

    protected float maxHealthPoints = 100f;
    protected float healthPoints = maxHealthPoints;

    protected float maxStaminaPoints = 100f;
    protected float staminaPoints = maxStaminaPoints;

    protected Timer immunityTimer;

    protected boolean immune;

    protected Vector2f facingVector;
    protected Vector2f attackingVector;


    protected WalkAnimation walkAnimation;

    protected Map<Integer, Item> equipmentItems;

    protected float healthRegen = 0.3f;
    protected float staminaRegen = 10f;

    protected Timer healthRegenTimer;
    protected Timer staminaRegenTimer;
    protected Timer poisonTickTimer;
    protected Timer poisonTimer;

    protected Area area;

    protected boolean passedGateRecently = false;

    protected Area pendingArea;

    protected boolean toBeRemoved;

    protected Float pendingX;
    protected Float pendingY;

    protected GameSystem gameSystem;


    protected boolean immobilized = false;

    protected List<Ability> abilityList;
    protected List<Attack> attackList;

    protected Attack currentAttack;

    protected BowAttack bowAttack;
    protected UnarmedAttack unarmedAttack;
    protected SwordAttack swordAttack;
    protected TridentAttack tridentAttack;

    protected float unarmedDamage;

    protected boolean sprinting;

    protected Timer staminaOveruseTimer;

    protected int staminaOveruseTime = 1300;

    protected boolean staminaOveruse;

    protected boolean poisoned;

    protected int poisonTickTime = 1500;

    protected int poisonTime = 20000;
    protected float knockbackPower;

    private Timer healingTimer;
    private Timer healingTickTimer;
    private boolean healing;

    protected int healingTickTime = 300;

    protected int healingTime = 8000;
    private float healingPower;

    private boolean staminaRegenActive = true;

    protected boolean knockback = false;
    protected Timer knockbackTimer;

    protected Vector2f knockbackVector;

    protected float knockbackSpeed;

    protected Vector2f movementVector;

    protected float startingPosX;
    protected float startingPosY;

    protected float scale;
    
    protected boolean isAttacking;

    protected String name;

    protected boolean knocbackable;

    protected boolean isBoss;

    protected Map<String, Float> dropTable;

    protected Sound onGettingHitSound;

    protected float baseSpeed;


    public Creature(GameSystem gameSystem, String id) {
        this.gameSystem = gameSystem;
        this.id = id;
        rect = new Rectangle(0, 0, 64, 64);
        hitbox = new Rectangle(2, 2, 60, 60);

        walkAnimation = new WalkAnimation(Assets.male1SpriteSheet, 3, 100, new int[]{3, 1, 0, 2}, 1);

        runningTimer = new Timer();
        immunityTimer = new Timer();
        healthRegenTimer = new Timer();
        staminaRegenTimer = new Timer();
        knockbackTimer = new Timer();

        staminaRegenTimer.start();

        facingVector = new Vector2f(0f, 0f);

        equipmentItems = new TreeMap<>();

        abilityList = new LinkedList<>();

        toBeRemoved = false;

        pendingX = 0.0f;
        pendingY = 0.0f;

        unarmedDamage = 15f;

        sprinting = false;

        staminaOveruseTimer = new Timer();

        staminaOveruse = false;

        attackingVector = new Vector2f(0f, 0f);

        poisonTickTimer = new Timer();
        poisonTimer = new Timer();

        poisonTickTimer.setElapsed(poisonTickTime);
        poisonTimer.setElapsed(poisonTime);

        healingTimer = new Timer();
        healingTickTimer = new Timer();
        healing = false;

        healingPower = 0f;

        knockbackPower = 0f;

        movementVector = new Vector2f(0f,0f);

        scale = 1.0f;

        knocbackable = true;

        isBoss = false;

        dropTable = new HashMap<>();

        onGettingHitSound = Assets.painSound;

        baseSpeed = 0.2f;
    }

    public void defineStandardAbilities() {

        abilityList = new LinkedList<>();
        attackList = new LinkedList<>();


        bowAttack = BowAttack.newInstance(this);
        unarmedAttack = UnarmedAttack.newInstance(this);
        swordAttack = SwordAttack.newInstance(this);
        tridentAttack = TridentAttack.newInstance(this);

        attackList.add(bowAttack);
        attackList.add(unarmedAttack);
        attackList.add(swordAttack);
        attackList.add(tridentAttack);

        currentAttack = unarmedAttack;
    }

    public void onInit() {
        defineStandardAbilities();

        defineCustomAbilities();

        updateAttackType();
    }

    protected void defineCustomAbilities() {

    }

    public void render(Graphics g, Camera camera) {
        Image sprite = walkAnimation.getRestPosition(direction);

        if (Globals.SHOW_HITBOXES) {
            g.setColor(Color.pink);
            g.fillRect(rect.getX() - camera.getPosX(), rect.getY() - camera.getPosY(), rect.getWidth(), rect.getHeight());
        }

        if (!running) {
            if (!isAlive()) {
                sprite.rotate(90f);
            }
            sprite.draw(rect.getX() - camera.getPosX(), rect.getY() - camera.getPosY(), rect.getWidth(), rect.getHeight());
            if (isAlive() && immune && (immunityTimer.getElapsed() % 250) < 125) {
                sprite.draw(rect.getX() - camera.getPosX(), rect.getY() - camera.getPosY(), rect.getWidth(), rect.getHeight(), Color.red);
            }
            else {
                sprite.draw(rect.getX() - camera.getPosX(), rect.getY() - camera.getPosY(), rect.getWidth(), rect.getHeight());

            }
        } else {
            if (isAlive() && immune && (immunityTimer.getElapsed() % 250) < 125) {
                walkAnimation.getAnimation(direction).draw((int) rect.getX() - (int) camera.getPosX(), (int) rect.getY() - (int) camera.getPosY(), rect.getWidth(), rect.getHeight(), Color.red);
            }
            else {
                walkAnimation.getAnimation(direction).draw((int) rect.getX() - (int) camera.getPosX(), (int) rect.getY() - (int) camera.getPosY(), rect.getWidth(), rect.getHeight());

            }

        }

        Assets.verdanaTtf.drawString((int) rect.getX() - (int) camera.getPosX(), (int) rect.getY() - (int) camera.getPosY() - 30f, (int) Math.ceil(healthPoints) + "/" + (int) Math.ceil(getMaxHealthPoints()), Color.red);
    }

    public void renderAbilities(Graphics g, Camera camera) {
        for (Ability ability : abilityList) {
            ability.render(g, camera);
        }
        currentAttack.render(g, camera);
    }

    public void update(GameContainer gc, int i, KeyInput keyInput, GameSystem gameSystem) {
        walkAnimation.getAnimation(direction).update(i);

        if (isAlive()) {

            onUpdateStart(i);

            for (Ability ability : abilityList) {
                ability.performOnUpdateStart(i);
            }
            currentAttack.performOnUpdateStart(i);

            performActions(gc, keyInput);

            executeMovementLogic();

            setFacingDirection(gc);

            regenerate();

            for (Ability ability : abilityList) {
                ability.update(i);
            }

            currentAttack.update(i);
        }




    }

    public void onPassedGate(List<AreaGate> gatesList) {
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

    public void regenerate() {
        if (healthRegenTimer.getElapsed() > 500f) {
            heal(healthRegen);
            healthRegenTimer.reset();
        }

        if (staminaRegenActive && !sprinting) {
            if (staminaRegenTimer.getElapsed() > 250f && !isAbilityActive() && !staminaOveruse) {
                if (getStaminaPoints() < getMaxStaminaPoints()) {
                    float afterRegen = getStaminaPoints() + staminaRegen;
                    staminaPoints = Math.min(afterRegen, getMaxStaminaPoints());
                }
                staminaRegenTimer.reset();
            }
        }

        if (staminaOveruse) {
            if (staminaOveruseTimer.getElapsed() > staminaOveruseTime) {
                staminaOveruse = false;
            }
        }

        if (poisoned) {
            if (poisonTickTimer.getElapsed() > poisonTickTime) {
                takeDamage(15f, false, 0, 0, 0);
                poisonTickTimer.reset();
            }
            if (poisonTimer.getElapsed() > poisonTime) {
                poisoned = false;
            }
        }

        if (healing) {
            if (healingTickTimer.getElapsed() > healingTickTime) {
                heal(healingPower);
                healingTickTimer.reset();
            }
            if (healingTimer.getElapsed() > healingTime || getHealthPoints() >= getMaxHealthPoints()) {
                healing = false;
            }
        }
    }

    private boolean isAbilityActive() {
        boolean abilityActive = false;

        for (Ability ability : abilityList) {
            if (ability.isActive()) {
                abilityActive = true;
                break;
            }
        }

        if (currentAttack.isActive()) {
            return true;
        }

        return abilityActive;
    }

    private void heal(float healValue) {

        if (getHealthPoints() < getMaxHealthPoints()) {
            float afterHeal = getHealthPoints() + healValue;

            healthPoints = Math.min(afterHeal, getMaxHealthPoints());
        }

    }

    protected abstract void setFacingDirection(GameContainer gc);

    public void becomePoisoned() {
        poisoned = true;
        poisonTickTimer.reset();
        poisonTimer.reset();
    }

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

            if (knocbackable && !knockback && knockbackPower > 0f) {
                this.knockbackPower = knockbackPower;

                knockbackVector = new Vector2f(rect.getX() - sourceX, rect.getY() - sourceY).getNormal();
                knockback = true;
                knockbackTimer.reset();

            }

            onGettingHitSound.play(1.0f, 0.1f);
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

    public boolean isCollidingX(List<TerrainTile> tiles, List<Blockade> blockadeList, float newPosX, float newPosY) {

        int tilesetColumns = area.getTerrainColumns();
        int tilesetRows = area.getTerrainRows();


        int startColumn = ((int)(newPosX / 64f) - 4) < 0f ? 0 : ((int)(newPosX / 64f) - 4);
        int startRow = ((int)(newPosY / 64f) - 4) < 0f ? 0 : ((int)(newPosY / 64f) - 4);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int column = startColumn + j >= tilesetColumns ? tilesetColumns - 1 : startColumn + j;
                int row = startRow + i >= tilesetRows ? tilesetRows -1 : startRow + i;
                TerrainTile tile = tiles.get(tilesetColumns * row + column);

                if (tile.isPassable()) continue;

                Rectangle tileRect = tile.getRect();
                Rect rect1 = new Rect(tileRect.getX(), tileRect.getY(), tileRect.getWidth(), tileRect.getHeight());

                Rect rect2 = new Rect(newPosX + hitbox.getX(), rect.getY() + hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());

                if (Globals.intersects(rect1, rect2)) {
                    return true;
                }
            }
        }

        Rect creatureRect = new Rect(newPosX + hitbox.getX(), rect.getY() + hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());

        for (Blockade blockade : blockadeList) {
            if (blockade.isActive()) {
                if (Globals.intersects(blockade.getRect(), creatureRect)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isCollidingY(List<TerrainTile> tiles, List<Blockade> blockadeList, float newPosX, float newPosY) {

        int tilesetColumns = area.getTerrainColumns();
        int tilesetRows = area.getTerrainRows();

        int startColumn = ((int)(newPosX / 64f) - 4) < 0f ? 0 : ((int)(newPosX / 64f) - 4);
        int startRow = ((int)(newPosY / 64f) - 4) < 0f ? 0 : ((int)(newPosY / 64f) - 4);


        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int column = startColumn + j >= tilesetColumns ? tilesetColumns - 1 : startColumn + j;
                int row = startRow + i >= tilesetRows ? tilesetRows -1 : startRow + i;
                TerrainTile tile = tiles.get(tilesetColumns * row + column);

                if (tile.isPassable()) continue;

                Rectangle tileRect = tile.getRect();
                Rect rect1 = new Rect(tileRect.getX(), tileRect.getY(), tileRect.getWidth(), tileRect.getHeight());

                Rect rect2 = new Rect(rect.getX() + hitbox.getX(), newPosY + hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());

                if (Globals.intersects(rect1, rect2)) {
                    return true;
                }
            }
        }

        Rect creatureRect = new Rect(rect.getX() + hitbox.getX(), newPosY + hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());

        for (Blockade blockade : blockadeList) {
            if (blockade.isActive()) {
                if (Globals.intersects(blockade.getRect(), creatureRect)) {
                    return true;
                }
            }
        }


        return false;
    }



    public void onUpdateStart(int i) {
        moving = false;

        totalDirections = 0;

        knockbackSpeed = knockbackPower * i;

        dirX = 0;
        dirY = 0;

        speed = baseSpeed * i;

        if (isAttacking) {
            speed = speed / 2f;
        }

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


    public void executeMovementLogic() {
        List<TerrainTile> tiles = area.getTiles();

        if (!immobilized && !knockback) {
            if (totalDirections > 1) {
                speed /= Math.sqrt(2);
            }

            float newPosX = rect.getX() + speed * dirX;
            float newPosY = rect.getY() + speed * dirY;

            List<Blockade> blockadeList = gameSystem.getCurrentArea().getBlockadeList();

            if (!isCollidingX(tiles, blockadeList, newPosX, newPosY) && newPosX >= 0 && newPosX < tiles.get(tiles.size() - 1).getRect().getX()) {
                move(speed * dirX, 0);
                movementVector.x = speed * dirX;
            }
            else {
                movementVector.x = 0;
            }

            if (!isCollidingY(tiles, blockadeList, newPosX, newPosY) && newPosY >= 0 && newPosY < tiles.get(tiles.size() - 1).getRect().getY()) {
                move(0, speed * dirY);
                movementVector.y = speed * dirY;
            }
            else {
                movementVector.y = 0;
            }

            if (moving) {
                runningTimer.reset();
                running = true;
            }
        }

        if (knockback) {

            float newPosX = rect.getX() + knockbackSpeed * knockbackVector.getX();
            float newPosY = rect.getY() + knockbackSpeed * knockbackVector.getY();

            List<Blockade> blockadeList = gameSystem.getCurrentArea().getBlockadeList();

            if (!isCollidingX(tiles, blockadeList, newPosX, newPosY) && newPosX >= 0 && newPosX < tiles.get(tiles.size() - 1).getRect().getX()) {
                move(knockbackSpeed * knockbackVector.getX(), 0);
            }

            if (!isCollidingY(tiles, blockadeList, newPosX, newPosY) && newPosY >= 0 && newPosY < tiles.get(tiles.size() - 1).getRect().getY()) {
                move(0, knockbackSpeed * knockbackVector.getY());

            }

            if (knockbackTimer.getElapsed() > 200f) {
                knockback = false;
            }
        }

        for (Ability ability : abilityList) {
            ability.performMovement();
        }

        currentAttack.performMovement();

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
        if (equipmentItems.get(4) != null && equipmentItems.get(4).getItemType().getId().equals("lifeRing")) return maxHealthPoints * 1.35f;
        return maxHealthPoints;
    }

    public float getMaxStaminaPoints() {
        return maxStaminaPoints;
    }

    public float getStaminaPoints() {
        return staminaPoints;
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

    public void takeStaminaDamage(float staminaDamage) {
        if (staminaPoints - staminaDamage > 0) staminaPoints -= staminaDamage;
        else {
            staminaPoints = 0f;
            staminaOveruse = true;
            staminaOveruseTimer.reset();
        }

    }

    public void useItem(Item item) {
        if (item.getItemType().getId().equals("healingPowder")) {
            startHealing(10f);
        }
    }

    private void startHealing(float healingPower) {
        healingTimer.reset();
        healingTickTimer.reset();
        healing = true;
        this.healingPower = healingPower;
    }

    public void stopStaminaRegen() {
        staminaRegenActive = false;
    }

    public void startStaminaRegen() {
        staminaRegenActive = true;
    }

    public void reset() {
        setHealthPoints(getMaxHealthPoints());
        rect.setX(startingPosX);
        rect.setY(startingPosY);
    }

    public void onAttack() {
        if (equipmentItems.get(4) != null && equipmentItems.get(4).getItemType().getId().equals("thiefRing")) {
            heal(7f);
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

    public boolean isImmune() {
        return immune;
    }

    public void setStartingPosX(float startingPosX) {
        this.startingPosX = startingPosX;
    }

    public void setStartingPosY(float startingPosY) {
        this.startingPosY = startingPosY;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public void setAttacking(boolean attacking) {
        isAttacking = attacking;
    }

    public boolean isStaminaOveruse() {
        return staminaOveruse;
    }

    public void setStaminaPoints(float staminaPoints) {
        this.staminaPoints = staminaPoints;
    }

    public void updateAttackType() {
        Item weapon = equipmentItems.get(0);
        if (weapon != null) {
            ItemType weaponItemType = weapon.getItemType();
            Attack attackAbility = attackList.stream().filter(attack -> attack.getAttackType().equals(weaponItemType.getAttackType())).findAny().get();
            currentAttack = attackAbility;
        }
        else {
            Attack attackAbility = attackList.stream().filter(attack -> attack.getAttackType().equals(AttackType.UNARMED)).findAny().get();
            currentAttack = attackAbility;
        }
    }

    public boolean isNoAbilityActive() {
        for (Ability ability : abilityList) {
            if (ability.isActive()) return false;
        }
        return true;
    }

    public String getName() {
        if (name != null) return name;
        return id;
    }

    public void onAggroed() {

    }

    public boolean isBoss() {
        return isBoss;
    }
}
