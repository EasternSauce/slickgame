package com.kamilkurp.areagate;

import com.kamilkurp.creatures.PlayerCharacter;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.terrain.CurrentAreaManager;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import java.util.Map;


public class AreaGate {
    private Area areaFrom;
    private Area areaTo;
    private Rectangle fromRect;
    private Rectangle toRect;

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

    public void update(Map<String, Area> areaMap, CurrentAreaManager currentAreaManager) throws SlickException {

;

        for (Area area : areaMap.values()) {
            area.getAreaCreaturesHolder().updateGatesLogic(this, currentAreaManager);
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
