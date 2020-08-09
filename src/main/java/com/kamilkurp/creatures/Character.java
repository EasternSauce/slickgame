package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.KeyInput;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.items.LootSystem;
import com.kamilkurp.terrain.TerrainTile;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Character extends Creature {
    private Sound stepSound = Assets.stepSound;
    private Sound gruntSound = Assets.gruntSound;

    private boolean walking = false;
    private boolean sprint = false;

    private Vector2f directionVector;
    private double facingDirAngle;

    public Character(String id, int posX, int posY, Map<String, Creature> creatures, LootSystem lootSystem) throws SlickException {
        super(id, posX, posY, creatures, lootSystem);

        facingDirAngle = 0.0f;
        directionVector = new Vector2f(0f, 0f);

        attackAnimation.getAnimation(3).setLooping(false);
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
        }
        //rewrite
        if (keyInput.isKeyPressed(KeyInput.Key.E)) {
            interact(creatures);
        }
    }

    @Override
    public void render(Graphics g, Camera camera) {
        super.render(g, camera);

        if (attacking) {
            Image image = attackAnimation.getAnimation(3).getCurrentFrame();
            image.setRotation((float) facingDirAngle);

            g.drawImage(image, attackRect.getX() - camera.getPosX(), attackRect.getY() - camera.getPosY());
            //g.drawRect(attackShiftX + rect.getCenterX() - camera.getPosX() - attackWidth / 2f, attackShiftY + rect.getCenterY() - camera.getPosY() - attackHeight / 2f, attackWidth, attackHeight);

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

        int mouseX = gc.getInput().getMouseX();
        int mouseY = gc.getInput().getMouseY();

//        System.out.println(mouseX + " " + mouseY);

        float centerX = Globals.SCREEN_WIDTH * Globals.SCREEN_PROPORTION / 2;
        float centerY = Globals.SCREEN_HEIGHT * Globals.SCREEN_PROPORTION / 2;

        directionVector = new Vector2f(mouseX - centerX, mouseY - centerY);

        System.out.println("theta: " + directionVector.getTheta());

        facingDirAngle = directionVector.getTheta();

        float range = 60f;

        float attackShiftX = directionVector.getNormal().getX() * range;
        float attackShiftY = directionVector.getNormal().getY() * range;

        int attackWidth = 40;
        int attackHeight = 40;

        float attackRectX = attackShiftX + rect.getCenterX() - attackWidth / 2f;
        float attackRectY = attackShiftY + rect.getCenterY() - attackHeight / 2f;

        attackRect = new Rectangle(attackRectX, attackRectY, attackWidth, attackHeight);


        attackAnimation.getAnimation(3).update(i);

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
