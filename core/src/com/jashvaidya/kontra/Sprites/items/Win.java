package com.jashvaidya.kontra.Sprites.items;
/**
 * Win is an item that allows Lance to win the game
 * @author Jash Vaidya
 * @version 1.0
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.jashvaidya.kontra.Project5Driver;
import com.jashvaidya.kontra.Screens.Play;
import com.jashvaidya.kontra.Sprites.Lance;

public class Win extends Item {
    public Win(Play screen, float x, float y) {
        super(screen, x, y);
        setRegion(screen.getAtlas().findRegion("Bill"), 190, 0, 16, 16);
        velocity = new Vector2(0.7f,0);
    }

    /**
     * defineItem creates the body of the win medal and sets the categoryBit and maskBits
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
     * update method sets the position of the win medal and moves it
     * @param dt, dt being deltatime which is a second between two frames
     */
    @Override
    public void update(float dt) {
        super.update(dt);
        //sets the position of the win icon in middle of the body
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2 );
        velocity.y = body.getLinearVelocity().y;
        body.setLinearVelocity(velocity);
    }

    /**
     * use method is called when Lance collides with win medal. Destroys the medal and lance wins the game
     * Log for debugging is sent to the console
     * @param lance, lance is passed in to call the won method
     */
    @Override
    public void use(Lance lance) {
        destroy();
        lance.lanceWon();
        Gdx.app.log("Win", "Collision");
    }
}
