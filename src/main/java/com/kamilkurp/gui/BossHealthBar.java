package com.kamilkurp.gui;

import com.kamilkurp.Globals;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public class BossHealthBar {
    private Creature boss;
    private boolean visible;

    private Rectangle maxHealthRect;
    private Rectangle healthRect;

    int w = Globals.SCREEN_WIDTH;
    int h = Globals.SCREEN_HEIGHT;
    float proportion = Globals.SCREEN_PROPORTION;

    public BossHealthBar() {
        boss = null;
        visible = false;
    }

    public void render(Graphics g) {
        if (visible && boss != null) {
            g.drawString(boss.getName(), w / 2f - 80, 10);

            g.setColor(Color.orange);
            g.fill(maxHealthRect);

            g.setColor(Color.red);
            g.fill(healthRect);
        }
    }

    public Creature getBoss() {
        return boss;
    }

    public void onBossBattleStart(Creature boss) {
        this.boss = boss;

        visible = true;
    }

    public void hide() {
        visible = false;
    }

    public void update() {
        if (visible && boss != null) {
            maxHealthRect = new Rectangle(w / 2f - 250, 40, 500, 20);
            healthRect = new Rectangle(w / 2f - 250, 40, 500 * boss.getHealthPoints()/boss.getMaxHealthPoints(), 20);
        }
    }
}
