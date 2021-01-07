package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.abilities.Ability;
import com.kamilkurp.abilities.DashAbility;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.effect.Effect;
import com.kamilkurp.spawn.PlayerRespawnPoint;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.utils.Camera;
import com.kamilkurp.utils.Timer;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class PlayerCharacter extends Creature {
    private final Sound stepSound = Assets.stepSound;
    private final Sound flybySound = Assets.flybySound;


    private boolean walking = false;

    private boolean respawning;

    private Timer respawnTimer;

    private PlayerRespawnPoint currentRespawnPoint;

    private DashAbility dashAbility;

    private Music fireDemonMusic = Assets.fireDemon;


    private float staminaDrain = 0.0f;

    public PlayerCharacter(GameSystem gameSystem, String id) {
        super(gameSystem, id);

        scale = 1.0f;

        rect = new Rectangle(0, 0, 64 * scale, 64 * scale);
        //hitbox = new Rectangle(0, 0, 64 * scale, 64 * scale);

        hitbox = new Rectangle(17 * scale, 15 * scale, 30 * scale, 46 * scale);

        respawnTimer = new Timer();

        creatureType = "playerCharacter";
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
            if (dashAbility.canPerform() && walking) {
                dashAbility.setDashVector(movementVector.getNormal());
                dashAbility.perform();
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
            if (!effectMap.get("immobility").isActive()) {
                if (currentAttack.canPerform()) {
                    currentAttack.perform();
                }
            }
        }
        //rewrite
        if (keyInput.isKeyPressed(KeyInput.Key.E)) {
            interact();
        }

    }

    @Override
    public void onInit() {
        currentRespawnPoint = area.getRespawnList().get(0);
        respawning = false;

        setMaxHealthPoints(300f);
        setHealthPoints(getMaxHealthPoints());

        defineStandardAbilities();
        defineCustomAbilities();
        updateAttackType();
    }

    @Override
    protected void defineCustomAbilities() {
        dashAbility = DashAbility.newInstance(this);
        dashAbility.onStartChannelAction(() -> { flybySound.play(1.0f, 0.1f); });
        abilityList.add(dashAbility);

//        swordAttackAbility.setAimed(true);
//        unarmedAttackAbility.setAimed(true);
//        tridentAttackAbility.setAimed(true);
    }

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

            currentAttack.performOnUpdateStart(i);



            if (!(gameSystem.getInventoryWindow().isInventoryOpen() || gameSystem.getDialogueWindow().isActivated() || gameSystem.getLootOptionWindow().isActivated())) {
                performActions(gc, keyInput);

                executeMovementLogic();

                setFacingDirection(gc);
            }

            regenerate();

            for (Ability ability : abilityList) {
                ability.update(i);
            }

            currentAttack.update(i);

            if (runningTimer.getElapsed() > 200) {
                running = false;
            }

        } else {
            stepSound.stop();
        }

        for (Effect effect : effectMap.values()) {
            effect.update();
        }

        if (respawning && respawnTimer.getElapsed() > 3000f) {
            respawning = false;

            pendingArea = currentRespawnPoint.getArea();
            pendingX = (float)currentRespawnPoint.getPosX();
            pendingY = (float)currentRespawnPoint.getPosY();

            setHealthPoints(getMaxHealthPoints());
            setStaminaPoints(getMaxStaminaPoints());
            isAttacking = false;
            staminaOveruse = false;

            effectMap.get("staminaRegenStopped").stop();

            gameSystem.getCurrentAreaHolder().setCurrentArea(currentRespawnPoint.getArea());
            gameSystem.resetArea();

            poisoned = false;

            gameSystem.stopBossBattleMusic();
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
            speed = baseSpeed * i;
        }

        if (isAttacking) {
            speed = speed / 2f;
        }

    }

    @Override
    public void onDeath() {
        respawnTimer.reset();
        respawning = true;
        running = false;

        for (Ability ability : abilityList) {
            ability.stopAbility();
        }

        currentAttack.stopAbility();

        gameSystem.getHud().getBossHealthBar().hide();
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
