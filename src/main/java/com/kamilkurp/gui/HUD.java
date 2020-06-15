package com.kamilkurp.gui;

import com.kamilkurp.Globals;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public class HUD {
    Rectangle bottomRect;
    Rectangle rightRect;

    Color color;

    public HUD() {
        int w = Globals.SCREEN_WIDTH;
        int h = Globals.SCREEN_HEIGHT;
        float proportion = Globals.SCREEN_PROPORTION;
        bottomRect = new Rectangle(0, h * proportion, w, h - h * proportion);
        rightRect = new Rectangle(w * proportion, 0, w - w * proportion, h);
        color = new Color(20,15,20);
    }

    public void render(Graphics g) {
        g.setColor(color);
        g.fill(bottomRect);
        g.fill(rightRect);
    }
}
