package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;
import games.spooky.gdx.nativefilechooser.NativeFileChooserIntent;

public class VictoryScreen implements Screen {
    private final MazeRunnerGame game;
    private final Stage stage;

    public VictoryScreen(MazeRunnerGame game) {
        this.game = game;
        OrthographicCamera camera = new OrthographicCamera();
        camera.zoom = 1.5f;
        stage = new Stage(new ScreenViewport(camera), game.getSpriteBatch());

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label victoryLabel = new Label("Victory", game.getSkin(), "title");
        table.add(victoryLabel).padBottom(80).row();

        //Select map
        TextButton SelectMap = new TextButton("Select map", game.getSkin());
        table.add(SelectMap).width(300).row();
        SelectMap.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                var fileChooserConfig = new NativeFileChooserConfiguration();
                fileChooserConfig.title = "Pick a maze file"; // Title of the window that will be opened
                fileChooserConfig.intent = NativeFileChooserIntent.OPEN; // We want to open a file
                fileChooserConfig.nameFilter = (file, name) -> name.endsWith("properties"); // Only accept .properties files
                fileChooserConfig.directory = Gdx.files.absolute(System.getProperty("user.home")); // Open at the user's home directory
                game.getFileChooser().chooseFile(fileChooserConfig, new NativeFileChooserCallback() {
                    @Override
                    public void onFileChosen(FileHandle fileHandle) {
                        // Do something with fileHandle
                        // Load the maze and switch to the game screen with the chosen maze file
                        game.loadMaze(fileHandle);
                    }

                    @Override
                    public void onCancellation() {
                        // User closed the window, don't need to do anything
                    }

                    @Override
                    public void onError(Exception exception) {
                        System.err.println("Error picking maze file: " + exception.getMessage());
                    }
                });
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

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        // Handle pause
    }

    @Override
    public void resume() {
        // Handle resume
    }

    @Override
    public void hide() {
        // Handle when the screen is hidden
    }

    @Override
    public void dispose() {

        // Dispose of other resources if necessary
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }
}
