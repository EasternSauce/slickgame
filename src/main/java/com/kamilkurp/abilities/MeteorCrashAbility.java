package com.kamilkurp.abilities;

import com.kamilkurp.Globals;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.Mob;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class MeteorCrashAbility extends Ability {
    protected List<Meteor> meteors;

    private MeteorCrashAbility(Creature abilityCreature) {
        super(abilityCreature);
    }

    @Override
    public void init() {
        cooldownTime = 6500;
        activeTime = 2000;
        channelTime = 850;
    }

    @Override
    protected void onActiveStart() {
        abilityCreature.takeStaminaDamage(25f);

        Rectangle rect = abilityCreature.getRect();

        meteors = new LinkedList<>();

        Vector2f facingVector = abilityCreature.getFacingVector().normalise();

        for (int i = 0; i < 5; i++) {
            meteors.add(new Meteor(100 * i, new Rectangle(rect.getCenterX() + (100 * (i + 1)) * facingVector.getX(), rect.getCenterY() + (100 * (i + 1)) * facingVector.getY(), 1, 1), 50 + 3 * i * i, 2.5f));
        }
        for (int i = 0; i < 5; i++) {
            Vector2f vector = facingVector.copy();
            vector.setTheta(vector.getTheta() + 50);
            meteors.add(new Meteor(100 * i, new Rectangle(rect.getCenterX() + (100 * (i + 1)) * vector.getX(), rect.getCenterY() + (100 * (i + 1)) * vector.getY(), 1, 1), 50 + 3 * i * i, 2.5f));
        }
        for (int i = 0; i < 5; i++) {
            Vector2f vector = facingVector.copy();
            vector.setTheta(vector.getTheta() - 50);
            meteors.add(new Meteor(100 * i, new Rectangle(rect.getCenterX() + (100 * (i + 1)) * vector.getX(), rect.getCenterY() + (100 * (i + 1)) * vector.getY(), 1, 1), 50 + 3 * i * i, 2.5f));
        }

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
                        Assets.explosionSound.play(1.0f, 0.03f);
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
                if (meteor.getState() == AbilityState.ABILITY_ACTIVE && Globals.distance(meteor.getPos(), creature.getRect()) < meteor.getExplosionRange() + creature.getRect().getWidth() / 2f && meteor.getActiveTimer().getTime() < 200f) {
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

    @Override
    protected void onStop() {
        abilityCreature.setImmobilized(false);
    }

    public static MeteorCrashAbility newInstance(Creature abilityCreature) {
        MeteorCrashAbility ability = new MeteorCrashAbility(abilityCreature);
        ability.init();
        ability.setTimerStartingPosition();
        return ability;
    }
}
