package com.kamilkurp.abilities;

import com.kamilkurp.animations.AbilityAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.AttackType;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.Mob;
import com.kamilkurp.items.Item;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;

import java.util.Collection;

public class UnarmedAttack extends MeleeAttack {
    private final Sound punchSound = Assets.punchSound;
    private boolean aimed;

    protected UnarmedAttack(Creature abilityCreature) {
        super(abilityCreature);

        attackType = AttackType.UNARMED;
    }

    @Override
    public void init() {
        float weaponSpeed = 1.0f;
        if (this.abilityCreature.getEquipmentItems().get(0) != null) {
            weaponSpeed = this.abilityCreature.getEquipmentItems().get(0).getItemType().getWeaponSpeed();
        }

        float baseChannelTime = 100f;
        float baseActiveTime = 200f;
        int numOfChannelFrames = 6;
        int numOfFrames = 6;
        int channelFrameDuration = (int)(baseChannelTime/numOfChannelFrames);
        int frameDuration = (int)(baseActiveTime/numOfFrames);

        channelTime = (int)(baseChannelTime * 1f/weaponSpeed);
        activeTime = (int)(baseActiveTime * 1f/weaponSpeed);

        cooldownTime = 500;

        windupAnimation = new AbilityAnimation(Assets.slashWindupSpriteSheet, numOfChannelFrames, channelFrameDuration);
        attackAnimation = new AbilityAnimation(Assets.betterSlashSpriteSheet, numOfFrames, frameDuration);

        meleeAttackRect = new Rectangle(-999, -999, 1, 1);

        meleeAttackHitbox = new Polygon(meleeAttackRect.getPoints());

        width = 32f;
        height = 32f;
        scale = 1.2f;
        attackRange = 30f;
        knockbackPower = 0.4f;

        aimed = false;
    }

    public static UnarmedAttack newInstance(Creature abilityCreature) {
        if (abilityCreature == null) throw new RuntimeException();
        UnarmedAttack ability = new UnarmedAttack(abilityCreature);

        ability.init();
        ability.setTimerStartingPosition();


        return ability;
    }

    @Override
    protected void onUpdateActive(int i) {
        updateAttackRect(i);

        attackAnimation.getAnimation().update(i);

        Collection<Creature> creatures = abilityCreature.getArea().getCreatures().values();
        for (Creature creature : creatures) {
            if (creature == this.abilityCreature) continue;
            if (meleeAttackRect.intersects(creature.getRect())) {
                if (!(this.abilityCreature instanceof Mob && creature instanceof Mob)) { // mob can't hurt a mob?
                    if (!creature.isImmune()) {
                        creature.takeDamage(this.abilityCreature.getUnarmedDamage(), true, 0.3f, abilityCreature.getRect().getCenterX(), abilityCreature.getRect().getCenterY());
                        abilityCreature.onAttack();
                    }
                }
            }
        }

    }
}
