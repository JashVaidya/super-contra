package com.jashvaidya.kontra.Tools;
/**
 * WorldContactListener class detects any collisions between any two objects and calls upon the correct method to be handled
 *
 * @author Jash Vaidya
 * @verison 1.0
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.jashvaidya.kontra.Project5Driver;
import com.jashvaidya.kontra.Sprites.Enemies.Enemy;
import com.jashvaidya.kontra.Sprites.Lance;
import com.jashvaidya.kontra.Sprites.items.InteractiveTileObject;
import com.jashvaidya.kontra.Sprites.items.Item;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        //Fixture A and B are body fixtures that are referred to when two objects in the world collide with one another
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef) {
            case Project5Driver.LANCE_HEAD_BIT | Project5Driver.BRICK_BIT:
            case Project5Driver.LANCE_HEAD_BIT | Project5Driver.COIN_BIT:
                if (fixA.getFilterData().categoryBits == Project5Driver.LANCE_HEAD_BIT)
                    ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Lance) fixA.getUserData());
                else
                    ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Lance) fixB.getUserData());
                break;
            case Project5Driver.ENEMY_HEAD_BIT | Project5Driver.LANCE_BIT:
                if (fixA.getFilterData().categoryBits == Project5Driver.ENEMY_HEAD_BIT)
                    ((Enemy) fixA.getUserData()).hitOnHead((Lance) fixB.getUserData());
                else
                    ((Enemy) fixB.getUserData()).hitOnHead((Lance) fixA.getUserData());
                break;
            case Project5Driver.ENEMY_BIT | Project5Driver.OBJECT_BIT:
                if (fixA.getFilterData().categoryBits == Project5Driver.ENEMY_BIT)
                    ((Enemy) fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy) fixB.getUserData()).reverseVelocity(true, false);
                break;
            case Project5Driver.LANCE_BIT | Project5Driver.ENEMY_BIT:
                Gdx.app.log("LANCE", "DIED");
                if (fixA.getFilterData().categoryBits == Project5Driver.LANCE_BIT)
                    ((Lance) fixA.getUserData()).hit((Enemy) fixB.getUserData());
                else
                    ((Lance) fixB.getUserData()).hit((Enemy) fixA.getUserData());
                break;
            case Project5Driver.ENEMY_BIT | Project5Driver.ENEMY_BIT:
                ((Enemy) fixA.getUserData()).onEnemyHit((Enemy) fixB.getUserData());
                ((Enemy) fixB.getUserData()).onEnemyHit((Enemy) fixA.getUserData());
                break;
            case Project5Driver.ITEM_BIT | Project5Driver.OBJECT_BIT:
                if (fixA.getFilterData().categoryBits == Project5Driver.ITEM_BIT)
                    ((Item) fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Item) fixB.getUserData()).reverseVelocity(true, false);
                break;
            case Project5Driver.ITEM_BIT | Project5Driver.LANCE_BIT:
                if (fixA.getFilterData().categoryBits == Project5Driver.ITEM_BIT)
                    ((Item) fixA.getUserData()).use((Lance) fixB.getUserData());
                else
                    ((Item) fixB.getUserData()).use((Lance) fixA.getUserData());
                break;
        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
