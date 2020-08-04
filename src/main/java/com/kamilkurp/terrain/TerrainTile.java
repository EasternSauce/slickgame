package com.kamilkurp.terrain;

import com.kamilkurp.Renderable;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public class TerrainTile implements Renderable {
    private Rectangle rect;
    private TerrainImage image;


    public TerrainTile(Rectangle rect, TerrainImage image) {
        this.rect = rect;
        this.image = image;
    }

    @Override
    public void render(Graphics g, Camera camera) {
        g.texture(new Rectangle((int)rect.getX() - (int)camera.getPosX(), (int)rect.getY() - (int)camera.getPosY(), rect.getWidth(), rect.getHeight()), image.getImage(), true);
    }

    public Rectangle getRect() {
        return rect;
    }

    public boolean isPassable() {
        return image.isPassable();
    }

    public void setImage(TerrainImage terrainImage) {
        this.image = terrainImage;
    }
}
