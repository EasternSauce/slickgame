package com.kamilkurp.spawn;

import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.utils.Camera;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public class PlayerRespawnPoint {
    float width = 30f;
    float height = 30f;

    private final Area area;

    private Rectangle rect;

    private GameSystem gameSystem;

    private Timer respawnSetTimer;

    private int respawnSetTime = 2000;

    public PlayerRespawnPoint(GameSystem gameSystem, int posX, int posY, Area area) {
        this.gameSystem = gameSystem;
        this.area = area;

        this.rect = new Rectangle(posX, posY, width, height);

        respawnSetTimer = new Timer();
        respawnSetTimer.setTime(respawnSetTime);

    }

    public void render(Graphics g, Camera camera) {
        if (respawnSetTimer.getTime() < respawnSetTime) {
            g.setColor(Color.red);
        }
        else {
            g.setColor(Color.orange);
        }

        g.fillRect(rect.getX() - width / 2f - camera.getPosX(), rect.getY() - height / 2f - camera.getPosY(), width, height);

    }


    public int getPosX() {
        return (int)rect.getX();
    }

    public int getPosY() {
        return (int)rect.getY();
    }

    public Area getArea() {
        return area;
    }

    public Rectangle getRect() {
        return rect;
    }

    public void onRespawnSet() {
        respawnSetTimer.reset();
    }
}
