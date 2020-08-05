package com.kamilkurp.animations;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

public class AttackAnimation {
    private SpriteSheet spriteSheet;
    private Animation[] attackAnimation;
    private int framesPerDirection;
    private int frameDuration;

    public AttackAnimation(SpriteSheet spriteSheet, int framesPerDirection, int frameDuration) {
        this.spriteSheet = spriteSheet;
        this.framesPerDirection = framesPerDirection;
        this.frameDuration = frameDuration;

        loadAnimations();
    }

    private void loadAnimations() {

        attackAnimation = new Animation[4];

        float[] rotationAngles = {270f, 180f, 90f, 0f};

        for (int i = 0; i < 4; i++) {
            attackAnimation[i] = new Animation();
            for (int j = 0; j < framesPerDirection; j++) {
                Image image = spriteSheet.getSprite(j,0);
                image.rotate(rotationAngles[i]);
                attackAnimation[i].addFrame(image, frameDuration);
            }
        }

    }

    public Animation getAnimation(int direction) {
        return attackAnimation[direction];
    }

    public void restart() {
        attackAnimation[0].restart();
        attackAnimation[1].restart();
        attackAnimation[2].restart();
        attackAnimation[3].restart();
    }
}
