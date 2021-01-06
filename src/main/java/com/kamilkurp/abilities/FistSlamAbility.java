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

public class FistSlamAbility extends Ability {
    protected List<Fist> fists;

    private FistSlamAbility(Creature abilityCreature) {
        super(abilityCreature);
    }

    @Override
    public void init() {
        cooldownTime = 6500;
        activeTime = 2200;
        channelTime = 350;
    }

    @Override
    protected void onActiveStart() {
        abilityCreature.takeStaminaDamage(25f);

    }

    @Override
    protected void onUpdateActive(int i) {

        for (Fist fist : fists) {
            if (!fist.isStarted() && activeTimer.getElapsed() > fist.getStartTime()) {
                fist.start();
            }

            if (fist.isStarted()) {
                fist.getAbilityAnimation().getAnimation().update(i);
                fist.getWindupAnimation().getAnimation().update(i);


                if (fist.getState() == AbilityState.ABILITY_CHANNELING) {
                    if (fist.getChannelTimer().getElapsed() > fist.getChannelTime()) {
                        fist.setState(AbilityState.ABILITY_ACTIVE);
                        Assets.glassBreakSound.play(1.0f, 0.1f);
                        fist.getAbilityAnimation().restart();

                        fist.getActiveTimer().reset();

                    }
                }
                if (fist.getState() == AbilityState.ABILITY_ACTIVE) {
                    if (fist.getActiveTimer().getElapsed() > fist.getActiveTime()) {
                        fist.setState(AbilityState.ABILITY_INACTIVE);
                    }
                }
            }
        }



        Collection<Creature> creatures = abilityCreature.getArea().getCreatures().values();
        for (Creature creature : creatures) {
            if (creature == this.abilityCreature) continue;

            for (Fist fist : fists) {
                if (fist.getState() == AbilityState.ABILITY_ACTIVE &&
                        fist.getHitbox().intersects(creature.getRect())
                        && fist.getActiveTimer().getElapsed() < 150f) {
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

        fists = new LinkedList<>();

        for (int i = 0; i < 10; i++) {
            int shiftX = 40;
            int shiftY = 100;
            int range = 155;
            fists.add(new Fist(200 * i, new Rectangle(rect.getCenterX() - shiftX + Globals.randInt(-range, range), rect.getCenterY() - shiftY + Globals.randInt(-range, range), 1, 1)));
        }

    }

    public void render(Graphics g, Camera camera) {
        if (state == AbilityState.ABILITY_ACTIVE) {
            for (Fist fist : fists) {
//                g.setColor(Color.green);
//                g.drawRect(fist.getHitbox().getX() - camera.getPosX(), fist.getHitbox().getY() - camera.getPosY(), 40 * fist.getScale(), 40 * fist.getScale());

                if (fist.getState() == AbilityState.ABILITY_CHANNELING) {
                    Image image = fist.getWindupAnimation().getAnimation().getCurrentFrame();

                    float posX = fist.getPos().getX() - camera.getPosX();
                    float posY = fist.getPos().getY() - camera.getPosY();

                    image.draw(posX, posY, fist.getScale());

                }
                if (fist.getState() == AbilityState.ABILITY_ACTIVE) {
                    Image image = fist.getAbilityAnimation().getAnimation().getCurrentFrame();

                    float posX = fist.getPos().getX() - camera.getPosX();
                    float posY = fist.getPos().getY() - camera.getPosY();
                    image.draw(posX, posY, fist.getScale());
                }
            }
        }
    }

    @Override
    protected void onStop() {
        abilityCreature.setImmobilized(false);
    }

    public static FistSlamAbility newInstance(Creature abilityCreature) {
        FistSlamAbility ability = new FistSlamAbility(abilityCreature);
        ability.init();
        return ability;
    }
}
