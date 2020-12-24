package com.kamilkurp.abilities;

import com.kamilkurp.Globals;
import com.kamilkurp.animations.AttackAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.Mob;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class MeteorRainAbility extends Ability {
    protected float explosionRange;

    protected List<Meteor> meteors;

    public MeteorRainAbility(Creature abilityCreature) {
        super(abilityCreature);
    }

    @Override
    public void init() {
        cooldownTime = 35000;
        activeTime = 13000;
        channelTime = 300;

        explosionRange = 250f;
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
                meteor.getExplosionWindupAnimation().getAnimation().update(i);


                if (meteor.getState() == AbilityState.ABILITY_CHANNELING) {
                    if (meteor.getChannelTimer().getTime() > meteor.getChannelTime()) {
                        meteor.setState(AbilityState.ABILITY_ACTIVE);
                        Assets.explosionSound.play(1.0f, 0.1f);
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
                if (meteor.getState() == AbilityState.ABILITY_ACTIVE && Globals.distance(meteor.getPos(), creature.getRect()) < explosionRange + creature.getRect().getWidth() / 2f && meteor.getActiveTimer().getTime() < 200f) {
                    if (!(this.abilityCreature instanceof Mob && creature instanceof Mob) && creature.isAlive()) { // mob can't hurt a mob?
                        if (!creature.isImmune()) {
                            creature.takeDamage(50f, true, 0, 0, 0);
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

        for (int i = 0; i < 30; i++) {
            meteors.add(new Meteor(400 * i, new Rectangle(rect.getCenterX() + Globals.randInt(-400, 400), rect.getCenterY() + Globals.randInt(-400, 400), 1, 1)));
        }

    }

    public void render(Graphics g, Camera camera) {
        if (state == AbilityState.ABILITY_ACTIVE) {
            for (Meteor meteor : meteors) {
                if (meteor.getState() == AbilityState.ABILITY_CHANNELING) {
                    int spriteWidth = 64;
                    float scale = explosionRange * 2 / spriteWidth;

                    Image image = meteor.getExplosionWindupAnimation().getAnimation().getCurrentFrame();

                    float posX = meteor.getPos().getX() - camera.getPosX() - explosionRange;
                    float posY = meteor.getPos().getY() - camera.getPosY() - explosionRange;

                    image.draw(posX, posY, scale);

                }
                if (meteor.getState() == AbilityState.ABILITY_ACTIVE) {
                    int spriteWidth = 64;
                    float scale = explosionRange * 2 / spriteWidth;

                    Image image = meteor.getExplosionAnimation().getAnimation().getCurrentFrame();

                    float posX = meteor.getPos().getX() - camera.getPosX() - explosionRange;
                    float posY = meteor.getPos().getY() - camera.getPosY() - explosionRange;
                    image.draw(posX, posY, scale);
                }
            }
        }
    }

    @Override
    protected void onStop() {
        abilityCreature.setImmobilized(false);
    }

    public static MeteorRainAbility newInstance(Creature abilityCreature) {
        MeteorRainAbility ability = new MeteorRainAbility(abilityCreature);
        ability.init();
        ability.setTimerStartingPosition();
        return ability;
    }
}
