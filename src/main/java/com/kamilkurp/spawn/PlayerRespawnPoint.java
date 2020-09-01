package com.kamilkurp.spawn;

import com.kamilkurp.Renderable;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.Character;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.utils.Camera;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class PlayerRespawnPoint implements Renderable {
    private int posX;
    private int posY;

    float width = 30f;
    float height = 30f;

    private Area area;

    public PlayerRespawnPoint(int posX, int posY, Area area) {
        this.posX = posX;
        this.posY = posY;
        this.area = area;

    }

    @Override
    public void render(Graphics g, Camera camera) {
        g.setColor(Color.orange);
        g.fillRect(posX - width / 2f - camera.getPosX(), posY - height / 2f - camera.getPosY(), width, height);

    }


    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public Area getArea() {
        return area;
    }
}
