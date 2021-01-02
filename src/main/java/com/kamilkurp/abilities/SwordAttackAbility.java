package com.kamilkurp.abilities;

import com.kamilkurp.Globals;
import com.kamilkurp.animations.AttackAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.AttackType;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.Mob;
import com.kamilkurp.items.Item;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;

import java.util.Collection;

public class SwordAttackAbility extends Ability {
    protected AttackAnimation swordAttackAnimation;
    protected AttackAnimation swordWindupAnimation;
    private final Sound swordAttackSound = Assets.attackSound;
    private boolean aimed;

    private SwordAttackAbility(Creature abilityCreature) {
        super(abilityCreature);

        attackType = AttackType.SWORD;
    }

    @Override
    public void init() {
        float weaponSpeed = 1.0f;
        if (this.abilityCreature.getEquipmentItems().get(0) != null) {
            weaponSpeed = this.abilityCreature.getEquipmentItems().get(0).getItemType().getWeaponSpeed();
        }

        float baseChannelTime = 300f;
        float baseActiveTime = 300f;
        int numOfChannelFrames = 6;
        int numOfFrames = 6;
        int channelFrameDuration = (int)(baseChannelTime/numOfChannelFrames);
        int frameDuration = (int)(baseActiveTime/numOfFrames);

        channelTime = (int)(baseChannelTime * 1f/weaponSpeed);
        activeTime = (int)(baseActiveTime * 1f/weaponSpeed);

        cooldownTime = 800;

        swordWindupAnimation = new AttackAnimation(Assets.slashWindupSpriteSheet, numOfChannelFrames, channelFrameDuration);
        swordAttackAnimation = new AttackAnimation(Assets.betterSlashSpriteSheet, numOfFrames, frameDuration);

        meleeAttackRect = new Rectangle(-999, -999, 1, 1);

        aimed = false;
    }

    @Override
    protected void onActiveStart() {
        swordAttackAnimation.restart();

        swordAttackSound.play(1.0f, 0.1f);

        abilityCreature.takeStaminaDamage(25f);
    }

    @Override
    protected void onStop() {
        abilityCreature.setAttacking(false);
    }

    @Override
    protected void onUpdateActive(int i) {
        updateAttackRect(i);

        swordAttackAnimation.getAnimation().update(i);

        Collection<Creature> creatures = abilityCreature.getArea().getCreatures().values();
        for (Creature creature : creatures) {
            if (creature == this.abilityCreature) continue;
            if (meleeAttackRect.intersects(creature.getRect())) {
                if (!(this.abilityCreature instanceof Mob && creature instanceof Mob)) { // mob can't hurt a mob?
                    if (!creature.isImmune()) {
                        Item weapon = this.abilityCreature.getEquipmentItems().get(0);
                        creature.takeDamage(weapon.getDamage(), true, 0.35f, abilityCreature.getRect().getCenterX(), abilityCreature.getRect().getCenterY());
                        abilityCreature.onAttack();

                        int random = Globals.random.nextInt(100);
                        if (random < weapon.getItemType().getPoisonChance() * 100f) {
                            creature.becomePoisoned();
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onUpdateChanneling(int i) {
        swordWindupAnimation.getAnimation().update(i);
        updateAttackRect(i);

        if (aimed) {
            abilityCreature.setAttackingVector(abilityCreature.getFacingVector());
        }
    }

    private void updateAttackRect(int i) {
        float attackRange = 60f;

        float attackShiftX = abilityCreature.getAttackingVector().getNormal().getX() * attackRange;
        float attackShiftY = abilityCreature.getAttackingVector().getNormal().getY() * attackRange;

        int attackWidth = 40;
        int attackHeight = 40;

        float attackRectX = attackShiftX + abilityCreature.getRect().getCenterX() - attackWidth / 2f;
        float attackRectY = attackShiftY + abilityCreature.getRect().getCenterY() - attackHeight / 2f;

        meleeAttackRect = new Rectangle(attackRectX, attackRectY, attackWidth, attackHeight);
    }

    @Override
    public void onChannellingStart() {
        abilityCreature.setAttackingVector(abilityCreature.getFacingVector());

        swordWindupAnimation.restart();

        abilityCreature.setAttacking(true);
    }

    public void render(Graphics g, Camera camera) {
        if (state == AbilityState.ABILITY_CHANNELING) {
            Image image = swordWindupAnimation.getAnimation().getCurrentFrame();
            image.setRotation((float) abilityCreature.getAttackingVector().getTheta());

            g.drawImage(image, meleeAttackRect.getX() - camera.getPosX(), meleeAttackRect.getY() - camera.getPosY());
        }
        if (state == AbilityState.ABILITY_ACTIVE) {
            Image image = swordAttackAnimation.getAnimation().getCurrentFrame();
            image.setRotation((float) abilityCreature.getAttackingVector().getTheta());

            g.drawImage(image, meleeAttackRect.getX() - camera.getPosX(), meleeAttackRect.getY() - camera.getPosY());
        }
    }

    public void setAimed(boolean aimed) {
        this.aimed = aimed;
    }

    public static SwordAttackAbility newInstance(Creature abilityCreature) {
        SwordAttackAbility ability = new SwordAttackAbility(abilityCreature);
        ability.init();
        ability.setTimerStartingPosition();
        return ability;
    }
}
