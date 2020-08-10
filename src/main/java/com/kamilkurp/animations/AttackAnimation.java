package com.kamilkurp.animations;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

public class AttackAnimation {
    private SpriteSheet spriteSheet;
    private Animation attackAnimation;
    private int frames;
    private int frameDuration;

    public AttackAnimation(SpriteSheet spriteSheet, int numberOfFrames, int frameDuration) {
        this.spriteSheet = spriteSheet;
        this.frames = numberOfFrames;
        this.frameDuration = frameDuration;

        loadAnimations();
    }

    private void loadAnimations() {


//        float[] rotationAngles = {270f, 180f, 90f, 0f};

        attackAnimation = new Animation();
        for (int j = 0; j < frames; j++) {
            Image image = spriteSheet.getSprite(j,0);
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
