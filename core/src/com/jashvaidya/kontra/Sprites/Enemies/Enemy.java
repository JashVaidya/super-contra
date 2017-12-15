package com.jashvaidya.kontra.Sprites.Enemies;

/**
 * Enemy is an abstract class that every enemy will be built upon
 *
 * @author Jash Vaidya
 * @version 1.0
 */

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.jashvaidya.kontra.Screens.Play;
import com.jashvaidya.kontra.Sprites.Lance;

public abstract class Enemy extends Sprite {
    //Creating the variables
    public Body box2Dbody;
    public Vector2 velocity;
    protected World world;
    protected Play screen;

    public Enemy(Play screen, float x, float y) {
        //Setting the variables
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        defineEnemy();
        velocity = new Vector2(1, 0);
        box2Dbody.setActive(false);
    }


    protected abstract void defineEnemy();

    public abstract void hitOnHead(Lance lance);

    public abstract void update(float dt);

    public abstract void onEnemyHit(Enemy enemy);

    /**
     * reverseVelocity reverses the velocity given of the enemy
     * @param x, velocity in the x-axis
     * @param y, velocity in the y-axis
     */
    public void reverseVelocity(boolean x, boolean y) {
        if (x)
            velocity.x = -velocity.x;
        if (y)
            velocity.y = -velocity.y;
    }
}
