package com.kamilkurp.projectile;

import com.kamilkurp.Renderable;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.terrain.TerrainTile;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.List;

public class Arrow implements Renderable {

    private float posX;
    private float posY;

    private Vector2f speedVector;

    private float arrowSpeed = 0.6f;

    private Image arrowImage = Assets.projectileSpriteSheet.getSprite(0, 0).copy();

    private List<Arrow> arrowList;
    private List<TerrainTile> tiles;
    private List<Creature> creatures;


    private Rectangle rect;

    private final int ARROW_WIDTH = 40;
    private final int ARROW_HEIGHT = 40;

    private boolean markedForDeletion = false;

    private Rectangle hitbox;

    private Creature shooter;

    public Arrow(float x, float y, Vector2f facingVector, List<Arrow> arrowList, List<TerrainTile> tiles, List<Creature> creatures, Creature shooter) {
        posX = x;
        posY = y;

        rect = new Rectangle(x + ARROW_WIDTH / 2f, y + ARROW_HEIGHT / 2f, ARROW_WIDTH, ARROW_HEIGHT);
        speedVector = facingVector;
        speedVector.normalise();
        arrowImage.setRotation((float)speedVector.getTheta());
        this.arrowList = arrowList;
        this.tiles = tiles;
        this.creatures = creatures;

        hitbox = new Rectangle(19, 19, 2, 2);

        this.shooter = shooter;
    }

    @Override
    public void render(Graphics g, Camera camera) {
        g.drawImage(arrowImage, posX - camera.getPosX(), posY - camera.getPosY());
    }

    public void update(int i) {
        posX = posX + speedVector.getX() * i * arrowSpeed;
        posY = posY + speedVector.getY() * i * arrowSpeed;

        int margin = 50;
        if (!((posX >= 0 - margin && posX < tiles.get(tiles.size() - 1).getRect().getX() + margin) && (posY >= 0 - margin && posY < tiles.get(tiles.size() - 1).getRect().getY() + margin))) {
            markedForDeletion = true;
        }

        if (isCollidingWithEnvironment(tiles, posX, posY)) {
            speedVector = new Vector2f(0f, 0f);
        }

        if (isCollidingWithCreatures(creatures, posX, posY)) {
            markedForDeletion = true;
        }


    }

    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }



    public boolean isCollidingWithEnvironment(List<TerrainTile> tiles, float newPosX, float newPosY) {
        for(TerrainTile tile : tiles) {
            if (tile.isPassable()) continue;

            Rectangle tileRect = tile.getRect();
            Rectangle rect1 = new Rectangle(tileRect.getX(), tileRect.getY(), tileRect.getWidth(), tileRect.getHeight());

            Rectangle rect2 = new Rectangle(newPosX + hitbox.getX(), newPosY + hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());

            if(rect1.intersects(rect2)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCollidingWithCreatures(List<Creature> creatures, float newPosX, float newPosY) {
        for(Creature creature : creatures) {
            if (creature == shooter) continue;

            Rectangle creatureRect = creature.getRect();

            Rectangle arrowRect = new Rectangle(newPosX + hitbox.getX(), newPosY + hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());

            if(creatureRect.intersects(arrowRect)) {
                if (speedVector.equals(new Vector2f(0f, 0f)) || creature.getHealthPoints() <= 0.0f) return false;
                creature.takeDamage(shooter.getEquipmentItems().get(0).getItemType().getMaxDamage());
                return true;
            }
        }
        return false;
    }

}
