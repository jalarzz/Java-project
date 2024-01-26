package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */@SuppressWarnings("unchecked")
public class GameScreen implements Screen {

    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private final BitmapFont font;
    private Maze maze;



    private float sinusInput = 0f;

    private Array<MazeElement> mazeElements;
    private Character playerCharacter; // The player-controlled character

    private HUD hud;



    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public GameScreen(MazeRunnerGame game) {
        this.game = game;
        this.maze = game.getMaze();

        this.mazeElements = new Array<>();

        this.loadMazeElements();

        // Create and configure the camera for the game view
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.zoom = 0.35f;

        // Get the font from the game's skin
        font = game.getSkin().getFont("font");

        // Get the textures for the HUD
        TextureRegion fullHeart = game.getFullHeartTexture();
        TextureRegion emptyHeart = game.getEmptyHeartTexture();
        TextureRegion keyTexture = game.getKeyTexture();
        TextureRegion noKeyTexture = game.getNoKeyTexture();

        // Initialize HUD
        hud = new HUD(fullHeart, emptyHeart, keyTexture, noKeyTexture, 5, 0);

        /**
         * todo:
         * create game character here and make him start at the spawn. at the same time, it should
         * have a connection with the camera as when the player moves, the camera also should
         * I recommend that the input will be checked after the character has successfully
         * moved around or done an action
         * there can be a priority list for what action should be counted first if multiple inputs are there
         */
        // Create the player character
        initializePlayerCharacter();



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
    /**
     * Initializes the player character with animation and positions it at the entry point.
     */
    private void initializePlayerCharacter() {
        EntryPoint entryPoint = findEntryPoint();
        if (entryPoint != null) {
            int initialLives = 5; // Number of lives for the character
            Animation<TextureRegion>[] animations = new Animation[]{
                    game.getCharacterDownAnimation(),
                    game.getCharacterLeftAnimation(),
                    game.getCharacterRightAnimation(),
                    game.getCharacterUpAnimation()
            };
            playerCharacter = new Character(animations, entryPoint.getX(), entryPoint.getY(), initialLives,camera);
            Gdx.app.log("GameScreen", "Character initialized at (" + entryPoint.getX() + ", " + entryPoint.getY() + ")");
        }else {
            Gdx.app.error("GameScreen", "Entry point not found, character not initialized");
        }
    }

    /**
     * Finds the entry point element from the maze elements.
     * @return The entry point element.
     */
    private EntryPoint findEntryPoint() {
        for (MazeElement element : mazeElements) {
            if (element instanceof EntryPoint) {
                return (EntryPoint) element;
            }
        }
        throw new IllegalStateException("Entry point not found in the maze.");
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

            case 3: // Trap
                Animation<TextureRegion> trapAnimation = game.loadTrapAnimation();
                return new Trap(trapAnimation, x * tileSize, y * tileSize);

            case 4: // Enemy (dynamic obstacle)
                return new Enemy(MazeRunnerGame.getEnemyTextureRegion(),x * tileSize, y * tileSize);

            case 5: // Key
                Animation<TextureRegion> keyAnimation = game.loadKeyAnimation();
                return new Key(keyAnimation, x * tileSize, y * tileSize);
            case 6: // Lava
                Animation<TextureRegion> lavaAnimation = game.loadLavaAnimation();
                return new Lava(lavaAnimation, x * tileSize, y * tileSize);

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

        // Update camera to center on the character
        camera.position.set(playerCharacter.getX(), playerCharacter.getY(), 0);
        camera.update();
        // Handle input for character movement
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerCharacter.move(Direction.LEFT, game.getMaze(), delta);
        } if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerCharacter.move(Direction.RIGHT, game.getMaze(),delta);
        }  if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            playerCharacter.move(Direction.UP, game.getMaze(),delta);
        }  if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            playerCharacter.move(Direction.DOWN, game.getMaze(),delta);
        }
        playerCharacter.update(Gdx.graphics.getDeltaTime());
        /*// Move text in a circular path to have an example of a moving object
        sinusInput += delta;
        float textX = (float) (camera.position.x + Math.sin(sinusInput) * 100);
        float textY = (float) (camera.position.y + Math.cos(sinusInput) * 100);*/

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
            // Draw the floor for every element
            game.getSpriteBatch().draw(
                    MazeRunnerGame.getFloorTextureRegion(),
                    element.getX() ,
                    element.getY()
            );

            if (element instanceof Trap) {
                Trap trap = (Trap) element;
                trap.update(Gdx.graphics.getDeltaTime());
                trap.draw(game.getSpriteBatch());
            } else if (element instanceof Wall || element instanceof Exit ) {
                // For Walls and Exits, render them normally without floor below
                element.draw(game.getSpriteBatch());
            } else if (element instanceof Enemy) {
                // If there are special considerations for enemies, handle them here
                Enemy enemy = (Enemy) element;
                enemy.draw(game.getSpriteBatch());
            } else if (element instanceof Lava) {
                Lava lava = (Lava) element;
                lava.update(delta); // Update the lava animation
                lava.draw(game.getSpriteBatch());

            } else if (element instanceof Key) {
                Key key = (Key) element;
                key.update(delta); // Update the key animation
                key.draw(game.getSpriteBatch());
            } else
            {
                // For other elements, render them on top of the floor
                element.draw(game.getSpriteBatch());
            }
        }
        if (playerCharacter != null) {
            playerCharacter.update(Gdx.graphics.getDeltaTime());
            playerCharacter.draw(game.getSpriteBatch());
            Gdx.app.log("GameScreen", "Character drawn at (" + playerCharacter.getX() + ", " + playerCharacter.getY() + ")");
        } else {
            Gdx.app.error("GameScreen", "Character is null, not drawn");
        }

        game.getSpriteBatch().end(); // Important to call this after drawing everything
        hud.updateHearts(playerCharacter.getLives(), game.getFullHeartTexture(), game.getEmptyHeartTexture());
        if (playerCharacter.getLives() <= 0) {
            game.showGameOverScreen();
        }

        playerCharacter.updateStatus(game.getMaze());
        hud.updateKey(playerCharacter.hasKey());
        playerCharacter.updateStatus(game.getMaze());
        hud.updateExit(playerCharacter.hasReachedExit());
        playerCharacter.update(Gdx.graphics.getDeltaTime());
        playerCharacter.updateStatus(game.getMaze()); // Update character status based on current position in the maze
        // Check if player has reached the exit and has the key
        if (playerCharacter.hasKey() && maze.checkCollision(playerCharacter.getBounds(), true) ==7 ) {
            game.showVictoryScreen();
        }
        hud.draw();}
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