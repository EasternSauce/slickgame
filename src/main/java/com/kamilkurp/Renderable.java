package com.kamilkurp;

import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Graphics;

public interface Renderable {
    void render(Graphics g, Camera camera);
}
