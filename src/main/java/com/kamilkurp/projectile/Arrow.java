package com.kamilkurp.projectile;

import com.kamilkurp.Renderable;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class Arrow implements Renderable {

    private float posX;
    private float posY;

    private Image arrowImage = Assets.projectileSpriteSheet.getSprite(0, 0);

    public Arrow(float x, float y) {
        posX = x;
        posY = y;
    }

    @Override
    public void render(Graphics g, Camera camera) {
        g.drawImage(arrowImage, posX - camera.getPosX(), posY - camera.getPosY());
    }
}
