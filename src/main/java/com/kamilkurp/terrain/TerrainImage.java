package com.kamilkurp.terrain;

import org.newdawn.slick.Image;

public class TerrainImage {
    private Image image;

    private boolean passable = true;

    private int x;
    private int y;


    public TerrainImage(Image image, int x, int y, boolean passable) {
        this.image = image;
        this.passable = passable;
        this.x = x;
        this.y = y;
    }

    public boolean isPassable() {
        return passable;
    }

    public Image getImage() {
        return image;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
