package com.jashvaidya.kontra;
/**
 * Project5Driver class creates the necessary collision detection bits, loads the assets, and starts the game building process
 *
 * @author Jash Vaidya
 * @version 1.0
 */

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jashvaidya.kontra.Screens.Play;

public class Project5Driver extends Game {
    //Creates the variables for the screen size and conversion for Pixels Per Meter
    public static final int V_WIDTH = 400;
    public static final int V_HEIGHT = 208;
    public static final float PPM = 100;

    //Bits created that will be used to detect collisions with different objects
    public static final short NO_BIT = 0;
    public static final short GROUND_BIT = 1;
    public static final short LANCE_BIT = 2;
    public static final short BRICK_BIT = 4;
    public static final short COIN_BIT = 8;
    public static final short DESTROYED_BIT = 16;
    public static final short OBJECT_BIT = 32;
    public static final short ENEMY_BIT = 64;
    public static final short ENEMY_HEAD_BIT = 128;
    public static final short ITEM_BIT = 256;
    public static final short LANCE_HEAD_BIT = 512;

    public static AssetManager manager;
    public SpriteBatch batch;

    /**
     * create method loads the necessary assets and starts the game
     */
    @Override
    public void create() {
        batch = new SpriteBatch();
        manager = new AssetManager();
        manager.load("audio/music/contra_music.ogg", Music.class);
        manager.load("audio/sounds/coin.wav", Sound.class);
        manager.load("audio/sounds/bump.wav", Sound.class);
        manager.load("audio/sounds/breakblock.wav", Sound.class);
        manager.load("audio/sounds/powerup_spawn.wav", Sound.class);
        manager.load("audio/sounds/powerup.wav", Sound.class);
        manager.load("audio/sounds/powerdown.wav", Sound.class);
        manager.load("audio/sounds/stomp.wav", Sound.class);
        manager.load("audio/sounds/lanceDies.wav", Sound.class);
        manager.load("audio/sounds/death_sound.wav", Sound.class);
        manager.load("audio/sounds/won_sound.wav", Sound.class);
        manager.finishLoading();

        setScreen(new Play(this));
    }

    /**
     * render method renders the world
     */
    @Override
    public void render() {
        super.render();
    }

    /**
     * dispose method disposes of the resources when game is finished or when objects are destroyed
     */
    @Override
    public void dispose() {
        super.dispose();
        manager.dispose();
        batch.dispose();
    }
}
