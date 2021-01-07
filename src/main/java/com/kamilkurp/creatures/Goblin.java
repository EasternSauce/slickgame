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

public class Goblin extends Mob {

    public Goblin(GameSystem gameSystem, MobSpawnPoint mobSpawnPoint, String id, String weapon) throws SlickException {
        super(gameSystem, mobSpawnPoint, id);

        actionTimer = new Timer(true);

        dropTable.put("ironSword", 0.03f);
        dropTable.put("poisonDagger", 0.005f);
        dropTable.put("healingPowder", 0.3f);
        dropTable.put("steelArmor", 0.03f);
        dropTable.put("steelGreaves", 0.05f);
        dropTable.put("steelGloves", 0.05f);
        dropTable.put("steelHelmet", 0.05f);

        walkAnimation = new WalkAnimation(Assets.goblinSpriteSheet, 3, 100, new int [] {3,1,0,2}, 0);

        hitbox = new Rectangle(17, 15, 30, 46);

        onGettingHitSound = Assets.evilYellingSound;

        baseSpeed = 0.3f;

        creatureType = "goblin";

        setMaxHealthPoints(300f);
        setHealthPoints(getMaxHealthPoints());

        grantWeapon(weapon);

    }

}
