package com.jashvaidya.kontra.Sprites.items;

/**
 * Item is an abstract class that builds the strcture for any objects that are created as an Item
 *
 * @author Jash Vaidya
 * @version 11.0
 */

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.jashvaidya.kontra.Project5Driver;
import com.jashvaidya.kontra.Screens.Play;
import com.jashvaidya.kontra.Sprites.Lance;

public abstract class Item extends Sprite {
    protected Play screen;
    protected World world;
    protected Vector2 velocity;
    protected boolean toDestroy;
    protected boolean destroyed;
    protected Body body;

    public Item(Play screen, float x, float y) {
        this.screen = screen;
        this.world = screen.getWorld();
        setPosition(x, y);
        setBounds(getX(), getY(), 16 / Project5Driver.PPM, 16 / Project5Driver.PPM);
        defineItem();
        toDestroy = false;
        destroyed = false;
    }

    public abstract void defineItem();

    public abstract void use(Lance lance);

    /**
     * update method here checks if the item has been set to be destroyed and it is not already destroyed. If true, then it will set the item to be destroyed
     * @param dt, dt being deltatime which is a second between two frames
     */
    public void update(float dt) {
        if (toDestroy && !destroyed) {
            world.destroyBody(body);
            destroyed = true;
        }
    }

    /**
     * overiding the draw method to check if the item has been destroyed or not. if it is not destroyed, then it will draw the item
     * @param batch, batch being the batch given to draw
     */
    public void draw(Batch batch) {
        if (!destroyed)
            super.draw(batch);
    }

    /**
     * destroy method sets the item to be destroyed
     * returns nothing
     */
    public void destroy() {
        toDestroy = true;
    }

    /**
     * reverseVelocity reverse the velocity between the item when it comes in contact with another item
     * @param x
     * @param y
     */
    public void reverseVelocity(boolean x, boolean y) {
        if (x)
            velocity.x = -velocity.x;
        if (y)
            velocity.y = -velocity.y;
    }
}
