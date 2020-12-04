package com.kamilkurp.behavior;

import com.kamilkurp.Globals;
import com.kamilkurp.animations.AttackAnimation;
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

public class TridentAttackAbility extends Ability {
    Creature abilityCreature;
    protected AttackAnimation tridentAttackAnimation;
    protected AttackAnimation tridentWindupAnimation;
    private final Sound swordAttackSound = Assets.attackSound;
    private boolean aimed;

    private float width;
    private float height;
    private float scale;
    private float attackRange;


    public TridentAttackAbility(Creature abilityCreature, boolean aimed) {
        super(abilityCreature);

        this.abilityCreature = abilityCreature;
        this.aimed = aimed;
        cooldownTime = 800;
        activeTime = 275;
        channelTime = 700;

        tridentAttackAnimation = new AttackAnimation(Assets.tridentThrustSpriteSheet, 11, 25, true);
        tridentWindupAnimation = new AttackAnimation(Assets.tridentThrustWindupSpriteSheet, 7, 100, true);
        meleeAttackRect = new Rectangle(-999, -999, 1, 1);
        meleeAttackPolygon = new Polygon(meleeAttackRect.getPoints());

        width = 64f;
        height = 32f;
        scale = 2.0f;
        attackRange = 60f;
    }

    @Override
    protected void onAbilityStart() {
        activeTimer.reset();
        tridentAttackAnimation.restart();

        swordAttackSound.play(1.0f, 0.1f);

        abilityCreature.takeStaminaDamage(25f);

    }

    @Override
    protected void onUpdateActive(int i) {
        updateAttackRect(i);

        tridentAttackAnimation.getAnimation().update(i);

        Collection<Creature> creatures = abilityCreature.getArea().getCreatures().values();
        for (Creature creature : creatures) {
            if (creature == this.abilityCreature) continue;
            if (meleeAttackRect.intersects(creature.getRect())) {
                if (!(this.abilityCreature instanceof Mob && creature instanceof Mob)) { // mob can't hurt a mob?
                    if (!creature.isImmune()) {
                        Item weapon = this.abilityCreature.getEquipmentItems().get(0);
                        creature.takeDamage(weapon.getDamage(), true);
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
        tridentWindupAnimation.getAnimation().update(i);
        updateAttackRect(i);

        if (aimed) {
            abilityCreature.setAttackingVector(abilityCreature.getFacingVector());
        }
    }

    private void updateAttackRect(int i) {
        Vector2f attackVector = abilityCreature.getAttackingVector().getNormal();

        float attackWidth = width * scale;
        float attackHeight = 32 * scale;


        float attackShiftX = attackVector.getX() * attackRange;
        float attackShiftY = attackVector.getY() * attackRange;


        float centerX = abilityCreature.getRect().getCenterX();
        float centerY = abilityCreature.getRect().getCenterY();

        float attackRectX = attackShiftX + centerX;
        float attackRectY = attackShiftY + centerY;

        meleeAttackRect = new Rectangle(attackRectX, attackRectY - 32 * scale, attackWidth, attackHeight);
        meleeAttackPolygon = new Polygon(meleeAttackRect.getPoints());

        meleeAttackPolygon = (Polygon)meleeAttackPolygon.transform(Transform.createRotateTransform((float) abilityCreature.getAttackingVector().getTheta() * 3.141593f/180f, meleeAttackRect.getX(), meleeAttackRect.getY() + 16 * scale));

    }

    @Override
    public void onChannel() {
        abilityCreature.setAttackingVector(abilityCreature.getFacingVector());

        tridentWindupAnimation.restart();
    }

    public void render(Graphics g, Camera camera) {
        Polygon polygonCopy = (Polygon)meleeAttackPolygon.transform(Transform.createTranslateTransform(-camera.getPosX(), -camera.getPosY() + height/2 * scale));
        //draw attack rect
        g.fill(polygonCopy);


        if (state == AbilityState.ABILITY_CHANNELING) {
            Image image = tridentWindupAnimation.getAnimation().getCurrentFrame();
            image.setCenterOfRotation(0, height/2 * scale);
            image.setRotation((float) abilityCreature.getAttackingVector().getTheta());


            image.draw(meleeAttackRect.getX() - camera.getPosX(), meleeAttackRect.getY() - camera.getPosY() + height/2 * scale, scale);
        }
        if (state == AbilityState.ABILITY_ACTIVE) {
            Image image = tridentAttackAnimation.getAnimation().getCurrentFrame();
            image.setCenterOfRotation(0, height/2 * scale);
            image.setRotation((float) abilityCreature.getAttackingVector().getTheta());


            image.draw(meleeAttackRect.getX() - camera.getPosX(), meleeAttackRect.getY() - camera.getPosY() + height/2 * scale, scale);
        }
    }

    public void setAimed(boolean aimed) {
        this.aimed = aimed;
    }
}
