package com.jashvaidya.kontra.Scenes;

/**
 * HUD class displays various data about the world at the top of the screen.
 *
 * @author Jash Vaidya
 * @version 1.0
 */

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jashvaidya.kontra.Project5Driver;

public class Hud implements Disposable {
    private static Integer score;
    private static Label scoreLabel;
    //scene2D ui and viewport hud
    public Stage stage;
    private Viewport viewport;
    //Lance score/time Tracking
    private Integer timer;
    private float count;
    private boolean timeIsUp;
    //Widgets
    private Label countdownLabel;
    private Label timeLabel;
    private Label levelLabel;
    private Label worldLabel;
    private Label kontraLabel;

    public Hud(SpriteBatch sb) {

        //Setting variables
        timer = 300;
        count = 0;
        score = 0;

        //Setting the viewport to the camera on the player. It will move along with the player
        viewport = new FitViewport(Project5Driver.V_WIDTH, Project5Driver.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        //Table is used to store the various labels
        Table table = new Table();
        table.top();
        table.setFillParent(true);

        //Assigning values for the labels
        countdownLabel = new Label(String.format("%03d", timer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.RED));
        levelLabel = new Label("1-1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel = new Label("WORLD", new Label.LabelStyle(new BitmapFont(), Color.RED));
        kontraLabel = new Label("Lance", new Label.LabelStyle(new BitmapFont(), Color.RED));

        //adding the labels to the table
        table.add(kontraLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row();
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countdownLabel).expandX();

        //Adding the table to the stage to display the labels
        stage.addActor(table);
    }

    /**
     * addScore method is used when the player hits a coin or brick, resulting in adding a certain value to the score label
     *
     * @param value, value is the amount of points that will be added to the label
     */
    public static void addScore(int value) {
        score += value;
        scoreLabel.setText(String.format("%06d", score));
    }

    /**
     * The update method runs every frame in the game. In this case it only counts down on the timer by one second starting from 300
     *
     * @param dt, dt is deltatime
     */
    public void update(float dt) {
        count = count + dt;
        if (count >= 1) {
            if (timer > 0) {
                timer--;
            } else
                timeIsUp = true;
            countdownLabel.setText(String.format("%03d", timer));
            count = 0;
        }
    }

    /**
     * isTimeUp method checks what is the status of the timeIsUp boolean
     *
     * @return the variable timeIsUp which can be false or true. If true, then the timer has counted all the way down to zero and the game will be over
     */
    public boolean isTimeUp() {
        return timeIsUp;
    }

    /**
     * dispose method disposes of the stage resource once the game is finished
     */
    @Override
    public void dispose() {
        stage.dispose();
    }


}
