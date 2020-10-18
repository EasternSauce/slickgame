package com.kamilkurp.items;

import com.kamilkurp.Renderable;
import com.kamilkurp.terrain.Area;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

import java.util.LinkedList;
import java.util.List;

public class LootPile implements Renderable {
    private Rectangle rect;

    private int width = 10;
    private int height = 10;

    private boolean visible = true;

    private Area area = null;

    List<Item> itemList;

    public LootPile(Area area, float x, float y) {
        rect = new Rectangle(x, y, width, height);
        itemList = new LinkedList<>();
        this.area = area;
    }

    @Override
    public void render(Graphics g, Camera camera) {
        if (visible) {
            g.setColor(Color.green);
            g.fillRect(rect.getX() - camera.getPosX(), rect.getY() - camera.getPosY(), rect.getWidth(), rect.getHeight());
        }
    }

    public Rectangle getRect() {
        return rect;
    }

    public void addItem(Item item) {
        itemList.add(item);
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Area getArea() {
        return area;
    }
}
