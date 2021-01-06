package com.kamilkurp.abilities;

import com.kamilkurp.animations.AbilityAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.AttackType;
import com.kamilkurp.creatures.Creature;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;

public class SwordAttack extends MeleeAttack {
    private SwordAttack(Creature abilityCreature) {
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

        windupAnimation = new AbilityAnimation(Assets.slashWindupSpriteSheet, numOfChannelFrames, channelFrameDuration);
        attackAnimation = new AbilityAnimation(Assets.betterSlashSpriteSheet, numOfFrames, frameDuration);

        meleeAttackRect = new Rectangle(-999, -999, 1, 1);

        meleeAttackHitbox = new Polygon(meleeAttackRect.getPoints());

        width = 32f;
        height = 32f;
        scale = 1.5f;
        attackRange = 30f;
        knockbackPower = 0.35f;

        aimed = false;
    }

    public static SwordAttack newInstance(Creature abilityCreature) {
        SwordAttack ability = new SwordAttack(abilityCreature);
        ability.init();
        return ability;
    }
}
