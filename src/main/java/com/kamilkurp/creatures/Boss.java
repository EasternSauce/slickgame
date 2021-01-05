package com.kamilkurp.creatures;

import com.kamilkurp.Globals;
import com.kamilkurp.abilities.Ability;
import com.kamilkurp.spawn.MobSpawnPoint;
import com.kamilkurp.systems.GameSystem;
import org.newdawn.slick.Music;

public class Boss extends Mob {

    protected Music bossMusic;

    protected boolean bossBattleStarted;

    public Boss(GameSystem gameSystem, MobSpawnPoint mobSpawnPoint, String id) {
        super(gameSystem, mobSpawnPoint, id);

        isBoss = true;
        bossBattleStarted = false;
        knocbackable = false;

    }

    @Override
    public void onInit() {

    }

    @Override
    public String getCreatureType() {
        return null;
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

    public void performIdleBehavior() {
        // stay put
    }

}
