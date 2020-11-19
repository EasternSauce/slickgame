package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.behavior.Ability;
import com.kamilkurp.behavior.DashAbility;
import com.kamilkurp.projectile.Arrow;
import com.kamilkurp.spawn.PlayerRespawnPoint;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.utils.Camera;
import com.kamilkurp.utils.Timer;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;

import java.util.LinkedList;
import java.util.List;

public class PlayerCharacter extends Creature {
    private final Sound stepSound = Assets.stepSound;

    private boolean walking = false;

    private boolean respawning;

    private Timer respawnTimer;

    private PlayerRespawnPoint currentRespawnPoint;

    private DashAbility dashAbility;


    private float staminaDrain = 0.0f;

    public PlayerCharacter(GameSystem gameSystem, String id) {
        super(gameSystem, id);

        dashAbility = new DashAbility(this);
        abilityList.add(dashAbility);
    }



    @Override
    public void performActions(GameContainer gc, KeyInput keyInput) {
        if (gameSystem.getInventoryWindow().isInventoryOpen()) return;

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
            dashAbility.setDashVector(facingVector.normalise());
            dashAbility.tryPerforming();
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
    }

    @Override
    public void render(Graphics g, Camera camera) {
        super.render(g, camera);


}

    @Override
    public void update(GameContainer gc, int i, KeyInput keyInput, GameSystem gameSystem) {
        if (isAlive()) {
            onUpdateStart(i);

            for (Ability ability : abilityList) {
                ability.performOnUpdateStart(i);
            }


            regenerate();

            if (!gameSystem.getDialogueWindow().isActivated()) {
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

        float centerX = Globals.SCREEN_WIDTH * Globals.SCREEN_PROPORTION / 2;
        float centerY = Globals.SCREEN_HEIGHT * Globals.SCREEN_PROPORTION / 2;

        facingVector = new Vector2f(mouseX - centerX, mouseY - centerY);
    }


    private void interact() {

        for (Creature creature : area.getCreatures().values()) {
            if (creature == this) continue;
            if (rect.intersects(creature.rect) && creature instanceof NonPlayerCharacter && creature.healthPoints > 0) {
                ((NonPlayerCharacter)creature).triggerDialogue();
            }
        }
    }

    @Override
    public void onUpdateStart(int i) {
        moving = false;

        totalDirections = 0;

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

    }


    public boolean isRespawning() {
        return respawning;
    }

    public void setRespawning(boolean respawning) {
        this.respawning = respawning;
    }

}
