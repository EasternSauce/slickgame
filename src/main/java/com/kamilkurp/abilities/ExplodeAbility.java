package com.kamilkurp.abilities;

import com.kamilkurp.Globals;
import com.kamilkurp.animations.AbilityAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.Mob;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import java.util.Collection;

public class ExplodeAbility extends Ability {
    protected AbilityAnimation explosionAnimation;
    protected float explosionRange;

    private ExplodeAbility(Creature abilityCreature) {
        super(abilityCreature);
    }

    @Override
    public void init() {
        cooldownTime = 800;
        activeTime = 1800;
        channelTime = 1300;

        explosionAnimation = new AbilityAnimation(Assets.explosionSpriteSheet, 20, 100);

        explosionRange = 300f;
    }

    @Override
    protected void onActiveStart() {
        explosionAnimation.restart();

        abilityCreature.takeStaminaDamage(25f);


        abilityCreature.takeDamage(700f, false, 0, 0, 0);

        Assets.explosionSound.play(1.0f, 0.07f);

    }

    @Override
    protected void onUpdateActive(int i) {
        explosionAnimation.getAnimation().update(i);

        Collection<Creature> creatures = abilityCreature.getArea().getCreatures().values();
        for (Creature creature : creatures) {
            if (creature == this.abilityCreature) continue;
            if (Globals.distance(abilityCreature.getRect(), creature.getRect()) < explosionRange && activeTimer.getElapsed() < 350f) {
                if (!(this.abilityCreature instanceof Mob && creature instanceof Mob) && creature.isAlive()) { // mob can't hurt a mob?
                    if (!creature.isImmune()) {
                        creature.takeDamage(700f, true, 0, 0, 0);
                    }

                }
            }
        }
    }

    @Override
    public void onChannellingStart() {
        abilityCreature.getEffect("immobilized").applyEffect(channelTime + activeTime);

    }

    public void render(Graphics g, Camera camera) {

        if (state == AbilityState.ABILITY_ACTIVE) {
            int spriteWidth = 64;
            float scale = explosionRange * 2 / spriteWidth;

            Image image = explosionAnimation.getAnimation().getCurrentFrame();

            image.draw(abilityCreature.getRect().getCenterX() - camera.getPosX() - explosionRange, abilityCreature.getRect().getCenterY() - camera.getPosY() - explosionRange, scale);
        }
    }

    public static ExplodeAbility newInstance(Creature abilityCreature) {
        ExplodeAbility ability = new ExplodeAbility(abilityCreature);
        ability.init();
        return ability;
    }
}
