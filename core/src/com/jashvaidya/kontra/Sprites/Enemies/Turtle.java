package com.jashvaidya.kontra.Sprites.Enemies;

/**
 * Turtle class creates a turtle that extends the enemy class
 *
 * @author Jash Vaidya
 * @version 1.0
 */

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.jashvaidya.kontra.Project5Driver;
import com.jashvaidya.kontra.Screens.Play;
import com.jashvaidya.kontra.Sprites.Lance;

public class Turtle extends Enemy {
    //Creating the variables
    public static final int LEFT_KICK_SPEED = -2;
    public static final int RIGHT_KICK_SPEED = 2;
    public enum State {WALKING, STANDING_SHELL, MOVING_SHELL, DEAD}
    public State currentState;
    public State previousState;
    private float stateTime;
    private Animation<TextureRegion> walkAnimation;
    private Array<TextureRegion> frames;
    private TextureRegion shell;
    private boolean destroyed;
    private float deadRotationDeg;

    public Turtle(Play screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 0, 0, 16, 24));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 16, 0, 16, 24));
        shell = new TextureRegion(screen.getAtlas().findRegion("turtle"), 64, 0, 16, 24);
        walkAnimation = new Animation<TextureRegion>(0.2f, frames);
        currentState = previousState = State.WALKING;
        deadRotationDeg = 0;

        setBounds(getX(), getY(), 16 / Project5Driver.PPM, 24 / Project5Driver.PPM);

    }

    /**
     * defineEnemy creates the body and the head of the turtle and sets the categoryBit and maskBit for appropriate collisions
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
        fixtureDef.restitution = .5f;
        fixtureDef.filter.categoryBits = Project5Driver.ENEMY_HEAD_BIT;
        box2Dbody.createFixture(fixtureDef).setUserData(this);
    }

    /**
     * onEnemyHit method checks to see which enemy the turtle has come in contact with and whether it should die or reverse its velocity
     *
     * @param enemy, the enemy in the game that comes in contact with that instance of the turtle
     */
    public void onEnemyHit(Enemy enemy) {
        if (enemy instanceof Turtle) {
            if (((Turtle) enemy).currentState == State.MOVING_SHELL && currentState != State.MOVING_SHELL) {
                killed();
            } else if (currentState == State.MOVING_SHELL && ((Turtle) enemy).currentState == State.WALKING)
                return;
            else
                reverseVelocity(true, false);
        } else if (currentState != State.MOVING_SHELL)
            reverseVelocity(true, false);
    }

    /**
     * hitOnHead method checks if Lance has jumped on top of the turtle. If he has, then the turtle will go inside of his shell
     *
     * @param lance
     */
    @Override
    public void hitOnHead(Lance lance) {
        if (currentState != State.STANDING_SHELL) {
            currentState = State.STANDING_SHELL;
            velocity.x = 0;
        } else {
            kick(lance.getX() <= this.getX() ? RIGHT_KICK_SPEED : LEFT_KICK_SPEED);
        }
    }

    /**
     * kick method is called when Lance collides with the turtle in his shell, resulting in the turtle moving in the direction of the push
     *
     * @param speed
     */
    public void kick(int speed) {
        velocity.x = speed;
        currentState = State.MOVING_SHELL;
    }

    /**
     * getFrame method checks the current state of the turtle and sets the appropriate action
     *
     * @param dt, deltaTime is the second between two frames
     * @return
     */
    public TextureRegion getFrame(float dt) {
        TextureRegion region;

        switch (currentState) {
            case STANDING_SHELL:
            case MOVING_SHELL:
                region = shell;
                break;
            case WALKING:
            default:
                region = walkAnimation.getKeyFrame(stateTime, true);
                break;
        }

        if (velocity.x > 0 && !region.isFlipX()) {
            region.flip(true, false);

        }
        if (velocity.x < 0 && region.isFlipX()) {
            region.flip(true, false);
        }
        stateTime = currentState == previousState ? stateTime + dt : 0;
        previousState = currentState;
        return region;
    }

    /**
     * update Method checks if the turtle has been in the shell form for 5 seconds with no action taken. If so, then the turtle will continue to walk again
     * Checks if the turtle has died, if died then it destroys the body and rotates it for animation effects
     *
     * @param dt, where Deltatime is the the second between two frames
     */
    @Override
    public void update(float dt) {
        setRegion(getFrame(dt));
        if (currentState == State.STANDING_SHELL && stateTime > 5) {
            currentState = State.WALKING;
            velocity.x = 1;
        }
        setPosition(box2Dbody.getPosition().x - getWidth() / 2, box2Dbody.getPosition().y - 8 / Project5Driver.PPM);
        if (currentState == State.DEAD) {
            deadRotationDeg += 3;
            rotate(deadRotationDeg);
            if (stateTime > 5 && !destroyed)
                world.destroyBody(box2Dbody);
            destroyed = true;
        } else
            box2Dbody.setLinearVelocity(velocity);
    }

    /**
     * getCurrentState gets the current state
     *
     * @return currentState, the current state of the turtle
     */
    public State getCurrentState() {
        return currentState;
    }

    /**
     * killed method checks if the turtle has died. If it has died then the state will be updated and the death animation will occur upon the turtle
     */
    public void killed() {
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = Project5Driver.NO_BIT;

        for (Fixture fixture : box2Dbody.getFixtureList())
            fixture.setFilterData(filter);
        box2Dbody.applyLinearImpulse(new Vector2(0, 5f), box2Dbody.getWorldCenter(), true);
    }

    /**
     * draw method is overridden to make sure that the turtle is only drawn to the screen if it has not been destroyed
     *
     * @param batch, where batch is the Batch where it will be drawn
     */
    public void draw(Batch batch) {
        if (!destroyed)
            super.draw(batch);
    }

}
