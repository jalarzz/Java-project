package de.tum.cit.ase.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;


/**
 * The MazeRunnerGame class represents the core of the Maze Runner game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 */
public class MazeRunnerGame extends Game {
    // Screens
    private MenuScreen menuScreen;
    private GameScreen gameScreen;

    // Sprite Batch for rendering
    private SpriteBatch spriteBatch;

    // UI Skin
    private Skin skin;

    // Texture regions for each maze element
    private static TextureRegion wallTextureRegion;
    private static TextureRegion entryPointTextureRegion;
    private static TextureRegion exitTextureRegion;
    private static TextureRegion trapTextureRegion;
    private static TextureRegion enemyTextureRegion;
    private static TextureRegion keyTextureRegion;

    // The texture containing all the elements
    private Texture mazeElementsTexture;
    //Maze
    private Maze maze;

    public Maze getMaze() {
        return maze;
    }

    // Character animation downwards
    private Animation<TextureRegion> characterDownAnimation;

    public NativeFileChooser getFileChooser() {
        return fileChooser;
    }

    private final NativeFileChooser fileChooser;

    /**
     * Constructor for MazeRunnerGame.
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */
    public MazeRunnerGame(NativeFileChooser fileChooser) {
        super();
        this.fileChooser = fileChooser;
    }

    /**
     * Called when the game is created. Initializes the SpriteBatch and Skin.
     */
    @Override
    public void create() {
        spriteBatch = new SpriteBatch(); // Create SpriteBatch
        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json")); // Load UI skin
        this.loadCharacterAnimation(); // Load character animation

        // Load the sprite sheet
        mazeElementsTexture = new Texture(Gdx.files.internal("basictiles.png"));

        // Initialize TextureRegions for each element
        // Adjust the coordinates (x, y) and dimensions (width, height) as per your sprite sheet layout
        wallTextureRegion = new TextureRegion(mazeElementsTexture, 0, 0, 16, 16);
        entryPointTextureRegion = new TextureRegion(mazeElementsTexture, 16, 0, 16, 16);
        exitTextureRegion = new TextureRegion(mazeElementsTexture, 32, 0, 16, 16);
        trapTextureRegion = new TextureRegion(mazeElementsTexture, 48, 0, 16, 16);
        enemyTextureRegion = new TextureRegion(mazeElementsTexture, 64, 0, 16, 16);
        keyTextureRegion = new TextureRegion(mazeElementsTexture, 80, 0, 16, 16);

        spriteBatch.begin();
        spriteBatch.draw(wallTextureRegion, 16, 16); // drawX, drawY are screen coordinates where you want to draw the region
        spriteBatch.end();


        // Play some background music
        // Background sound
        Music backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        goToMenu(); // Navigate to the menu screen
    }

    /**
     * Switches to the menu screen.
     */
    public void goToMenu() {
        this.setScreen(new MenuScreen(this)); // Set the current screen to MenuScreen
        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the game screen if it exists
            gameScreen = null;
        }
    }

    /**
     * Switches to the game screen.
     */
    public void goToGame() {
        this.setScreen(new GameScreen(this)); // Set the current screen to GameScreen
        if (menuScreen != null) {
            menuScreen.dispose(); // Dispose the menu screen if it exists
            menuScreen = null;
        }
    }
    public void loadMaze(FileHandle fileHandle) {
        this.maze = new Maze(fileHandle);
        goToGame(); // Go to the game screen after loading the maze
    }


    /**
     * Loads the character animation from the character.png file.
     */
    private void loadCharacterAnimation() {
        Texture walkSheet = new Texture(Gdx.files.internal("character.png"));

        int frameWidth = 16;
        int frameHeight = 32;
        int animationFrames = 4;

        // libGDX internal Array instead of ArrayList because of performance
        Array<TextureRegion> walkFrames = new Array<>(TextureRegion.class);

        // Add all frames to the animation
        for (int col = 0; col < animationFrames; col++) {
            walkFrames.add(new TextureRegion(walkSheet, col * frameWidth, 0, frameWidth, frameHeight));
        }

        characterDownAnimation = new Animation<>(0.1f, walkFrames);
    }

    /**
     * Cleans up resources when the game is disposed.
     */
    @Override
    public void dispose() {
        getScreen().hide(); // Hide the current screen
        getScreen().dispose(); // Dispose the current screen
        spriteBatch.dispose(); // Dispose the spriteBatch
        skin.dispose(); // Dispose the skin
        mazeElementsTexture.dispose(); // Dispose the texture
    }

    // Getter methods
    public Skin getSkin() {
        return skin;
    }

    public Animation<TextureRegion> getCharacterDownAnimation() {
        return characterDownAnimation;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }
    // Getters for each TextureRegion
    public static TextureRegion getWallTextureRegion() { return wallTextureRegion; }
    public static TextureRegion getEntryPointTextureRegion() { return entryPointTextureRegion; }
    public static TextureRegion getExitTextureRegion() { return exitTextureRegion; }
    public static TextureRegion getTrapTextureRegion() { return trapTextureRegion; }
    public static TextureRegion getEnemyTextureRegion() { return enemyTextureRegion; }
    public static TextureRegion getKeyTextureRegion() { return keyTextureRegion; }
}
