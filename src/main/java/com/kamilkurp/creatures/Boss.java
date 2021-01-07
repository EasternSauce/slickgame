package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.abilities.Ability;
import com.kamilkurp.spawn.MobSpawnPoint;
import com.kamilkurp.systems.GameSystem;
import org.newdawn.slick.Music;
import org.newdawn.slick.geom.Vector2f;

public abstract class Boss extends Mob {

    protected Music bossMusic;

    protected boolean bossBattleStarted;

    public Boss(GameSystem gameSystem, MobSpawnPoint mobSpawnPoint, String id) {
        super(gameSystem, mobSpawnPoint, id);

        isBoss = true;
        bossBattleStarted = false;
        knocbackable = false;

    }

    @Override
    public void onAggroed() {
        if (!bossBattleStarted) {
            bossBattleStarted = true;

            bossMusic.loop(1.0f, Globals.MUSIC_VOLUME);

            gameSystem.getHud().getBossHealthBar().onBossBattleStart(this);

            mobSpawnPoint.getBlockade().setActive(true);
        }
    }

    @Override
    public void onDeath() {
        gameSystem.getLootSystem().spawnLootPile(area, rect.getCenterX(), rect.getCenterY(), dropTable);

        for (Ability ability : abilityList) {
            ability.stopAbility();
        }

        currentAttack.stopAbility();

        bossMusic.stop();

        if (gameSystem.getHud().getBossHealthBar().getBoss() == this) {
            gameSystem.getHud().getBossHealthBar().hide();
        }

        mobSpawnPoint.getBlockade().setActive(false);
    }

    @Override
    public void performIdleBehavior() {
        // stay put
    }

    public Music getBossMusic() {
        return bossMusic;
    }

    public void takeDamage(float damage, boolean immunityFrames, float knockbackPower, float sourceX, float sourceY) {

        if (isAlive()) {

            float beforeHP = healthPoints;

            float actualDamage = damage * 100f/(100f + getTotalArmor());

            if (healthPoints - actualDamage > 0) healthPoints -= actualDamage;
            else healthPoints = 0f;

            if (beforeHP != healthPoints && healthPoints == 0f) {
                onDeath();
            }

            effectMap.get("immunityFrames").applyEffect(500);

            if (knocbackable && !knockback && knockbackPower > 0f) {
                this.knockbackPower = knockbackPower;

                knockbackVector = new Vector2f(rect.getX() - sourceX, rect.getY() - sourceY).getNormal();
                knockback = true;
                knockbackTimer.reset();

            }

            if (Globals.randFloat() < 0.3) onGettingHitSound.play(1.0f, 0.1f);

        }

    }
}
