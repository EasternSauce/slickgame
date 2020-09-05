package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.areagate.AreaGate;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.projectile.Arrow;
import com.kamilkurp.spawn.PlayerRespawnPoint;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.terrain.CurrentAreaManager;
import com.kamilkurp.terrain.TerrainTile;
import com.kamilkurp.utils.Camera;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Character extends Creature {
    private Sound stepSound = Assets.stepSound;

    private boolean walking = false;
    private boolean sprint = false;

    private boolean respawning;

    private Timer respawnTimer;

    private PlayerRespawnPoint currentRespawnPoint;

    private CurrentAreaManager areaManager;

    public Character(String id, int posX, int posY, Area area, LootSystem lootSystem, CurrentAreaManager areaManager) throws SlickException {
        super(id, posX, posY, area, lootSystem);

        this.areaManager = areaManager;

        respawning = false;
        respawnTimer = new Timer();
        setMaxHealthPoints(300f);

        currentRespawnPoint = area.getRespawnList().get(0);
    }



    @Override
    public void performActions(GameContainer gc, Map<String, Creature> creatures, KeyInput keyInput, List<Arrow> arrowList, List<TerrainTile> tiles) {
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

        if (input.isKeyDown(Input.KEY_LSHIFT)) {
            sprint = true;
        }
        else {
            sprint = false;
        }

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

        if (keyInput.isKeyPressed(KeyInput.Key.SPACE)) {
            attack(arrowList, tiles, creatures);
        }
        //rewrite
        if (keyInput.isKeyPressed(KeyInput.Key.E)) {
            interact(creatures);
        }
    }

    @Override
    public void render(Graphics g, Camera camera) {
        super.render(g, camera);


}

    @Override
    public void update(GameContainer gc, int i, List<TerrainTile> tiles, Map<String, Creature> creatures, KeyInput keyInput, List<Arrow> arrowList, List<AreaGate> gatesList) throws SlickException {
        super.update(gc, i ,tiles, creatures, keyInput, arrowList, gatesList);

        if (!isAlive()) {
            stepSound.stop();
        }

        if (runningTimer.getTime() > 200) {
            running = false;
        }

        if (attackingTimer.getTime() > 300) {
            attacking = false;
        }

        if (immunityTimer.getTime() > 500) {
            immune = false;
        }

        List<Arrow> toBeDeleted = new LinkedList<>();
        for (Arrow arrow : arrowList) {
            arrow.update(i);
            if (arrow.isMarkedForDeletion()) {
                toBeDeleted.add(arrow);
            }
        }

        arrowList.removeAll(toBeDeleted);


        if (respawning && respawnTimer.getTime() > 3000f) {
            respawning = false;
            setPosition(currentRespawnPoint.getPosX(), currentRespawnPoint.getPosY());
            areaToMove = currentRespawnPoint.getArea();
            setHealthPoints(getMaxHealthPoints());
            areaManager.setCurrentArea(currentRespawnPoint.getArea());
            areaToMove.reset();
        }



    }

    @Override
    protected void setFacingDirection(GameContainer gc) {
        int mouseX = gc.getInput().getMouseX();
        int mouseY = gc.getInput().getMouseY();

        float centerX = Globals.SCREEN_WIDTH * Globals.SCREEN_PROPORTION / 2;
        float centerY = Globals.SCREEN_HEIGHT * Globals.SCREEN_PROPORTION / 2;

        facingVector = new Vector2f(mouseX - centerX, mouseY - centerY);

        facingAngle = facingVector.getTheta();
    }


    private void interact(Map<String, Creature> creatures) {
        for (Creature creature : creatures.values()) {
            if (creature == this) continue;
            if (rect.intersects(creature.rect) && creature instanceof NPC && creature.healthPoints > 0) {
                ((NPC)creature).triggerDialogue();
            }
        }
    }

    @Override
    public void beforeMovement(int i) {
        moving = false;

        totalDirections = 0;

        dirX = 0;
        dirY = 0;

        if (sprint) {
            speed = 0.4f * i;
        }
        else {
            speed = 0.2f * i;
        }
    }

    @Override
    protected void onDeath() {
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
