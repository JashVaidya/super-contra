package com.jashvaidya.kontra.Sprites.items;

/**
 * Brick class provides the physics of the brick structure and it's reaction to the world
 *
 * @author Jash Vaidya
 * @version 1.0
 */

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.jashvaidya.kontra.Project5Driver;
import com.jashvaidya.kontra.Scenes.Hud;
import com.jashvaidya.kontra.Screens.Play;
import com.jashvaidya.kontra.Sprites.Lance;

public class Brick extends InteractiveTileObject {
    public Brick(Play screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(Project5Driver.BRICK_BIT);
    }

    /**
     * onHeadHit method is called when Lance hits the brick with his head. The brick will only break if he is transformed otherwise it will just play a "bump" sound.
     * If the brick breaks, then a break sound will be played and some points will be added to the score
     * To win the game, a certain brick has to be broken that will release the Win item.
     * @param lance, the player instance for the method to check if lance has been transformed or not
     */
    @Override
    public void onHeadHit(Lance lance) {
        if (lance.isLanceTransformed()) {
            setCategoryFilter(Project5Driver.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(200);
            Project5Driver.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
            if (object.getProperties().containsKey("win"))
                screen.spawnItem(new ItemDefinition(new Vector2(body.getPosition().x, body.getPosition().y + 16 / Project5Driver.PPM),
                        Win.class));
        } else
            Project5Driver.manager.get("audio/sounds/bump.wav", Sound.class).play();
    }
}
