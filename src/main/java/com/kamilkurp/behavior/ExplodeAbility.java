package com.kamilkurp.behavior;

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

public class ExplodeAbility extends Ability {
    Creature abilityCreature;
    protected AttackAnimation explosionAnimation;

    public ExplodeAbility(Creature abilityCreature) {
        super(abilityCreature);

        this.abilityCreature = abilityCreature;
        cooldownTime = 800;
        activeTime = 1800;
        channelTime = 2000;

        explosionAnimation = new AttackAnimation(Assets.explosionSpriteSheet, 20, 100);
        swordAttackRect = new Rectangle(-999, -999, 1, 1);
    }

    @Override
    protected void onAbilityStart() {
        activeTimer.reset();
        explosionAnimation.restart();

        abilityCreature.takeStaminaDamage(25f);


        abilityCreature.takeDamage(700f);
        Assets.explosionSound.play();

    }

    @Override
    protected void onUpdateActive(int i) {
        explosionAnimation.getAnimation().update(i);

        Collection<Creature> creatures = abilityCreature.getArea().getCreatures().values();
        for (Creature creature : creatures) {
            if (creature == this.abilityCreature) continue;
            if (Globals.distance(abilityCreature.getRect(), creature.getRect()) < 200f && activeTimer.getTime() < 350f) {
                if (!(this.abilityCreature instanceof Mob && creature instanceof Mob) && creature.isAlive()) { // mob can't hurt a mob?
                    creature.takeDamage(700f);

                }
            }
        }
    }

    @Override
    public void onChannel() {
        abilityCreature.setImmobilized(true);

    }

    @Override
    public void render(Graphics g, Camera camera) {
        if (state == AbilityState.ABILITY_ACTIVE) {
            int scale = 6;

            Image image = explosionAnimation.getAnimation().getCurrentFrame();

            image.draw(abilityCreature.getRect().getX() - camera.getPosX() - 150, abilityCreature.getRect().getY() - camera.getPosY() - 150, scale);
        }
    }
}
