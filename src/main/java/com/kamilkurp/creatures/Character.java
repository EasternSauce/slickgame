package com.kamilkurp.creatures;

import com.kamilkurp.KeyInput;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.terrain.TerrainTile;
import org.newdawn.slick.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Character extends Creature {

    private Sound attackSound = new Sound("swoosh.wav");
    private Sound stepSound = new Sound("running.wav");
    private Sound gruntSound = new Sound("grunt.wav");

    private boolean walking = false;
    private boolean sprint = false;

    public Character(String id, int posX, int posY, Map<String, Creature> creatures, LootSystem lootSystem) throws SlickException {
        super(id, posX, posY, creatures, lootSystem);

    }

    @Override
    public void performActions(GameContainer gc, Collection<Creature> creatures, KeyInput keyInput) {
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
            attackSound.play(1.0f, 0.1f);
        }
        //rewrite
        if (keyInput.isKeyPressed(KeyInput.Key.E)) {
            interact(creatures);
        }
    }

    @Override
    public void update(GameContainer gc, int i, List<TerrainTile> tiles, Collection<Creature> creatures, KeyInput keyInput) {
        super.update(gc, i ,tiles, creatures, keyInput);

        if (healthPoints <= 0f) {
            stepSound.stop();
        }

        if (runningTimer.getTime() > 200) {
            running = false;
        }

        if (attackingTimer.getTime() > 200) {
            attacking = false;
        }

        if (immunityTimer.getTime() > 500) {
            immune = false;
        }

    }


    private void interact(Collection<Creature> creatures) {
        for (Creature creature : creatures) {
            if (creature == this) continue;
//                System.out.println(attackRect.getCenterX() + " " + attackRect.getCenterY() + " " + creature.rect.getCenterX() + " " + creature.rect.getCenterY());
            if (attackRect.intersects(creature.rect) && creature instanceof NPC) {
                ((NPC)creature).triggerDialogue();
                //creature.takeDamage();
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
//            System.out.println("took damage");

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
