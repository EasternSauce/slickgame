package com.kamilkurp.abilities;

import com.kamilkurp.Globals;
import com.kamilkurp.animations.AbilityAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.Mob;
import com.kamilkurp.items.Item;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.*;

import java.util.Collection;

public abstract class MeleeAttack extends Attack {
    protected AbilityAnimation attackAnimation;
    protected AbilityAnimation windupAnimation;
    protected final Sound weaponSound = Assets.attackSound;
    protected boolean aimed;

    protected float width;
    protected float height;
    protected float scale;
    protected float attackRange;

    protected float knockbackPower;

    protected MeleeAttack(Creature abilityCreature) {
        super(abilityCreature);
    }

    @Override
    public abstract void init();

    @Override
    protected void onActiveStart() {
        attackAnimation.restart();

        weaponSound.play(1.0f, 0.1f);

        abilityCreature.takeStaminaDamage(15f);

    }

    @Override
    protected void onUpdateActive(int i) {
        updateAttackRect(i);

        attackAnimation.getAnimation().update(i);

        Collection<Creature> creatures = abilityCreature.getArea().getCreatures().values();
        for (Creature creature : creatures) {
            if (creature == this.abilityCreature) continue;
            if (meleeAttackHitbox.intersects(creature.getRect()) || meleeAttackHitbox.contains(creature.getRect()) || creature.getRect().contains(meleeAttackHitbox)) {
                if (!(this.abilityCreature instanceof Mob && creature instanceof Mob)) { // mob can't hurt a mob?
                    if (!creature.isImmune()) {
                        Item weapon = this.abilityCreature.getEquipmentItems().get(0);
                        creature.takeDamage(weapon.getDamage(), true, knockbackPower, abilityCreature.getRect().getCenterX(), abilityCreature.getRect().getCenterY());
                        abilityCreature.onAttack();
                        int random = Globals.random.nextInt(100);
                        if (random < weapon.getItemType().getPoisonChance() * 100f) {
                            creature.becomePoisoned();
                        }

                        Assets.bloodSquirtSound.play(1.0f, 0.05f);
                    }
                }
            }
        }
    }

    @Override
    protected void onUpdateChanneling(int i) {
        windupAnimation.getAnimation().update(i);
        updateAttackRect(i);

        if (aimed) {
            abilityCreature.setAttackingVector(abilityCreature.getFacingVector());
        }
    }

    protected void updateAttackRect(int i) {
        Vector2f attackVector = abilityCreature.getAttackingVector().getNormal();

        float attackWidth = width * scale;
        float attackHeight = 32 * scale;


        float attackShiftX = attackVector.getX() * attackRange;
        float attackShiftY = attackVector.getY() * attackRange;


        float centerX = abilityCreature.getRect().getCenterX();
        float centerY = abilityCreature.getRect().getCenterY();

        float attackRectX = attackShiftX + centerX;
        float attackRectY = attackShiftY + centerY;

        meleeAttackRect = new Rectangle(attackRectX, attackRectY - height * scale, attackWidth, attackHeight);
        meleeAttackHitbox = new Polygon(meleeAttackRect.getPoints());

        meleeAttackHitbox = (Polygon) meleeAttackHitbox.transform(Transform.createRotateTransform((float) abilityCreature.getAttackingVector().getTheta() * 3.141593f/180f, meleeAttackRect.getX(), meleeAttackRect.getY() + height / 2 * scale));

        meleeAttackHitbox = (Polygon) meleeAttackHitbox.transform(Transform.createTranslateTransform(0, height/2 * scale));

    }

    @Override
    public void onChannellingStart() {
        abilityCreature.setAttackingVector(abilityCreature.getFacingVector());

        windupAnimation.restart();

        abilityCreature.setAttacking(true);
    }

    public void render(Graphics g, Camera camera) {



        if (state == AbilityState.ABILITY_CHANNELING) {
            Image image = windupAnimation.getAnimation().getCurrentFrame();
            image.setCenterOfRotation(0, height/2 * scale);
            image.setRotation((float) abilityCreature.getAttackingVector().getTheta());


            image.draw(meleeAttackRect.getX() - camera.getPosX(), meleeAttackRect.getY() - camera.getPosY() + height/2 * scale, scale);
        }
        if (state == AbilityState.ABILITY_ACTIVE) {
            if (Globals.SHOW_HITBOXES) {
                g.setColor(Color.pink);
                // draw attack rect
                Polygon polygonCopy = (Polygon)meleeAttackHitbox.transform(Transform.createTranslateTransform(-camera.getPosX(), -camera.getPosY()));
                g.fill(polygonCopy);
            }


            Image image = attackAnimation.getAnimation().getCurrentFrame();
            image.setCenterOfRotation(0, height/2 * scale);
            image.setRotation((float) abilityCreature.getAttackingVector().getTheta());


            image.draw(meleeAttackRect.getX() - camera.getPosX(), meleeAttackRect.getY() - camera.getPosY() + height/2 * scale, scale);
        }
    }

    public void setAimed(boolean aimed) {
        this.aimed = aimed;
    }

    public void setAttackRange(float attackRange) {
        this.attackRange = attackRange;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    protected void onStop() {
        abilityCreature.setAttacking(false);
    }
}
