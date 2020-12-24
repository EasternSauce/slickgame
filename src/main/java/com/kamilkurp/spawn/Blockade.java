package com.kamilkurp.spawn;

import com.kamilkurp.utils.Camera;
import com.kamilkurp.utils.Rect;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class Blockade {
    private MobSpawnPoint mobSpawnPoint;
    private int blockadePosX;
    private int blockadePosY;
    private boolean active;

    private Rect rect;

    public Blockade(MobSpawnPoint mobSpawnPoint, int blockadePosX, int blockadePosY) {
        this.mobSpawnPoint = mobSpawnPoint;
        active = false;
        rect = new Rect(blockadePosX, blockadePosY, 64 ,64);
        this.blockadePosX = blockadePosX;
        this.blockadePosY = blockadePosY;
    }

    public void render(Graphics g, Camera camera) {
        if (active) {
            g.setColor(Color.black);
            g.fillRect(rect.getX() - camera.getPosX(), rect.getY() - camera.getPosY(), rect.getWidth(), rect.getHeight());
        }
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public Rect getRect() {
        return rect;
    }
}
