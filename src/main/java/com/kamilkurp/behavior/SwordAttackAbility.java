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

public class SwordAttackAbility extends Ability {
    Creature abilityCreature;
    protected AttackAnimation swordAttackAnimation;
    protected AttackAnimation swordWindupAnimation;
    private final Sound swordAttackSound = Assets.attackSound;

    public SwordAttackAbility(Creature abilityCreature) {
        super(abilityCreature);

        this.abilityCreature = abilityCreature;
        cooldownTime = 800;
        activeTime = 300;
        channelTime = 500;

        swordAttackAnimation = new AttackAnimation(Assets.betterSlashSpriteSheet, 6, 50);
        swordWindupAnimation = new AttackAnimation(Assets.slashWindupSpriteSheet, 6, 90);
        swordAttackRect = new Rectangle(-999, -999, 1, 1);
    }

    @Override
    protected void onAbilityStart() {
        activeTimer.reset();
        swordAttackAnimation.restart();

        swordAttackSound.play(1.0f, 0.1f);

        abilityCreature.takeStaminaDamage(25f);

    }

    @Override
    protected void onUpdateActive(int i) {
        updateAttackRect(i);

        swordAttackAnimation.getAnimation().update(i);

        Collection<Creature> creatures = abilityCreature.getArea().getCreatures().values();
        for (Creature creature : creatures) {
            if (creature == this.abilityCreature) continue;
            if (swordAttackRect.intersects(creature.getRect())) {
                if (!(this.abilityCreature instanceof Mob && creature instanceof Mob)) { // mob can't hurt a mob?
                    creature.takeDamage(this.abilityCreature.getEquipmentItems().get(0).getDamage());
                }
            }
        }
    }

    @Override
    protected void onUpdateChanneling(int i) {
        swordWindupAnimation.getAnimation().update(i);
        updateAttackRect(i);
    }

    private void updateAttackRect(int i) {
        float attackRange = 60f;

        float attackShiftX = abilityCreature.getAttackingVector().getNormal().getX() * attackRange;
        float attackShiftY = abilityCreature.getAttackingVector().getNormal().getY() * attackRange;

        int attackWidth = 40;
        int attackHeight = 40;

        float attackRectX = attackShiftX + abilityCreature.getRect().getCenterX() - attackWidth / 2f;
        float attackRectY = attackShiftY + abilityCreature.getRect().getCenterY() - attackHeight / 2f;

        swordAttackRect = new Rectangle(attackRectX, attackRectY, attackWidth, attackHeight);
    }

    @Override
    public void onChannel() {
        abilityCreature.setAttackingVector(abilityCreature.getFacingVector());

        swordWindupAnimation.restart();
    }

    @Override
    public void render(Graphics g, Camera camera) {
        if (state == AbilityState.ABILITY_ACTIVE) {
            Image image = swordAttackAnimation.getAnimation().getCurrentFrame();
            image.setRotation((float) abilityCreature.getAttackingVector().getTheta());

            g.drawImage(image, swordAttackRect.getX() - camera.getPosX(), swordAttackRect.getY() - camera.getPosY());
        }
        if (state == AbilityState.ABILITY_CHANNELING) {
            Image image = swordWindupAnimation.getAnimation().getCurrentFrame();
            image.setRotation((float) abilityCreature.getAttackingVector().getTheta());

            g.drawImage(image, swordAttackRect.getX() - camera.getPosX(), swordAttackRect.getY() - camera.getPosY());
        }
    }
}
