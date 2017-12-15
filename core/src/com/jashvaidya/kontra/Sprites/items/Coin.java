package com.jashvaidya.kontra.Sprites.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.jashvaidya.kontra.Project5Driver;
import com.jashvaidya.kontra.Scenes.Hud;
import com.jashvaidya.kontra.Screens.Play;
import com.jashvaidya.kontra.Sprites.Lance;

public class Coin extends InteractiveTileObject {

    private static TiledMapTileSet tileSet;
    private final int BLANK = 28;

    public Coin(Play screen, MapObject object) {
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(Project5Driver.COIN_BIT);
    }

    /**
     * onHeadHit method is called when Lance hits a coin with his head. If the coin has been hit once, then the coin will turn into a blank block.
     * Also handles which block to spawn a Medal
     * @param lance, passes the player instance of Lance
     */
    @Override
    public void onHeadHit(Lance lance) {
        Gdx.app.log("Coin", "Collision");
        if (getCell().getTile().getId() == BLANK)
            Project5Driver.manager.get("audio/sounds/bump.wav", Sound.class).play();
        else {
            if (object.getProperties().containsKey("medal")) {
                screen.spawnItem(new ItemDefinition(new Vector2(body.getPosition().x, body.getPosition().y + 16 / Project5Driver.PPM),
                        Medal.class));
                Project5Driver.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            }
            Project5Driver.manager.get("audio/sounds/coin.wav", Sound.class).play();
            Hud.addScore(100);
        }
        getCell().setTile(tileSet.getTile(BLANK));

    }

}
