package com.kamilkurp.abilities;

import com.kamilkurp.Globals;
import com.kamilkurp.animations.AttackAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.Mob;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class MeteorRainAbility extends Ability {
    Creature abilityCreature;
    protected float explosionRange;

    protected List<Meteor> meteors;

    public MeteorRainAbility(Creature abilityCreature) {
        super(abilityCreature);

        this.abilityCreature = abilityCreature;
        cooldownTime = 5000;
        activeTime = 3000;
        channelTime = 300;

        explosionRange = 200f;


        setTimerStartingPosition();

    }

    @Override
    protected void onActiveStart() {
        activeTimer.reset();
        abilityCreature.takeStaminaDamage(25f);

    }

    @Override
    protected void onUpdateActive(int i) {

        for (Meteor meteor : meteors) {
            if (!meteor.isStarted() && activeTimer.getTime() > meteor.getStartTime()) {
                meteor.start();
            }

            if (meteor.isStarted()) {
                meteor.getExplosionAnimation().getAnimation().update(i);

                if (meteor.getState() == AbilityState.ABILITY_CHANNELING) {
                    if (meteor.getChannelTimer().getTime() > meteor.getChannelTime()) {
                        meteor.setState(AbilityState.ABILITY_ACTIVE);
                        Assets.explosionSound.play(1.0f, 0.4f);
                        meteor.getExplosionAnimation().restart();

                        meteor.getActiveTimer().reset();

                    }
                }
                if (meteor.getState() == AbilityState.ABILITY_ACTIVE) {
                    if (meteor.getActiveTimer().getTime() > meteor.getActiveTime()) {
                        meteor.setState(AbilityState.ABILITY_INACTIVE);
                    }
                }
            }
        }



        Collection<Creature> creatures = abilityCreature.getArea().getCreatures().values();
        for (Creature creature : creatures) {
            if (creature == this.abilityCreature) continue;

            for (Meteor meteor : meteors) {
                if (meteor.getState() == AbilityState.ABILITY_ACTIVE && Globals.distance(meteor.getPos(), creature.getRect()) < explosionRange) {
                    if (!(this.abilityCreature instanceof Mob && creature instanceof Mob) && creature.isAlive()) { // mob can't hurt a mob?
                        if (!creature.isImmune()) {
                            creature.takeDamage(50f, true);
                        }

                    }
                }
            }


        }
    }

    @Override
    public void onChannellingStart() {
        abilityCreature.setImmobilized(true);

        Rectangle rect = abilityCreature.getRect();

        meteors = new LinkedList<>();
        meteors.add(new Meteor(400, new Rectangle(rect.getCenterX() + 100, rect.getCenterY() - 150, 1, 1)));
        meteors.add(new Meteor(800, new Rectangle(rect.getCenterX() - 50, rect.getCenterY() + 180, 1, 1)));
        meteors.add(new Meteor(1200, new Rectangle(rect.getCenterX() + 150, rect.getCenterY() + 200, 1, 1)));


    }

    public void render(Graphics g, Camera camera) {
        if (state == AbilityState.ABILITY_ACTIVE) {
            for (Meteor meteor : meteors) {
                if (meteor.getState() == AbilityState.ABILITY_ACTIVE) {
                    int spriteWidth = 64;
                    float scale = explosionRange * 2 / spriteWidth;

                    Image image = meteor.getExplosionAnimation().getAnimation().getCurrentFrame();

                    image.draw(meteor.getPos().getX() - camera.getPosX() - explosionRange, meteor.getPos().getY() - camera.getPosY() - explosionRange, scale);
                }
            }
        }
    }

    @Override
    protected void onStop() {
        abilityCreature.setImmobilized(false);
    }
}
