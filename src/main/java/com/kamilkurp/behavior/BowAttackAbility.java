package com.kamilkurp.behavior;

import com.kamilkurp.animations.AttackAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.Mob;
import com.kamilkurp.projectile.Arrow;
import com.kamilkurp.terrain.TerrainTile;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BowAttackAbility extends Ability {
    Creature abilityCreature;
    protected AttackAnimation swordAttackAnimation;
    private final Sound bowAttackSound = Assets.arrowWhizzSound;

    public BowAttackAbility(Creature abilityCreature) {
        super();

        this.abilityCreature = abilityCreature;
        cooldown = 800;
        abilityTime = 300;

//        swordAttackAnimation = new AttackAnimation(Assets.betterSlashSpriteSheet, 6, 50);
//        swordAttackRect = new Rectangle(-999, -999, 1, 1);

    }

    @Override
    public void update(int i) {
        if (cooldownTimer.getTime() > abilityTime) {
            active = false;
        }

//        if (active) {
//
//
//            float attackRange = 60f;
//
//            float attackShiftX = abilityCreature.getAttackingVector().getNormal().getX() * attackRange;
//            float attackShiftY = abilityCreature.getAttackingVector().getNormal().getY() * attackRange;
//
//            int attackWidth = 40;
//            int attackHeight = 40;
//
//            float attackRectX = attackShiftX + abilityCreature.getRect().getCenterX() - attackWidth / 2f;
//            float attackRectY = attackShiftY + abilityCreature.getRect().getCenterY() - attackHeight / 2f;
//
//            swordAttackRect = new Rectangle(attackRectX, attackRectY, attackWidth, attackHeight);
//
//
//            swordAttackAnimation.getAnimation().update(i);
//
//
//            Collection<Creature> creatures = abilityCreature.getArea().getCreatures().values();
//            for (Creature creature : creatures) {
//                if (creature == this.abilityCreature) continue;
//                if (swordAttackRect.intersects(creature.getRect())) {
//                    if (!(this.abilityCreature instanceof Mob && creature instanceof Mob)) { // mob can't hurt a mob?
//                        System.out.println(this.abilityCreature.getId() + " shoots " + creature.getId());
//                        creature.takeDamage(this.abilityCreature.getEquipmentItems().get(0).getDamage());
//                    }
//                }
//            }
//        }
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

        bowAttackSound.play(1.0f, 0.1f);

        abilityCreature.setAttackingVector(abilityCreature.getFacingVector());

        List<Arrow> arrowList = abilityCreature.getArea().getArrowList();
        List<TerrainTile> tiles = abilityCreature.getArea().getTiles();
        Map<String, Creature> areaCreatures = abilityCreature.getArea().getCreatures();

        if (!abilityCreature.getFacingVector().equals(new Vector2f(0.f, 0f))) {
            Arrow arrow = new Arrow(abilityCreature.getRect().getX(), abilityCreature.getRect().getY(), abilityCreature.getFacingVector(), arrowList, tiles, areaCreatures, this.abilityCreature);
            arrowList.add(arrow);
        }


    }

    @Override
    public void render(Graphics g, Camera camera) {

    }
}
