package com.kamilkurp.behavior;

import com.kamilkurp.animations.AttackAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.projectile.Arrow;
import com.kamilkurp.terrain.TerrainTile;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;

import java.util.List;
import java.util.Map;

public class BowAttackAbility extends Ability {
    protected AttackAnimation swordAttackAnimation;
    private final Sound bowAttackSound = Assets.arrowWhizzSound;

    public BowAttackAbility(Creature abilityCreature) {
        super(abilityCreature);

        cooldownTime = 800;
        activeTime = 300;
        channelTime = 150;
    }

    @Override
    protected void onAbilityStart() {

        activeTimer.reset();

        bowAttackSound.play(1.0f, 0.1f);

        abilityCreature.setAttackingVector(abilityCreature.getFacingVector());

        List<Arrow> arrowList = abilityCreature.getArea().getArrowList();
        List<TerrainTile> tiles = abilityCreature.getArea().getTiles();
        Map<String, Creature> areaCreatures = abilityCreature.getArea().getCreatures();

        if (!abilityCreature.getFacingVector().equals(new Vector2f(0.f, 0f))) {
            Arrow arrow = new Arrow(abilityCreature.getRect().getX(), abilityCreature.getRect().getY(), abilityCreature.getFacingVector(), arrowList, tiles, areaCreatures, this.abilityCreature);
            arrowList.add(arrow);
        }

        abilityCreature.takeStaminaDamage(30f);

    }

    public void render(Graphics g, Camera camera) {

    }
}
