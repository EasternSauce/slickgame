package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.abilities.Ability;
import com.kamilkurp.abilities.DashAbility;
import com.kamilkurp.projectile.Arrow;
import com.kamilkurp.spawn.PlayerRespawnPoint;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.utils.Camera;
import com.kamilkurp.utils.Timer;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.LinkedList;
import java.util.List;

public class PlayerCharacter extends Creature {
    private final Sound stepSound = Assets.stepSound;
    private final Sound flybySound = Assets.flybySound;


    private boolean walking = false;

    private boolean respawning;

    private Timer respawnTimer;

    private PlayerRespawnPoint currentRespawnPoint;

    private DashAbility dashAbility;


    private float staminaDrain = 0.0f;

    public PlayerCharacter(GameSystem gameSystem, String id) {
        super(gameSystem, id);

        scale = 1.0f;

        rect = new Rectangle(0, 0, 64 * scale, 64 * scale);
        hitbox = new Rectangle(0, 0, 64 * scale, 64 * scale);



        defineAbilities();


        dashAbility = new DashAbility(this);
        dashAbility.onStartChannelAction(() -> { flybySound.play(1.0f, 0.1f); });
        abilityList.add(dashAbility);


        swordAttackAbility.setAimed(true);
        unarmedAttackAbility.setAimed(true);
        tridentAttackAbility.setAimed(true);
    }



    @Override
    public void performActions(GameContainer gc, KeyInput keyInput) {

        Input input = gc.getInput();

        boolean movement = false;

        if (input.isKeyDown(Input.KEY_W)) {
            moveUp();
            movement = true;
        }
        if (input.isKeyDown(Input.KEY_A)) {
            moveLeft();
            movement = true;
        }
        if (input.isKeyDown(Input.KEY_S)) {
            moveDown();
            movement = true;
        }
        if (input.isKeyDown(Input.KEY_D)) {
            moveRight();
            movement = true;
        }

        if (input.isKeyDown(Input.KEY_SPACE)) {
            if (walking) {
                dashAbility.setDashVector(movementVector.getNormal());
                dashAbility.tryPerforming();
            }
        }

        sprinting = input.isKeyDown(Input.KEY_LSHIFT);

        if (!walking) {
            if (movement) {
                stepSound.loop(1.0f, 0.1f);
                walking = true;
            }
        }
        else {
            if (!movement) {
                stepSound.stop();
                walking = false;
            }
        }


        if (Mouse.isButtonDown(0)) {
            attack();
        }
        //rewrite
        if (keyInput.isKeyPressed(KeyInput.Key.E)) {
            interact();
        }

    }

    @Override
    public String getCreatureType() {
        return "playerCharacter";
    }

    @Override
    public void onInit() {
        currentRespawnPoint = area.getRespawnList().get(0);
        respawning = false;
        respawnTimer = new Timer();
        setMaxHealthPoints(300f);
        setHealthPoints(getMaxHealthPoints());
    }

    public void render(Graphics g, Camera camera) {
        super.render(g, camera);


}

    @Override
    public void update(GameContainer gc, int i, KeyInput keyInput, GameSystem gameSystem) {
//        System.out.println("character pos: " + rect.getX() + " " + rect.getY());

        if (isAlive()) {
            onUpdateStart(i);

            for (Ability ability : abilityList) {
                ability.performOnUpdateStart(i);
            }


            regenerate();

            if (!(gameSystem.getInventoryWindow().isInventoryOpen() || gameSystem.getDialogueWindow().isActivated() || gameSystem.getLootOptionWindow().isActivated())) {
                performActions(gc, keyInput);

                executeMovementLogic();

                setFacingDirection(gc);
            }


        } else {
            stepSound.stop();
        }

        for (Ability ability : abilityList) {
            ability.update(i);
        }

        if (runningTimer.getTime() > 200) {
            running = false;
        }

        if (immunityTimer.getTime() > 500) {
            immune = false;
        }

        List<Arrow> toBeDeleted = new LinkedList<>();
        for (Arrow arrow : area.getArrowList()) {
            arrow.update(i);
            if (arrow.isMarkedForDeletion()) {
                toBeDeleted.add(arrow);
            }
        }

        area.getArrowList().removeAll(toBeDeleted);


        if (respawning && respawnTimer.getTime() > 3000f) {
            respawning = false;
            //setPosition(currentRespawnPoint.getPosX(), currentRespawnPoint.getPosY());
            pendingArea = currentRespawnPoint.getArea();
            pendingX = (float)currentRespawnPoint.getPosX();
            pendingY = (float)currentRespawnPoint.getPosY();

            setHealthPoints(getMaxHealthPoints());


            gameSystem.getCurrentAreaHolder().setCurrentArea(currentRespawnPoint.getArea());
            gameSystem.resetArea();

            poisoned = false;

        }

        if (passedGateRecently) {
            onPassedGate(gameSystem.getGateList());
        }

        if (staminaDrain >= 300f) {

            takeStaminaDamage(8f);

            staminaDrain = 0.0f;
        }
    }

    @Override
    protected void setFacingDirection(GameContainer gc) {
        int mouseX = gc.getInput().getMouseX();
        int mouseY = gc.getInput().getMouseY();

        float centerX = Globals.SCREEN_WIDTH / 2f;
        float centerY = Globals.SCREEN_HEIGHT * Globals.SCREEN_PROPORTION / 2f;

        facingVector = new Vector2f(mouseX - centerX, mouseY - centerY);
    }


    private void interact() {

        if (gameSystem.getLootSystem().getVisibleItemsCount() == 0) {

            for (Creature creature : area.getCreatures().values()) {
                if (creature == this) continue;
                if (rect.intersects(creature.rect) && creature instanceof NonPlayerCharacter && creature.healthPoints > 0) {
                    ((NonPlayerCharacter) creature).triggerDialogue();
                }
            }
            for (PlayerRespawnPoint playerRespawnPoint : area.getRespawnList()) {
                if (rect.intersects(playerRespawnPoint.getRect())) {
                    currentRespawnPoint = playerRespawnPoint;
                    currentRespawnPoint.onRespawnSet();
                    if (getHealthPoints() < getMaxHealthPoints() / 2) {
                        setHealthPoints(getMaxHealthPoints() / 2);
                    }

                    gameSystem.getCurrentArea().softReset();
                }
            }
        }
    }

    @Override
    public void onUpdateStart(int i) {
        moving = false;

        totalDirections = 0;

        knockbackSpeed = knockbackPower * i;

        dirX = 0;
        dirY = 0;

        if (sprinting && staminaPoints > 0) {
            speed = 0.4f * i;
            staminaDrain += i;
        }
        else {
            speed = 0.2f * i;
        }

    }

    @Override
    public void onDeath() {
        respawnTimer.reset();
        respawning = true;

        for (Ability ability : abilityList) {
            ability.stopAbility();
        }
    }


    public boolean isRespawning() {
        return respawning;
    }

    public void setRespawning(boolean respawning) {
        this.respawning = respawning;
    }

    public Area getRespawnArea() {
        return currentRespawnPoint.getArea();
    }

    public PlayerRespawnPoint getCurrentRespawnPoint() {
        return currentRespawnPoint;
    }

    public void setCurrentRespawnPoint(PlayerRespawnPoint currentRespawnPoint) {
        this.currentRespawnPoint = currentRespawnPoint;
    }
}
