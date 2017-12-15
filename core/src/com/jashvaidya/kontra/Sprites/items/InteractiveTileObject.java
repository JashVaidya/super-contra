package com.jashvaidya.kontra.Sprites.items;
/**
 * InteractiveTileObject class is an abstract class that builds a structure for the brick and coin classes
 * @author Jash Vaidya
 * @verison 1.0
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.jashvaidya.kontra.Project5Driver;
import com.jashvaidya.kontra.Screens.Play;
import com.jashvaidya.kontra.Sprites.Lance;

public abstract class InteractiveTileObject {
    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Rectangle bounds;
    protected Body body;
    protected Play screen;
    protected Fixture fixture;
    protected MapObject object;

    public InteractiveTileObject(Play screen, MapObject object) {
        this.object = object;
        this.screen = screen;
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.bounds = ((RectangleMapObject) object).getRectangle();

        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((bounds.getX() + bounds.getWidth() / 2) / Project5Driver.PPM, (bounds.getY() + bounds.getHeight() / 2) / Project5Driver.PPM);

        body = world.createBody(bodyDef);

        shape.setAsBox((bounds.getWidth() / 2) / Project5Driver.PPM, (bounds.getHeight() / 2) / Project5Driver.PPM);
        fixtureDef.shape = shape;
        fixture = body.createFixture(fixtureDef);
    }

    public abstract void onHeadHit(Lance lance);

    /**
     * Sets the categoryBits for classes that extend InteractiveTileObject
     * @param filterBit, the filter of the class
     */
    public void setCategoryFilter(short filterBit) {
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    /**
     * getCell gets the location of the cell being referred to
     * @return the position of the cell
     */
    public TiledMapTileLayer.Cell getCell() {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);
        return layer.getCell((int) (body.getPosition().x * Project5Driver.PPM / 16),
                (int) (body.getPosition().y * Project5Driver.PPM / 16));
    }
}
