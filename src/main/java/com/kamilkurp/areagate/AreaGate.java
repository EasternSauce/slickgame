package com.kamilkurp.areagate;

import com.kamilkurp.creatures.Character;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.terrain.CurrentAreaManager;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import java.util.LinkedList;
import java.util.List;


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

    public void update(CurrentAreaManager currentAreaManager) throws SlickException {
        Area currentArea = currentAreaManager.getCurrentArea();

        for (Creature creature : currentArea.getCreaturesMap().values()) {
            if (creature instanceof Character) {
                if (!creature.isPassedGateRecently()) {
                    Rectangle gateRect = null;
                    Area destinationArea = null;
                    Rectangle destinationRect = null;
                    if (currentArea == areaFrom) {
                        gateRect = fromRect;
                        destinationArea = areaTo;
                        destinationRect = toRect;
                    }
                    if (currentArea == areaTo) {
                        gateRect = toRect;
                        destinationArea = areaFrom;
                        destinationRect = fromRect;
                    }

                    if (creature.getRect().intersects(gateRect) && creature.getPassedGateTimer().getTime() > 1000f) {
                        creature.getPassedGateTimer().reset();
                        creature.getRect().setX(destinationRect.getX());
                        creature.getRect().setY(destinationRect.getY());
                        creature.setPassedGateRecently(true);
                        System.out.println("setting area to move to for " + creature.getId() + " to " + destinationArea.getId());
                        creature.setAreaToMoveTo(destinationArea);
                        destinationArea.reset();
                    }
                }
            }
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
