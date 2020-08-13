package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.projectile.Arrow;
import com.kamilkurp.terrain.TerrainTile;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Character extends Creature {
    private Sound stepSound = Assets.stepSound;
    private Sound gruntSound = Assets.gruntSound;

    private boolean walking = false;
    private boolean sprint = false;



    public Character(String id, int posX, int posY, Map<String, Creature> creatures, LootSystem lootSystem) throws SlickException {
        super(id, posX, posY, creatures, lootSystem);



        attackAnimation.getAnimation().setLooping(false);
    }

    @Override
    public void performActions(GameContainer gc, Collection<Creature> creatures, KeyInput keyInput, List<Arrow> arrowList) {
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
            attack();

            Arrow arrow = new Arrow(rect.getX(), rect.getY(), facingVector);

            arrowList.add(arrow);
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
    public void update(GameContainer gc, int i, List<TerrainTile> tiles, Collection<Creature> creatures, KeyInput keyInput, List<Arrow> arrowList) {
        super.update(gc, i ,tiles, creatures, keyInput, arrowList);

        if (healthPoints <= 0f) {
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

        for (Arrow arrow : arrowList) {
            arrow.update(i);
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


    private void interact(Collection<Creature> creatures) {
        for (Creature creature : creatures) {
            if (creature == this) continue;
            if (attackRect.intersects(creature.rect) && creature instanceof NPC && creature.healthPoints > 0) {
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
    public void takeDamage() {
        if (!immune) {
            float damage = 0f;
            if (healthPoints - damage > 0) healthPoints -= damage;
            else healthPoints = 0f;

            immunityTimer.reset();
            immune = true;
            gruntSound.play(1.0f, 0.1f);

        }

    }

    @Override
    protected void onDeath() {

    }


}
