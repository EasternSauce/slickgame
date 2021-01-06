package com.kamilkurp.abilities;

import com.kamilkurp.animations.AbilityAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.AttackType;
import com.kamilkurp.creatures.Creature;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;

public class TridentAttack extends MeleeAttack {
    private TridentAttack(Creature abilityCreature) {
        super(abilityCreature);

        attackType = AttackType.TRIDENT;
    }

    @Override
    public void init() {
        float weaponSpeed = 1.0f;
        if (this.abilityCreature.getEquipmentItems().get(0) != null) {
            weaponSpeed = this.abilityCreature.getEquipmentItems().get(0).getItemType().getWeaponSpeed();
        }


        float baseChannelTime = 600;
        float baseActiveTime = 275;
        int numOfChannelFrames = 7;
        int numOfFrames = 11;
        int channelFrameDuration = (int)(baseChannelTime/numOfChannelFrames);
        int frameDuration = (int)(baseActiveTime/numOfFrames);

        channelTime = (int)(baseChannelTime * 1f/weaponSpeed);
        activeTime = (int)(baseActiveTime * 1f/weaponSpeed);

        cooldownTime = 1300;

        windupAnimation = new AbilityAnimation(Assets.tridentThrustWindupSpriteSheet, numOfChannelFrames, channelFrameDuration, true);
        attackAnimation = new AbilityAnimation(Assets.tridentThrustSpriteSheet, numOfFrames, frameDuration, true);

        meleeAttackRect = new Rectangle(-999, -999, 1, 1);

        meleeAttackHitbox = new Polygon(meleeAttackRect.getPoints());

        width = 64f;
        height = 32f;
        scale = 1.2f;
        attackRange = 30f;
        knockbackPower = 0.5f;

        aimed = false;
    }

    public static TridentAttack newInstance(Creature abilityCreature) {
        TridentAttack ability = new TridentAttack(abilityCreature);
        ability.init();
        return ability;
    }
}
