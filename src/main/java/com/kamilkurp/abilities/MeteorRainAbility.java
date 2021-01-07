package com.kamilkurp.abilities;

import com.kamilkurp.Globals;
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
    protected float explosionRange;

    protected List<Meteor> meteors;

    private MeteorRainAbility(Creature abilityCreature) {
        super(abilityCreature);
    }

    @Override
    public void init() {
        cooldownTime = 35000;
        activeTime = 13000;
        channelTime = 300;

        explosionRange = 200f;
    }

    @Override
    protected void onActiveStart() {
        abilityCreature.takeStaminaDamage(25f);

    }

    @Override
    protected void onUpdateActive(int i) {

        for (Meteor meteor : meteors) {
            if (!meteor.isStarted() && activeTimer.getElapsed() > meteor.getStartTime()) {
                meteor.start();
            }

            if (meteor.isStarted()) {
                meteor.getExplosionAnimation().getAnimation().update(i);
                meteor.getExplosionWindupAnimation().getAnimation().update(i);


                if (meteor.getState() == AbilityState.ABILITY_CHANNELING) {
                    if (meteor.getChannelTimer().getElapsed() > meteor.getChannelTime()) {
                        meteor.setState(AbilityState.ABILITY_ACTIVE);
                        Assets.explosionSound.play(1.0f, 0.05f);
                        meteor.getExplosionAnimation().restart();

                        meteor.getActiveTimer().reset();

                    }
                }
                if (meteor.getState() == AbilityState.ABILITY_ACTIVE) {
                    if (meteor.getActiveTimer().getElapsed() > meteor.getActiveTime()) {
                        meteor.setState(AbilityState.ABILITY_INACTIVE);
                    }
                }
            }
        }



        Collection<Creature> creatures = abilityCreature.getArea().getCreatures().values();
        for (Creature creature : creatures) {
            if (creature == this.abilityCreature) continue;

            for (Meteor meteor : meteors) {
                if (meteor.getState() == AbilityState.ABILITY_ACTIVE && Globals.distance(meteor.getPos(), creature.getRect()) < meteor.getExplosionRange() + creature.getRect().getWidth() / 2f && meteor.getActiveTimer().getElapsed() < 200f) {
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
        abilityCreature.getEffect("immobilized").applyEffect(channelTime + activeTime);

        Rectangle rect = abilityCreature.getRect();

        meteors = new LinkedList<>();

        for (int i = 0; i < 30; i++) {
            meteors.add(new Meteor(400 * i, new Rectangle(rect.getCenterX() + Globals.randInt(-400, 400), rect.getCenterY() + Globals.randInt(-400, 400), 1, 1), explosionRange, 1.25f));
        }

    }

    public void render(Graphics g, Camera camera) {
        if (state == AbilityState.ABILITY_ACTIVE) {
            for (Meteor meteor : meteors) {
                if (meteor.getState() == AbilityState.ABILITY_CHANNELING) {
                    int spriteWidth = 64;
                    float scale = meteor.getExplosionRange() * 2 / spriteWidth;

                    Image image = meteor.getExplosionWindupAnimation().getAnimation().getCurrentFrame();

                    float posX = meteor.getPos().getX() - camera.getPosX() - meteor.getExplosionRange();
                    float posY = meteor.getPos().getY() - camera.getPosY() - meteor.getExplosionRange();

                    image.draw(posX, posY, scale);

                }
                if (meteor.getState() == AbilityState.ABILITY_ACTIVE) {
                    int spriteWidth = 64;
                    float scale = meteor.getExplosionRange() * 2 / spriteWidth;

                    Image image = meteor.getExplosionAnimation().getAnimation().getCurrentFrame();

                    float posX = meteor.getPos().getX() - camera.getPosX() - meteor.getExplosionRange();
                    float posY = meteor.getPos().getY() - camera.getPosY() - meteor.getExplosionRange();
                    image.draw(posX, posY, scale);
                }
            }
        }
    }

    public static MeteorRainAbility newInstance(Creature abilityCreature) {
        MeteorRainAbility ability = new MeteorRainAbility(abilityCreature);
        ability.init();
        return ability;
    }


}
