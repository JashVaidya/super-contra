package com.jashvaidya.kontra.Tools;

/**
 * Box2DWorldCreator class creates the world based on the level.tmx file
 * @author Jash Vaidya
 * @version 1.0
 */

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.jashvaidya.kontra.Project5Driver;
import com.jashvaidya.kontra.Screens.Play;
import com.jashvaidya.kontra.Sprites.Enemies.Turtle;
import com.jashvaidya.kontra.Sprites.items.Brick;
import com.jashvaidya.kontra.Sprites.items.ItemDefinition;
import com.jashvaidya.kontra.Sprites.items.Win;

public class Box2DWorldCreator {
    private Array<com.jashvaidya.kontra.Sprites.Enemies.Goomba> goombas;
    private Array<com.jashvaidya.kontra.Sprites.Enemies.Turtle> turtles;

    public Box2DWorldCreator(Play screen) {
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        //Create body and fixture variables
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        //creating ground bodies/fixtures
        for (MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Project5Driver.PPM, (rect.getY() + rect.getHeight() / 2) / Project5Driver.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / Project5Driver.PPM, (rect.getHeight() / 2) / Project5Driver.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        //creating pipe bodies/fixtures
        for (MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / Project5Driver.PPM, (rect.getY() + rect.getHeight() / 2) / Project5Driver.PPM);

            body = world.createBody(bdef);

            shape.setAsBox((rect.getWidth() / 2) / Project5Driver.PPM, (rect.getHeight() / 2) / Project5Driver.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = Project5Driver.OBJECT_BIT;
            body.createFixture(fdef);
        }
        //creating brick bodies/fixtures
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {

            new Brick(screen, object);
        }
        //creating coin bodies/fixtures
        for (MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {

            new com.jashvaidya.kontra.Sprites.items.Coin(screen, object);
        }

        //create all goombas
        goombas = new Array<com.jashvaidya.kontra.Sprites.Enemies.Goomba>();
        for (MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            goombas.add(new com.jashvaidya.kontra.Sprites.Enemies.Goomba(screen, rect.getX() / Project5Driver.PPM, rect.getY() / Project5Driver.PPM));

        }

        turtles = new Array<com.jashvaidya.kontra.Sprites.Enemies.Turtle>();
        for (MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            turtles.add(new Turtle(screen, rect.getX() / Project5Driver.PPM, rect.getY() / Project5Driver.PPM));

        }

    }

    /**
     * getEnemies method adds all the enemies into an array that can be used to place them into the world
     * @return an array filled with goombas and turtles
     */
    public Array<com.jashvaidya.kontra.Sprites.Enemies.Enemy> getEnemies() {
        Array<com.jashvaidya.kontra.Sprites.Enemies.Enemy> enemies = new Array<com.jashvaidya.kontra.Sprites.Enemies.Enemy>();
        enemies.addAll(goombas);
        enemies.addAll(turtles);
        return enemies;
    }
}
