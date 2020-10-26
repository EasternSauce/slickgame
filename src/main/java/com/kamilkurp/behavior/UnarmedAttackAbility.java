package com.kamilkurp.behavior;

import com.kamilkurp.animations.AttackAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.Mob;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;

import java.util.Collection;

public class UnarmedAttackAbility extends Ability {
    Creature creature;
    protected AttackAnimation swordAttackAnimation;
    private final Sound punchSound = Assets.punchSound;

    public UnarmedAttackAbility(Creature creature) {
        super();

        this.creature = creature;
        cooldown = 800;
        abilityTime = 300;

        swordAttackAnimation = new AttackAnimation(Assets.betterSlashSpriteSheet, 6, 50);
        swordAttackRect = new Rectangle(-999, -999, 1, 1);

    }

    @Override
    public void update(int i) {
        if (cooldownTimer.getTime() > abilityTime) {
            active = false;
        }

        if (active) {


            float attackRange = 60f;

            float attackShiftX = creature.getAttackingVector().getNormal().getX() * attackRange;
            float attackShiftY = creature.getAttackingVector().getNormal().getY() * attackRange;

            int attackWidth = 40;
            int attackHeight = 40;

            float attackRectX = attackShiftX + creature.getRect().getCenterX() - attackWidth / 2f;
            float attackRectY = attackShiftY + creature.getRect().getCenterY() - attackHeight / 2f;

            swordAttackRect = new Rectangle(attackRectX, attackRectY, attackWidth, attackHeight);


            swordAttackAnimation.getAnimation().update(i);


            Collection<Creature> creatures = creature.getArea().getCreatures().values();
            for (Creature creature : creatures) {
                if (creature == this.creature) continue;
                if (swordAttackRect.intersects(creature.getRect())) {
                    if (!(this.creature instanceof Mob && creature instanceof Mob)) { // mob can't hurt a mob?
                        creature.takeDamage(this.creature.getUnarmedDamage());
                    }
                }
            }
        }
    }

    @Override
    public void performMovement() {

    }

    @Override
    public void performOnUpdateStart(int i) {

    }

    @Override
    protected void perform() {
        active = true;
        abilityTimer.reset();
        cooldownTimer.reset();
        swordAttackAnimation.restart();

        punchSound.play(1.0f, 0.1f);

        creature.setAttackingVector(creature.getFacingVector());
    }

    @Override
    public void render(Graphics g, Camera camera) {
        if (active) {

//            if (currentAttackType == AttackType.SWORD) {
            Image image = swordAttackAnimation.getAnimation().getCurrentFrame();
            image.setRotation((float) creature.getAttackingVector().getTheta());

            g.drawImage(image, swordAttackRect.getX() - camera.getPosX(), swordAttackRect.getY() - camera.getPosY());
        }
    }
}
