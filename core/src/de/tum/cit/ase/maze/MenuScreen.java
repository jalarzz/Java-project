package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;
import games.spooky.gdx.nativefilechooser.NativeFileChooserIntent;

/**
 * The MenuScreen class is responsible for displaying the main menu of the game.
 * It extends the LibGDX Screen class and sets up the UI components for the menu.
 */
public class MenuScreen implements Screen {

    private final Stage stage;
    private MazeRunnerGame game;
    private Texture backgroundImage;

    /**
     * Constructor for MenuScreen. Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public MenuScreen(MazeRunnerGame game) {
        this.game = game;
        var camera = new OrthographicCamera();
        camera.zoom = 1.5f; // Set camera zoom for a closer view

        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements

        // Load the background image
       backgroundImage = new Texture(Gdx.files.internal("runnert.png"));


        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage





        // Add a label as a title
       // table.add(new Label("Froggo & Capitalism", game.getSkin(), "title")).padBottom(80).row();


        //Resume the Game

        TextButton continueButton = new TextButton("Continue", game.getSkin());
        table.add(continueButton).width(300).row();
        continueButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (game.isPaused()) {
                    Gdx.app.log("MenuScreen", "Continue button clicked.");
                    game.setPaused(false); // Unpause the game
                    Gdx.app.log("MazeRunnerGame", "Game paused state: " + game.isPaused);
                    if(!game.isPaused){
                        Gdx.app.log("resume", "Game paused state: " + game.isPaused);
                        if(game.getGameScreen() == null) {
                            Gdx.app.log("Debug", "game.gameScreen is null");
                        } else {
                            Gdx.app.log("Debug", "game.gameScreen is not null");
                        }
                        game.resumeGame(); // Switch back to GameScreen//
                         }
                }
            }
        });
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
                        // Load the maze and switch to the game screen with the chosen maze file

                        game.loadMaze(fileHandle);
                        game.setPaused(false);
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
        //Adding and exit button
        TextButton exitButton = new TextButton("Exit", game.getSkin());
        table.add(exitButton).width(300).row();
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void render(float delta) {
        /// Convert RGB values to OpenGL's 0.0 to 1.0 range
        float red = 40f / 255f;
        float green = 38f / 255f;
        float blue = 38f / 255f;
        float alpha = 1f; // Fully opaque

        // Clear the screen with the specified color
        Gdx.gl.glClearColor(red, green, blue, alpha);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Ensure the spriteBatch is not already in use
        if (!game.getSpriteBatch().isDrawing()) {
            // Begin a new drawing session for the background
            game.getSpriteBatch().begin();
            game.getSpriteBatch().draw(backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            game.getSpriteBatch().end();
        }

        // Now draw the stage
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // Update the stage viewport on resize
    }

    @Override
    public void dispose() {
        // Dispose of the stage when screen is disposed
        stage.dispose();
        if (backgroundImage != null) {
            backgroundImage.dispose();
        }
    }

    @Override
    public void show() {
        // Set the input processor so the stage can receive input events
        Gdx.input.setInputProcessor(stage);

    }

    // The following methods are part of the Screen interface but are not used in this screen.
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {

    }

    public void setBackgroundImage(Texture backgroundImage) {
        this.backgroundImage = backgroundImage;
    }
}
