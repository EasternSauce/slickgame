package com.kamilkurp.projectile;

import com.kamilkurp.Renderable;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.terrain.TerrainTile;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

import java.util.List;

public class Arrow implements Renderable {

    private float posX;
    private float posY;

    private Vector2f speedVector;

    private float arrowSpeed = 0.2f;

    private Image arrowImage = Assets.projectileSpriteSheet.getSprite(0, 0).copy();

    private List<Arrow> arrowList;
    private List<TerrainTile> tiles;

    private boolean markedForDeletion = false;

    public Arrow(float x, float y, Vector2f facingVector, List<Arrow> arrowList, List<TerrainTile> tiles) {
        posX = x;
        posY = y;
        speedVector = facingVector;
        speedVector.normalise();
        arrowImage.setRotation((float)speedVector.getTheta());
        this.arrowList = arrowList;
        this.tiles = tiles;
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
    }

    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }
}
