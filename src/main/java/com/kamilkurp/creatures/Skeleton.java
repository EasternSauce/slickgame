package com.kamilkurp.creatures;

import com.kamilkurp.KeyInput;
import com.kamilkurp.animations.WalkAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.spawn.MobSpawnPoint;
import com.kamilkurp.systems.GameSystem;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

public class Skeleton extends Mob {

    public Skeleton(GameSystem gameSystem, MobSpawnPoint mobSpawnPoint, String id, String weapon) throws SlickException {
        super(gameSystem, mobSpawnPoint, id);

        actionTimer = new Timer(true);

        dropTable.put("ringmailGreaves", 0.1f);
        dropTable.put("leatherArmor", 0.05f);
        dropTable.put("hideGloves", 0.1f);
        dropTable.put("leatherHelmet", 0.1f);
        dropTable.put("woodenSword", 0.1f);
        dropTable.put("healingPowder", 0.5f);

        walkAnimation = new WalkAnimation(Assets.skeletonSpriteSheet, 9, 100, new int [] {0,1,2,3}, 0);

        hitbox = new Rectangle(17, 15, 30, 46);

        onGettingHitSound = Assets.boneClickSound;

        creatureType = "skeleton";

        setMaxHealthPoints(200f);
        setHealthPoints(getMaxHealthPoints());

        grantWeapon(weapon);

    }

}
