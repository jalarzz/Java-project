package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen implements Screen {

    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private final BitmapFont font;

    private float sinusInput = 0f;

    private Array<MazeElement> mazeElements;



    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public GameScreen(MazeRunnerGame game) {
        this.game = game;

        this.mazeElements = new Array<>();

        this.loadMazeElements();

        // Create and configure the camera for the game view
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.zoom = 0.75f;

        // Get the font from the game's skin
        font = game.getSkin().getFont("font");

        /**
         * create game character here and make him start at the spawn. at the same time, it should
         * have a connection with the camera as when the player moves, the camera also should
         * I recommend that the input will be checked after the character has successfully
         * moved around or done an action
         * there can be a priority list for what action should be counted first if multiple inputs are there
         */

    }

    //Load maze elements
    private void loadMazeElements() {
        // Assuming Maze class provides the layout and methods to access it
        int[][] layout = game.getMaze().getLayout();
        for (int i = 0; i < layout.length; i++) {
            for (int j = 0; j < layout[i].length; j++) {
                MazeElement element = createElementFromType(layout[i][j], i, j);
                if (element != null) {
                    mazeElements.add(element);
                }
            }
        }
    }

    //Create elements of each type
    private MazeElement createElementFromType(int type, int x, int y) {
        // The x and y coordinates might need to be adjusted or scaled
        // depending on your game's coordinate system and tile size.
        final int tileSize = 16; // Example tile size, adjust as needed.

        switch (type) {
            case -1: // Floor
                return new Floor(MazeRunnerGame.getFloorTextureRegion(), x * tileSize, y * tileSize);

            case 0: // Wall
                return new Wall(MazeRunnerGame.getWallTextureRegion(),x * tileSize, y * tileSize);

            case 1: // Entry point
                return new EntryPoint(MazeRunnerGame.getEntryPointTextureRegion(),x * tileSize, y * tileSize);

            case 2: // Exit
                return new Exit(MazeRunnerGame.getExitTextureRegion(),x * tileSize, y * tileSize);

            case 3: // Trap (static obstacle)
                return new Trap(MazeRunnerGame.getTrapTextureRegion(),x * tileSize, y * tileSize);

            case 4: // Enemy (dynamic obstacle)
                return new Enemy(MazeRunnerGame.getEnemyTextureRegion(),x * tileSize, y * tileSize);

            case 5: // Key
                return new Key(MazeRunnerGame.getKeyTextureRegion(),x * tileSize, y * tileSize);

            default:
                return null; // For undefined types, return null
        }
    }


    // Screen interface methods with necessary functionality
    @Override
    public void render(float delta) {
        // Check for escape key press to go back to the menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.goToMenu();
        }

        ScreenUtils.clear(0, 0, 0, 1); // Clear the screen

        camera.update(); // Update the camera

        // Move text in a circular path to have an example of a moving object
        sinusInput += delta;
        float textX = (float) (camera.position.x + Math.sin(sinusInput) * 100);
        float textY = (float) (camera.position.y + Math.cos(sinusInput) * 100);

        // Set up and begin drawing with the sprite batch
        game.getSpriteBatch().setProjectionMatrix(camera.combined);


        game.getSpriteBatch().begin(); // Important to call this before drawing anything

        // Render the text
       // font.draw(game.getSpriteBatch(), "Press ESC to go to menu", textX, textY);

       /* // Draw the character next to the text :) / We can reuse sinusInput here
        game.getSpriteBatch().draw(
                game.getCharacterDownAnimation().getKeyFrame(sinusInput, true),
                textX - 96,
                textY - 64,
                64,
                128
        );*/
        // Render the maze elements
       /* for (MazeElement element : mazeElements) {
            element.draw(game.getSpriteBatch());
        }*/
        for (MazeElement element : mazeElements) {
            game.getSpriteBatch().draw(
                    MazeRunnerGame.getFloorTextureRegion(),
                    element.getX() + camera.position.x,
                    element.getY() + camera.position.y

            );
            if (element instanceof Wall || element instanceof Exit) {
                // For Walls and Exits, render them normally without floor below
                element.draw(game.getSpriteBatch(), camera);
            }
            else if(element instanceof Enemy enemy){
                game.getCharacterDownAnimation().getKeyFrame(sinusInput, true);
                enemy.draw(game.getSpriteBatch(), camera);
            }
            else {
                // For other elements, render the floor below first

                // Then, render the element itself on top
                element.draw(game.getSpriteBatch(), camera);
            }
        }

        game.getSpriteBatch().end(); // Important to call this after drawing everything
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }

    // Additional methods and logic can be added as needed for the game screen
}
