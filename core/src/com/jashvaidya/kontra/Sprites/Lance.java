package com.jashvaidya.kontra.Sprites;

/**
 * Lance class creates the Lance character
 * @author Jash Vaidya
 * @version 1.0
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.jashvaidya.kontra.Project5Driver;
import com.jashvaidya.kontra.Scenes.Hud;
import com.jashvaidya.kontra.Screens.Play;
import com.jashvaidya.kontra.Sprites.Enemies.Enemy;
import com.jashvaidya.kontra.Sprites.Enemies.Turtle;

public class Lance extends Sprite {
    public enum State {FALLING, JUMPING, STANDING, RUNNING, TRANSFORMING, DEAD, WON}
    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;
    private TextureRegion lanceStand;
    private Animation<TextureRegion> lanceRun;
    private Animation<TextureRegion> lanceJump;
    private TextureRegion lanceDead;
    private TextureRegion billStand;
    private Animation<TextureRegion> billJump;
    private Animation<TextureRegion> billRun;
    private Animation<TextureRegion> transform;
    private float stateTimer;
    private boolean runningRight;
    private boolean lanceTransformed;
    private boolean runTransformedAnimation;
    private boolean timeToChange;
    private boolean timeToChangeAgain;
    private boolean lanceIsDead;
    private boolean lanceHasWon;
    private Play screen;

    public Lance(Play screen) {
        //initialize default values
        this.screen = screen;
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        for (int i = 0; i < 6; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("Lance"), i * 20, 42, 15, 35));
        }
        lanceRun = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        for (int i = 0; i < 6; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("Bill"), i * 20, 42, 15, 35));
        }
        billRun = new Animation<TextureRegion>(0.1f, frames);

        frames.clear();

        frames.add(new TextureRegion(screen.getAtlas().findRegion("Bill"), 23, 7, 22, 34));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("Lance"), 23, 7, 22, 34));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("Bill"), 23, 7, 22, 34));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("Lance"), 23, 7, 22, 34));
        transform = new Animation<TextureRegion>(0.2f, frames);
        frames.clear();

        for (int i = 5; i < 10; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("Lance"), i * 20, 42, 15, 34));
        lanceJump = new Animation<TextureRegion>(0.13f, frames);
        frames.clear();

        for (int i = 5; i < 10; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("Bill"), i * 20, 42, 15, 34));
        }
        billJump = new Animation<TextureRegion>(0.13f, frames);
        frames.clear();

        lanceDead = new TextureRegion(screen.getAtlas().findRegion("Lance"), 172, 78, 22, 34);
        frames.clear();

        lanceStand = new TextureRegion(screen.getAtlas().findRegion("Lance"), 23, 7, 23, 34);
        billStand = new TextureRegion(screen.getAtlas().findRegion("Bill"), 23, 7, 22, 34);

        defineLance();
        setBounds(0, 0, 16 / Project5Driver.PPM, 16 / Project5Driver.PPM);
        setRegion(lanceStand);
    }

    /**
     * update method sets the position of lance, checks the transformation conditions, suicide key, and checks the timer
     * @param dt, dt being Delta Time, the second between two frames
     */
    public void update(float dt) {
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
        if (timeToChange)
            defineBill();
        if (timeToChangeAgain)
            redefineLance();

        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            die();
        }

        if (screen.hud().isTimeUp() && !isDead()) {
            die();
        }
    }

    /**
     * die method kills Lance. Stops the music and throws the body off screen
     */
    public void die() {
        lanceIsDead = true;
        Project5Driver.manager.get("audio/music/contra_music.ogg", Music.class).stop();
        Project5Driver.manager.get("audio/sounds/lanceDies.wav", Sound.class).play();

        Filter filter = new Filter();
        filter.maskBits = Project5Driver.NO_BIT;
        for (Fixture fixture : b2body.getFixtureList())
            fixture.setFilterData(filter);
        b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
    }

    /**
     * getFrame method checks the state of Lance and does an appropriate action
     * @param dt, DeltTime is the second between two frames
     * @return region, the texture region of the current state
     */
    public TextureRegion getFrame(float dt) {
        currentState = getState();

        TextureRegion region;
        switch (currentState) {
            case TRANSFORMING:
                region = transform.getKeyFrame(stateTimer);
                if (transform.isAnimationFinished(stateTimer))
                    runTransformedAnimation = false;
                break;
            case JUMPING:
                if (lanceTransformed) {
                    region = billJump.getKeyFrame(stateTimer);
                } else
                    region = lanceJump.getKeyFrame(stateTimer);
                break;
            case RUNNING:
                if (lanceTransformed)
                    region = billRun.getKeyFrame(stateTimer, true);
                else
                    region = lanceRun.getKeyFrame(stateTimer, true);
                break;
            case DEAD:
//                region = lanceDead.getKeyFrame(stateTimer);
                region = lanceDead;
                break;
            case FALLING:
            case STANDING:
            default:
                if (lanceTransformed)
                    region = billStand;
                else
                    region = lanceStand;
                break;
        }
        if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        } else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    /**
     * isLanceTransformed is a boolean method that checks if Lance has transformed from using the medal
     * @return lanceTransformed, a boolean returned for if lance has transformed or not
     */
    public boolean isLanceTransformed() {
        return lanceTransformed;
    }

    /**
     * getState method checks the what lance's body is doing and sets the appropriate state
     * @return returns the appropriate state of Lance
     */
    public State getState() {
        //..
        if (lanceIsDead)
            return State.DEAD;
        else if (lanceHasWon)
            return State.WON;
        else if (runTransformedAnimation)
            return State.TRANSFORMING;
        else if (b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if (b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if (b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }

    /**
     * defineBill method is called when Lance transforms. Builds the body and head and appropriate categoryBits and maskbits.
     */
    public void defineBill() {
        Vector2 currentPos = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPos.add(0, 0));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Project5Driver.PPM);
        fixtureDef.filter.categoryBits = Project5Driver.LANCE_BIT;
        fixtureDef.filter.maskBits = Project5Driver.GROUND_BIT |
                Project5Driver.COIN_BIT |
                Project5Driver.BRICK_BIT |
                Project5Driver.ENEMY_BIT |
                Project5Driver.OBJECT_BIT |
                Project5Driver.ENEMY_HEAD_BIT |
                Project5Driver.ITEM_BIT;

        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Project5Driver.PPM, 7 / Project5Driver.PPM), new Vector2(2 / Project5Driver.PPM, 7 / Project5Driver.PPM));
        fixtureDef.filter.categoryBits = Project5Driver.LANCE_HEAD_BIT;
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;

        b2body.createFixture(fixtureDef).setUserData(this);
        timeToChange = false;
    }

    /**
     * defineLance method defines Lance's body. Builds the body and head and appropriate categoryBits and maskbits.
     */
    public void defineLance() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / Project5Driver.PPM, 32 / Project5Driver.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Project5Driver.PPM);
        fixtureDef.filter.categoryBits = Project5Driver.LANCE_BIT;
        fixtureDef.filter.maskBits = Project5Driver.GROUND_BIT |
                Project5Driver.COIN_BIT |
                Project5Driver.BRICK_BIT |
                Project5Driver.ENEMY_BIT |
                Project5Driver.OBJECT_BIT |
                Project5Driver.ENEMY_HEAD_BIT |
                Project5Driver.ITEM_BIT;

        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Project5Driver.PPM, 7 / Project5Driver.PPM), new Vector2(2 / Project5Driver.PPM, 7 / Project5Driver.PPM));
        fixtureDef.filter.categoryBits = Project5Driver.LANCE_HEAD_BIT;
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;

        b2body.createFixture(fixtureDef).setUserData(this);
    }

    /**
     * change method is called when lance needs to be transformed
     */
    public void change() {
        runTransformedAnimation = true;
        lanceTransformed = true;
        timeToChange = true;
        setBounds(getX(), getY(), getWidth(), getHeight());
        Project5Driver.manager.get("audio/sounds/powerup.wav", Sound.class).play();
    }

    /**
     * hit method is called when lance collides with an enemy
     * Either powers down or dies
     * @param enemy, an instance of the enemy that comes in contact with lance
     */
    public void hit(Enemy enemy) {
        if (enemy instanceof com.jashvaidya.kontra.Sprites.Enemies.Turtle && ((com.jashvaidya.kontra.Sprites.Enemies.Turtle) enemy).getCurrentState() == com.jashvaidya.kontra.Sprites.Enemies.Turtle.State.STANDING_SHELL) {
            ((com.jashvaidya.kontra.Sprites.Enemies.Turtle) enemy).kick(this.getX() <= enemy.getX() ? com.jashvaidya.kontra.Sprites.Enemies.Turtle.RIGHT_KICK_SPEED : Turtle.LEFT_KICK_SPEED);
        } else {

            if (lanceTransformed) {
                lanceTransformed = false;
                timeToChangeAgain = true;
                Project5Driver.manager.get("audio/sounds/powerdown.wav", Sound.class).play();
            } else {
                die();
            }
        }
    }

    /**
     * redefineLance method is called when Lance has to be powered down back to his normal form
     */
    public void redefineLance() {
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / Project5Driver.PPM);

        fixtureDef.filter.categoryBits = Project5Driver.LANCE_BIT;
        fixtureDef.filter.maskBits = Project5Driver.GROUND_BIT |
                Project5Driver.COIN_BIT |
                Project5Driver.BRICK_BIT |
                Project5Driver.ENEMY_BIT |
                Project5Driver.OBJECT_BIT |
                Project5Driver.ENEMY_HEAD_BIT |
                Project5Driver.ITEM_BIT;

        fixtureDef.shape = shape;
        b2body.createFixture(fixtureDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / Project5Driver.PPM, 7 / Project5Driver.PPM), new Vector2(2 / Project5Driver.PPM, 7 / Project5Driver.PPM));
        fixtureDef.filter.categoryBits = Project5Driver.LANCE_HEAD_BIT;
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;

        b2body.createFixture(fixtureDef).setUserData(this);
        timeToChangeAgain = false;

    }

    /**
     * lanceWon method is called when lance has won the game
     */
    public void lanceWon() {
        lanceHasWon = true;
        Project5Driver.manager.get("audio/music/contra_music.ogg", Music.class).stop();

    }

    /**
     * isDead method checks the status of lance
     * @return lanceIsDead, checks if lance is dead or not
     */
    public boolean isDead() {
        return lanceIsDead;
    }

    /**
     * getStateTimer method gets the state time passed
     * @return the current time
     */
    public float getStateTimer() {
        return stateTimer;
    }



}