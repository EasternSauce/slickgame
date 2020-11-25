package com.kamilkurp.behavior;

import com.kamilkurp.Globals;
import com.kamilkurp.animations.AttackAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.Mob;
import com.kamilkurp.utils.Action;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;

import java.util.Collection;

public class ExplodeAbility extends Ability {
    Creature abilityCreature;
    protected AttackAnimation explosionAnimation;

    public ExplodeAbility(Creature abilityCreature) {
        super();

        this.abilityCreature = abilityCreature;
        cooldown = 800;
        abilityTime = 1800;
        windupTime = 2000;

        explosionAnimation = new AttackAnimation(Assets.explosionSpriteSheet, 20, 100);
        swordAttackRect = new Rectangle(-999, -999, 1, 1);
    }

    @Override
    public void update(int i) {
        if (windup && windupTimer.getTime() > windupTime) {
            windup = false;
            if (abilityCreature.getStaminaPoints() != 0) {
                perform();
                onPerformAction.execute();
            }
        }

        if (cooldownTimer.getTime() > abilityTime) {
            active = false;
        }


        if (active) {
            explosionAnimation.getAnimation().update(i);




            Collection<Creature> creatures = abilityCreature.getArea().getCreatures().values();
            for (Creature creature : creatures) {
                if (creature == this.abilityCreature) continue;
                if (Globals.distance(abilityCreature.getRect(), creature.getRect()) < 200f && cooldownTimer.getTime() < 350f) {
                    if (!(this.abilityCreature instanceof Mob && creature instanceof Mob) && creature.isAlive()) { // mob can't hurt a mob?
                        creature.takeDamage(700f);

                    }
                }
            }

        }
    }

    @Override
    protected void perform() {
        active = true;
        cooldownTimer.reset();
        explosionAnimation.restart();

        abilityCreature.takeStaminaDamage(25f);


        abilityCreature.takeDamage(700f);
        Assets.explosionSound.play();

    }

    @Override
    public void onWindup() {
        abilityCreature.setImmobilized(true);

    }

    @Override
    public void render(Graphics g, Camera camera) {
        if (active) {
            int scale = 6;

            Image image = explosionAnimation.getAnimation().getCurrentFrame();

            image.draw(abilityCreature.getRect().getX() - camera.getPosX() - 150, abilityCreature.getRect().getY() - camera.getPosY() - 150, scale);
        }
        if (windup) {

        }
    }
}
