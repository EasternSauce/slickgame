package com.kamilkurp.behavior;

import com.kamilkurp.animations.AttackAnimation;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.creatures.Mob;
import com.kamilkurp.projectile.Arrow;
import com.kamilkurp.terrain.TerrainTile;
import com.kamilkurp.utils.Camera;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BowAttackAbility extends Ability {
    Creature creature;
    protected AttackAnimation swordAttackAnimation;
    private final Sound bowAttackSound = Assets.arrowWhizzSound;

    public BowAttackAbility(Creature creature) {
        super();

        this.creature = creature;
        cooldown = 800;
        abilityTime = 300;

        swordAttackAnimation = new AttackAnimation(Assets.betterSlashSpriteSheet, 6, 50);
        swordAttackRect = new Rectangle(-999, -999, 1, 1);

    }

    @Override
    public void update(int i) {
        if (cooldownTimer.getTime() > abilityTime) {
            active = false;
        }

        if (active) {


            float attackRange = 60f;

            float attackShiftX = creature.getAttackingVector().getNormal().getX() * attackRange;
            float attackShiftY = creature.getAttackingVector().getNormal().getY() * attackRange;

            int attackWidth = 40;
            int attackHeight = 40;

            float attackRectX = attackShiftX + creature.getRect().getCenterX() - attackWidth / 2f;
            float attackRectY = attackShiftY + creature.getRect().getCenterY() - attackHeight / 2f;

            swordAttackRect = new Rectangle(attackRectX, attackRectY, attackWidth, attackHeight);


            swordAttackAnimation.getAnimation().update(i);


            Collection<Creature> creatures = creature.getArea().getCreatures().values();
            for (Creature creature : creatures) {
                if (creature == this.creature) continue;
                if (swordAttackRect.intersects(creature.getRect())) {
                    if (!(this.creature instanceof Mob && creature instanceof Mob)) { // mob can't hurt a mob?
                        creature.takeDamage(this.creature.getEquipmentItems().get(0).getDamage());
                    }
                }
            }
        }
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

        creature.setAttackingVector(creature.getFacingVector());

        List<Arrow> arrowList = creature.getArea().getArrowList();
        List<TerrainTile> tiles = creature.getArea().getTiles();
        Map<String, Creature> areaCreatures = creature.getArea().getCreatures();

        if (!creature.getFacingVector().equals(new Vector2f(0.f, 0f))) {
            Arrow arrow = new Arrow(creature.getRect().getX(), creature.getRect().getY(), creature.getFacingVector(), arrowList, tiles, areaCreatures, this.creature);
            arrowList.add(arrow);
        }


    }

    @Override
    public void render(Graphics g, Camera camera) {

    }
}
