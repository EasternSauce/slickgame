package com.kamilkurp.projectile;

import com.kamilkurp.Renderable;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;

public class Arrow implements Renderable {

    private float posX;
    private float posY;

    private Vector2f speedVector;

    private float arrowSpeed = 0.2f;

    private Image arrowImage = Assets.projectileSpriteSheet.getSprite(0, 0).copy();

    public Arrow(float x, float y, Vector2f facingVector) {
        posX = x;
        posY = y;
        speedVector = facingVector;
        speedVector.normalise();
        arrowImage.setRotation((float)speedVector.getTheta());
    }

    @Override
    public void render(Graphics g, Camera camera) {
        g.drawImage(arrowImage, posX - camera.getPosX(), posY - camera.getPosY());
    }

    public void update(int i) {
        posX = posX + speedVector.getX() * i * arrowSpeed;
        posY = posY + speedVector.getY() * i * arrowSpeed;
    }
}
