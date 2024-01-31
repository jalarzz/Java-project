package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
/**
 * Represents the game over screen shown when the player loses the game.
 * This screen provides options to restart the game or return to the main menu.
 */
public class GameOverScreen implements Screen {

    private final MazeRunnerGame game;
    private final Stage stage;

    /**
     * Constructs a GameOverScreen for the provided game instance.
     *
     * @param game The instance of the MazeRunnerGame to which this screen belongs.
     */
    public GameOverScreen(MazeRunnerGame game) {
        this.game = game;
        OrthographicCamera camera = new OrthographicCamera();
        camera.zoom = 1.5f;
        stage = new Stage(new ScreenViewport(camera), game.getSpriteBatch());

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label gameOverLabel = new Label("Game Over", game.getSkin(), "title");
        table.add(gameOverLabel).padBottom(80).row();

        // Restart button
        TextButton restartButton = new TextButton("Restart Game", game.getSkin());
        table.add(restartButton).width(300).row();
        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {dispose();
                stage.dispose();
                game.goToGame();
            }
        });

        // Return to menu button
        TextButton menuButton = new TextButton("Return to Menu", game.getSkin());
        table.add(menuButton).width(300).row();
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu();
            }
        });

    }

    /**
     * Renders the game over screen and its UI elements.
     *
     * @param delta The time in seconds since the last render call.
     */

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }
    /**
     * Handles resizing the screen.
     *
     * @param width The new width of the screen.
     * @param height The new height of the screen.
     */
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

    @Override
    public void dispose() {

    }
    /**
     * Called when the game over screen becomes the current screen for a game.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

}
