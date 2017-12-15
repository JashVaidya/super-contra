package com.jashvaidya.kontra.Screens;

/**
 * Play class is ran once the game is started. This class calls other classes and creates the world for building
 *
 * @author Jash Vaidya
 * @version 1.0
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jashvaidya.kontra.Project5Driver;
import com.jashvaidya.kontra.Scenes.Hud;
import com.jashvaidya.kontra.Sprites.Enemies.Enemy;
import com.jashvaidya.kontra.Sprites.Lance;
import com.jashvaidya.kontra.Sprites.items.Item;
import com.jashvaidya.kontra.Sprites.items.ItemDefinition;
import com.jashvaidya.kontra.Sprites.items.Medal;
import com.jashvaidya.kontra.Sprites.items.Win;
import com.jashvaidya.kontra.Tools.Box2DWorldCreator;
import com.jashvaidya.kontra.Tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingDeque;

public class Play implements Screen {
    //Reference to game and the atlas texture file in assets
    private Project5Driver game;
    private TextureAtlas atlas;
    //playscreen variables, creating the camera, viewport and hud
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;

    //Tile map variables
    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;
    private Box2DWorldCreator creator;

    //Sprites
    private Lance player;
    //Music
    private Music music;

    //Items
    private Array<Item> items;
    private LinkedBlockingDeque<ItemDefinition> itemsToSpawn;

    public Play(Project5Driver game) {
        //Gets the textures from atlas file provided in assets
        atlas = new TextureAtlas("Lance_and_enemies.txt");

        this.game = game;
        //setting the camera, viewport, hud, and loading the map from the tmx file
        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(Project5Driver.V_WIDTH / Project5Driver.PPM, Project5Driver.V_HEIGHT / Project5Driver.PPM, gamecam);
        hud = new Hud(game.batch);
        maploader = new TmxMapLoader();
        map = maploader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / Project5Driver.PPM);
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        //setting the world and box2D bodies/collisions
        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();
        creator = new Box2DWorldCreator(this);
        //setting Lance in the Game World
        player = new Lance(this);

        //Sets the Contact listener to WorldContactListener, which checks for any collisions that occurs in the game
        world.setContactListener(new WorldContactListener());

        //Starts the music upon play and loops once finished
        music = Project5Driver.manager.get("audio/music/contra_music.ogg", Music.class);
        music.setLooping(true);
        music.setVolume(0.7f);
        music.play();

        //Creates the items (Medals and Win)
        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingDeque<ItemDefinition>();
    }


    /**
     * spawnItem method checks the Item Definition of the item created and adds it to itemsToSpawn
     * @param itemDef, itemDef parameter gives the position and the type of the item
     */
    public void spawnItem(ItemDefinition itemDef) {
        itemsToSpawn.add(itemDef);
    }

    public void spawningItems() {
        if (!itemsToSpawn.isEmpty()) {
            ItemDefinition itemDef = itemsToSpawn.poll();
            if (itemDef.type == Medal.class) {
                items.add(new Medal(this, itemDef.position.x, itemDef.position.y));
            }
            if (itemDef.type == Win.class) {
                items.add(new Win(this, itemDef.position.x, itemDef.position.y));
            }
        }

    }

    /**
     * getAtlas method allows other classes to refer to the atlas texture pack in order for the sprites to get their textures
     * @return atlas, the texture pack for each sprite
     */
    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void show() {

    }

    /**
     * handleInput method checks handles player controls for up, right, and left. Once a key is pressed, it applies a linear Impulse Force upon the player to move
     * @param dt, dt being DeltaTime, which is the second between two frames
     */
    public void handleInput(float dt) {
        if (player.currentState != Lance.State.DEAD) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
                player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2)
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2)
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
        }
//        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && player.currentState != Lance.State.JUMPING)
    }

    /**
     * gameOver method checks to see if Lance is dead and the time since death has been 3 seconds.
     * @return a boolean value whether game over has occurred or not
     */
    public boolean gameOver() {
        if (player.currentState == Lance.State.DEAD && player.getStateTimer() > 3) {
            return true;
        }
        return false;
    }

    /**
     * gameWon method checks to see if Lance has won the game and more than 1 second has passed since.
     * @return returns a boolean value whether the game has been won or not
     */
    public boolean gameWon() {
        if (player.currentState == Lance.State.WON && player.getStateTimer() > 1) {
            return true;
        }
        return false;
    }

    /**
     * hud method allows access to the Hud class. Typically used to have access to the hud's time check method
     * @return the Hud class
     */
    public Hud hud() {
        return hud;
    }

    /**
     * update method checks for several things. One is to handle all the input of the player, spawning all of the items, and updating the player's status.
     * Makes enemies active once Lance has come close enough to their bodies, checks the status of items, and when to stop the camera from following lance till death.
     * @param dt, dt being DeltaTime, the second between two frames
     */
    public void update(float dt) {
        //handles the player's controls and spawning of items
        handleInput(dt);
        spawningItems();
        //takes one step in the physics simulation, so 60 frames per second
        world.step(1 / 60f, 6, 2);

        //Activates the enemy once Lance has come close enough
        player.update(dt);
        for (Enemy enemy : creator.getEnemies()) {
            enemy.update(dt);
            if (enemy.getX() < player.getX() + 220 / Project5Driver.PPM)
                enemy.box2Dbody.setActive(true);
        }
        for (Item item : items)
            item.update(dt);

        hud.update(dt);
        if (player.currentState != Lance.State.DEAD) {
            gamecam.position.x = player.b2body.getPosition().x;
        }

        gamecam.update();
        renderer.setView(gamecam);
    }

    /**
     * render method clears the screen and updates it with the appropriate sprites and draws the game
     * @param dt, dt being DeltaTime, that is one second between two frames
     */
    @Override
    public void render(float dt) {
        //separate update logic from render
        update(dt);

        //Clear game screen with black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render game map
        renderer.render();

        //render Box2DDebugLines
        b2dr.render(world, gamecam.combined);

        //Sets the camera
        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        //Draws the enemies and items in the game
        player.draw(game.batch);
        for (Enemy enemy : creator.getEnemies())
            enemy.draw(game.batch);
        for (Item item : items)
            item.draw(game.batch);
        game.batch.end();

        //Set batch to draw HUD camera view
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        //If the game is over, then the gameOver screen will be set
        if (gameOver()) {
            game.setScreen(new GameOver(game));
            Project5Driver.manager.get("audio/sounds/death_sound.wav", Sound.class).play();
            dispose();
        }

        //if the game is won, then the gameWon screen will be set
        if (gameWon()) {
            game.setScreen(new GameWon(game));
            Project5Driver.manager.get("audio/sounds/won_sound.wav", Sound.class).play();
        }
    }

    /**
     * Setting game's width and height to the screen specifications
     * @param width, width of the game screen
     * @param height, height of the game screen
     */
    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    /**
     * getMap method allows other classes to have access to the tmx file to get the objects created in the game
     * @return map, the level file and objects within
     */
    public TiledMap getMap() {
        return map;
    }

    /**
     * getWorld method allows other classes to have access to the game World
     * @return world, the game world
     */
    public World getWorld() {
        return world;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    /**
     * dispose method disposes of the resources that were built in the game but need to be thrown away when finished
     */
    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
