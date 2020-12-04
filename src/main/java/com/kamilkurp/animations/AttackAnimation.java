package com.kamilkurp.animations;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

public class AttackAnimation {
    private final SpriteSheet spriteSheet;
    private Animation attackAnimation;
    private final int frames;
    private final int frameDuration;
    private boolean rotatedSheet;

    public AttackAnimation(SpriteSheet spriteSheet, int numberOfFrames, int frameDuration) {
        this.spriteSheet = spriteSheet;
        this.frames = numberOfFrames;
        this.frameDuration = frameDuration;
        rotatedSheet = false;

        loadAnimations();
    }

    public AttackAnimation(SpriteSheet spriteSheet, int numberOfFrames, int frameDuration, boolean rotatedSheet) {
        this.spriteSheet = spriteSheet;
        this.frames = numberOfFrames;
        this.frameDuration = frameDuration;
        this.rotatedSheet = rotatedSheet;

        loadAnimations();
    }

    private void loadAnimations() {


//        float[] rotationAngles = {270f, 180f, 90f, 0f};

        attackAnimation = new Animation();
        for (int j = 0; j < frames; j++) {
            Image image;
            if (rotatedSheet) {
                image = spriteSheet.getSprite(0, j);
            }
            else {
                image = spriteSheet.getSprite(j,0);
            }
            //image.
            //image.rotate(rotationAngles[i]);
            attackAnimation.addFrame(image, frameDuration);
        }


    }

    public Animation getAnimation() {
        return attackAnimation;
    }

    public void restart() {
        attackAnimation.restart();
    }
}
