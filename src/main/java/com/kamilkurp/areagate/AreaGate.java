package com.kamilkurp.areagate;

import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;


public class AreaGate {
    private final Area areaFrom;
    private final Area areaTo;
    private final Rectangle fromRect;
    private final Rectangle toRect;

    public AreaGate(Area areaFrom, int fromPosX, int fromPosY, Area areaTo, int toPosX, int toPosY) {
        this.areaFrom = areaFrom;
        this.areaTo = areaTo;

        fromRect = new Rectangle(fromPosX, fromPosY, 50, 50);
        toRect = new Rectangle(toPosX, toPosY, 50, 50);
    }


    public void render(Graphics g, Camera camera, Area currentArea) {
        g.setColor(Color.blue);
        if (currentArea == areaFrom) {
            g.fillRect(fromRect.getX() - camera.getPosX(), fromRect.getY() - camera.getPosY(), fromRect.getWidth(), fromRect.getHeight());
        }
        if (currentArea == areaTo) {
            g.fillRect(toRect.getX() - camera.getPosX(), toRect.getY() - camera.getPosY(), toRect.getWidth(), toRect.getHeight());
        }
    }

    public void update(GameSystem gameSystem) {

        for (Area area : gameSystem.getAreaMap().values()) {
            area.getAreaCreaturesHolder().updateGatesLogic(this, gameSystem.getCurrentAreaHolder());
        }

    }

    public Area getAreaFrom() {
        return areaFrom;
    }

    public Area getAreaTo() {
        return areaTo;
    }

    public Rectangle getFromRect() {
        return fromRect;
    }

    public Rectangle getToRect() {
        return toRect;
    }
}
