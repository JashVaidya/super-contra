package com.jashvaidya.kontra.Sprites.items;

/**
 * Medal is an item that allows Lance to powerup and break bricks
 *
 * @author Jash Vaidya
 * @version 1.0
 */

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.jashvaidya.kontra.Project5Driver;
import com.jashvaidya.kontra.Screens.Play;
import com.jashvaidya.kontra.Sprites.Lance;

public class Medal extends Item {
    public Medal(Play screen, float x, float y) {
        super(screen, x, y);
        setRegion(screen.getAtlas().findRegion("Lance"), 190, 0, 16, 16);
        velocity = new Vector2(0.7f, 0);
    }

    /**
     * defineItem method creates the medal's body and sets the categoryBits and maskBits
     */
    @Override
    public void defineItem() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Project5Driver.PPM);
        fixtureDef.filter.categoryBits = Project5Driver.ITEM_BIT;
        fixtureDef.filter.maskBits = Project5Driver.LANCE_BIT |
                Project5Driver.OBJECT_BIT |
                Project5Driver.GROUND_BIT |
                Project5Driver.COIN_BIT |
                Project5Driver.BRICK_BIT;

        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);
    }

    /**
     * use method is called when Lance collides with the medal
     * It will destroy the item and change Lance
     * @param lance, the player instance of Lance
     */
    @Override
    public void use(Lance lance) {
        destroy();
        lance.change();
    }

    /**
     * update method checks if the item is not destroyed sets the position and makes it move
     * @param dt, dt being deltatime which is a second between two frames
     */
    @Override
    public void update(float dt) {
        super.update(dt);
        if (!destroyed) {
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
            velocity.y = body.getLinearVelocity().y;
            body.setLinearVelocity(velocity);
        }
    }
}
