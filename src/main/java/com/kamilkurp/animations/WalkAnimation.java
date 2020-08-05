package com.kamilkurp.animations;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

public class WalkAnimation {
    private SpriteSheet spriteSheet;
    private Animation[] walkAnimation;
    private int framesPerDirection;
    private int frameDuration;

    public WalkAnimation(SpriteSheet spriteSheet, int framesPerDirection, int frameDuration) {
        this.spriteSheet = spriteSheet;
        this.framesPerDirection = framesPerDirection;
        this.frameDuration = frameDuration;

        loadAnimations();
    }

    private void loadAnimations() {
        walkAnimation = new Animation[4];

        for (int i = 0; i < 4; i++) { // four directions
            walkAnimation[i] = new Animation();
            for (int j = 0; j < framesPerDirection; j++) {
                walkAnimation[i].addFrame(spriteSheet.getSprite(j,i), frameDuration);

            }
        }


    }

    public Image getRestPosition(int direction) {
        return spriteSheet.getSprite(0, direction);
    }

    public Animation getAnimation(int direction) {
        return walkAnimation[direction];
    }
}
