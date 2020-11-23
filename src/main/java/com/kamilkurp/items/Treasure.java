package com.kamilkurp.items;

import com.kamilkurp.terrain.Area;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class Treasure extends LootPile {
    public Treasure(Area area, float x, float y) {
        super(area, x, y);
    }

    @Override
    public void render(Graphics g, Camera camera) {
        if (visible) {
            g.setColor(Color.pink);
            g.fillRect(rect.getX() - camera.getPosX(), rect.getY() - camera.getPosY(), rect.getWidth(), rect.getHeight());
        }
    }
}
