package com.jashvaidya.kontra.Sprites.Enemies;

/**
 * Goomba class is where a Goomba is defined which extends the Enemy class
 *
 * @author Jash Vaidya
 * @version 1.0
 */

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.jashvaidya.kontra.Project5Driver;
import com.jashvaidya.kontra.Screens.Play;
import com.jashvaidya.kontra.Sprites.Lance;

public class Goomba extends Enemy {
    //Creating the variables
    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;

    public Goomba(Play screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        for (int i = 0; i < 2; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i * 16, 0, 16, 16));
        walkAnimation = new Animation<TextureRegion>(0.4f, frames);
        stateTime = 0;
        setBounds(getX(), getY(), 16 / Project5Driver.PPM, 16 / Project5Driver.PPM);
        setToDestroy = false;
        destroyed = false;
    }

    /**
     * onEnemyHit method checks if the goomba was hit by a turtle during its moving shell form. If it is, then the goomba dies. Otherwise reverse the velocity of the enemies
     *
     * @param enemy, an enemy that can be either a goomba or turtle
     */
    public void onEnemyHit(Enemy enemy) {
        if (enemy instanceof Turtle && ((Turtle) enemy).currentState == Turtle.State.MOVING_SHELL)
            setToDestroy = true;
        else
            reverseVelocity(true, false);

    }

    /**
     * update method checks whether the goomba is dead or not. If it is, then destroy it and give the death animation
     * If it is not dead, then the goomba moves
     *
     * @param dt, DeltaTime, a second between two frames
     */
    public void update(float dt) {
        stateTime += dt;
        if (setToDestroy && !destroyed) {
            world.destroyBody(box2Dbody);
            destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"), 32, 0, 16, 16));
        } else if (!destroyed) {
            box2Dbody.setLinearVelocity(velocity);
            setPosition(box2Dbody.getPosition().x - getWidth() / 2, box2Dbody.getPosition().y - getHeight() / 2);
            setRegion(walkAnimation.getKeyFrame(stateTime, true));
            stateTime = 0;
        }
    }

    /**
     * defineEnemy method creates the body and the head of the goomba.
     */
    @Override
    protected void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(), getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        box2Dbody = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Project5Driver.PPM);
        fixtureDef.filter.categoryBits = Project5Driver.ENEMY_BIT;
        fixtureDef.filter.maskBits = Project5Driver.GROUND_BIT | Project5Driver.COIN_BIT | Project5Driver.BRICK_BIT | Project5Driver.ENEMY_BIT | Project5Driver.OBJECT_BIT |
                Project5Driver.LANCE_BIT;

        fixtureDef.shape = shape;
        box2Dbody.createFixture(fixtureDef).setUserData(this);

        //create head
        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-5, 8).scl(1 / Project5Driver.PPM);
        vertice[1] = new Vector2(5, 8).scl(1 / Project5Driver.PPM);
        vertice[2] = new Vector2(-3, 3).scl(1 / Project5Driver.PPM);
        vertice[3] = new Vector2(3, 3).scl(1 / Project5Driver.PPM);
        head.set(vertice);

        fixtureDef.shape = head;
        fixtureDef.restitution = 0.5f;
        fixtureDef.filter.categoryBits = Project5Driver.ENEMY_HEAD_BIT;
        box2Dbody.createFixture(fixtureDef).setUserData(this);
    }

    /**
     * draw method overrides the draw method to make sure it only draws the Goomba if it is not dead
     *
     * @param batch
     */
    public void draw(Batch batch) {
        if (!destroyed || stateTime < 1) {
            super.draw(batch);
        }
    }

    /*8
    hitOnHead method checks if Lance has bounced on the head of a goomba, if he has, then the goomba dies
     */
    @Override
    public void hitOnHead(Lance lance) {
        setToDestroy = true;
        Project5Driver.manager.get("audio/sounds/stomp.wav", Sound.class).play();
    }

}
