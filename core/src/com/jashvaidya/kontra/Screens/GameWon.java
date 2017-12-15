package com.jashvaidya.kontra.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jashvaidya.kontra.Project5Driver;

public class GameWon implements Screen {
    private Viewport viewport;
    private Stage stage;

    private Game game;

    public GameWon(Game game) {
        this.game = game;
        //Sets up the view and the camera for the GameOver screen
        viewport = new FitViewport(Project5Driver.V_WIDTH, Project5Driver.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((Project5Driver) game).batch);

        //Labels to display information upon death
        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.YELLOW);
        Label.LabelStyle font2 = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        //Table to store labels
        Table table = new Table();
        table.center();
        table.setFillParent(true);

        //Setting labels and adding to the table
        Label gameOverLabel = new Label("YOU WON!!", font);
        Label playAgainLabel = new Label("Press Any Key to Play Again", font2);
        table.add(gameOverLabel).expandX();
        table.row();
        table.add(playAgainLabel).expandX().padTop(10);

        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    /**
     * render method starts a new game once any key is pressed during the game won screen
     *
     * @param dt, dt being deltatime that is a second between two frames
     */
    @Override
    public void render(float dt) {
        if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            game.setScreen(new Play((Project5Driver) game));
            dispose();
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

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
     * Disposes the stage resource once the screen is over
     */
    @Override
    public void dispose() {
        stage.dispose();
    }
}
