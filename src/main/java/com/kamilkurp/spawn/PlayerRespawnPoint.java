package com.kamilkurp.spawn;

import com.kamilkurp.Renderable;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.Character;
import com.kamilkurp.utils.Camera;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class PlayerRespawnPoint implements Renderable {
    private int posX;
    private int posY;

    float width = 30f;
    float height = 30f;

    private Character character;
    private Timer timer;
    private boolean respawning;

    public PlayerRespawnPoint(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;

        timer = new Timer();
        respawning = false;

    }

    public void update() {
        if (respawning && timer.getTime() > 3000f) {
            respawning = false;
            character.setPosition(posX, posY);
            character.setMaxHealthPoints(character.getMaxHealthPoints());
        }

    }

    @Override
    public void render(Graphics g, Camera camera) {
        g.setColor(Color.orange);
        g.fillRect(posX - width / 2f - camera.getPosX(), posY - height / 2f - camera.getPosY(), width, height);
        if (respawning) {
            Assets.verdanaHugeTtf.drawString(175, 175, "YOU DIED", Color.red);
        }
    }

    public void startRespawnProcess(Character character) {
        timer.reset();
        respawning = true;
        this.character = character;
    }
}
