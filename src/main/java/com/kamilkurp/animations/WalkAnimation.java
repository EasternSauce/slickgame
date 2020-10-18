package com.kamilkurp.animations;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

public class WalkAnimation {
    private final SpriteSheet spriteSheet;
    private Animation[] walkAnimation;
    private final int framesPerDirection;
    private final int frameDuration;
    private final int[] directions;
    private final int restPosition;

    public WalkAnimation(SpriteSheet spriteSheet, int framesPerDirection, int frameDuration, int[] directions, int restPosition) {
        this.spriteSheet = spriteSheet;
        this.framesPerDirection = framesPerDirection;
        this.frameDuration = frameDuration;
        this.directions = directions;
        this.restPosition = restPosition;

        loadAnimations();
    }

    private void loadAnimations() {
        walkAnimation = new Animation[4];

        for (int i = 0; i < 4; i++) { // four directions
            walkAnimation[i] = new Animation();
            for (int j = 0; j < framesPerDirection; j++) {
                walkAnimation[i].addFrame(spriteSheet.getSprite(j,directions[i]), frameDuration);

            }
        }


    }

    public Image getRestPosition(int direction) {
        return spriteSheet.getSprite(restPosition, directions[direction]);
    }

    public Animation getAnimation(int direction) {
        return walkAnimation[direction];
    }
}
