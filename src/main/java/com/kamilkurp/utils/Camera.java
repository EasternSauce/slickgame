package com.kamilkurp.utils;

import com.kamilkurp.Globals;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Rectangle;

public class Camera {
    private float posX = 0;
    private float posY = 0;

    public void setPos(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public void update(GameContainer gc, Rectangle rect) {
        float gameplayScreenProportion = Globals.SCREEN_PROPORTION;
        setPos(rect.getX() - Globals.SCREEN_WIDTH* gameplayScreenProportion/2f + rect.getWidth()/2f, rect.getY() - Globals.SCREEN_HEIGHT * gameplayScreenProportion/2f + rect.getHeight()/2f);
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }
}
