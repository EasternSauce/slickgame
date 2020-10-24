package com.kamilkurp.behavior;

import com.kamilkurp.Globals;
import com.kamilkurp.assets.Assets;
import com.kamilkurp.creatures.Creature;
import com.kamilkurp.utils.Timer;
import org.newdawn.slick.Sound;
import org.newdawn.slick.geom.Vector2f;

public class DashBehavior implements Behavior{

    protected Timer dashCooldownTimer;
    protected Timer dashTimer;
    protected Vector2f dashVector;
    protected float dashSpeed = 0.0f;
    protected boolean dashing = false;
    private final Sound dogBarkSound = Assets.dogBarkSound;
    private Creature creature;

    public DashBehavior(Creature creature) {
        this.creature = creature;
        dashCooldownTimer = new Timer();
        dashTimer = new Timer();
        dashVector = new Vector2f(0f, 0f);
    }

    public void update(int i, Creature aggroed, boolean hasDestination, float destinationX, float destinationY) {
        float dashDistance = 250f;
        dashSpeed = 0.4f * i;

        if (aggroed != null){
            if (hasDestination) {
                if (dashCooldownTimer.getTime() > 3000f) {
                    if (Globals.distance(aggroed.getRect(), creature.getRect()) < dashDistance) {
                        //start dash, start dash cooldown
                        dogBarkSound.play(1.0f, 0.1f);

                        dashing = true;

                        dashVector = new Vector2f(destinationX - creature.getRect().getX(), destinationY - creature.getRect().getY()).normalise();

                        dashCooldownTimer.reset();
                        dashTimer.reset();
                    }

                }
            }

            if (dashing) {
                //end dash
                if (dashTimer.getTime() > 1000f) {
                    dashing = false;
                }
            }
        }
    }
}
