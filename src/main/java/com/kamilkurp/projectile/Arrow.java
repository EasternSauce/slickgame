package com.kamilkurp.projectile;

import com.kamilkurp.Globals;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.Mob;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.terrain.TerrainTile;
import com.kamilkurp.utils.Camera;
import com.kamilkurp.utils.Rect;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.List;
import java.util.Map;

public class Arrow {

    private float posX;
    private float posY;

    private Vector2f speedVector;

    private final float arrowSpeed = 0.4f;

    private final Image arrowImage = Assets.projectileSpriteSheet.getSprite(0, 0).copy();

    private final List<TerrainTile> tiles;
    private final Map<String, Creature> creatures;

    private boolean markedForDeletion = false;

    private final Rectangle hitbox;

    private final Creature shooter;

    private Area area;

    public Arrow(float x, float y, Area area, Vector2f facingVector, List<Arrow> arrowList, List<TerrainTile> tiles, Map<String, Creature> creatures, Creature shooter) {
        posX = x;
        posY = y;

        speedVector = facingVector;
        speedVector.normalise();
        arrowImage.setRotation((float)speedVector.getTheta());
        this.tiles = tiles;
        this.creatures = creatures;

        hitbox = new Rectangle(19, 19, 2, 2);

        this.shooter = shooter;

        this.area = area;
    }

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

        int tilesetColumns = area.getTerrainColumns();
        int tilesetRows = area.getTerrainRows();

        int startColumn = ((int)(newPosX / 64f) - 1) < 0f ? 0 : ((int)(newPosX / 64f) - 1);
        int startRow = ((int)(newPosY / 64f) - 1) < 0f ? 0 : ((int)(newPosY / 64f) - 1);


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int column = startColumn + j >= tilesetColumns ? tilesetColumns - 1 : startColumn + j;
                int row = startRow + i >= tilesetRows ? tilesetRows -1 : startRow + i;
                TerrainTile tile = tiles.get(tilesetColumns * row + column);

                if (tile.isPassable()) continue;

                Rectangle tileRect = tile.getRect();
                Rect rect1 = new Rect(tileRect.getX(), tileRect.getY(), tileRect.getWidth(), tileRect.getHeight());

                Rect rect2 = new Rect(newPosX + hitbox.getX(), newPosY + hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());


                if(Globals.intersects(rect1, rect2)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isCollidingWithCreatures(Map<String, Creature> creatures, float newPosX, float newPosY) {
        for(Creature creature : creatures.values()) {
            if (creature == shooter) continue;

            Rectangle creatureRect = creature.getRect();

            Rectangle arrowRect = new Rectangle(newPosX + hitbox.getX(), newPosY + hitbox.getY(), hitbox.getWidth(), hitbox.getHeight());

            if (!(shooter instanceof Mob && creature instanceof Mob)) { // mob can't hurt a mob?

                if (creatureRect.intersects(arrowRect)) {
                    if (speedVector.equals(new Vector2f(0f, 0f)) || creature.getHealthPoints() <= 0.0f) return false;
                    if (!creature.isImmune()) {
                        creature.takeDamage(shooter.getEquipmentItems().get(0).getItemType().getMaxDamage(), true);
                    }
                    return true;
                }
            }
        }
        return false;
    }

}
