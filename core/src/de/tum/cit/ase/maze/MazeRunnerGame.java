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
    //private static TextureRegion trapTextureRegion;
    private static TextureRegion enemyTextureRegion;
    private static TextureRegion keyTextureRegion;
    private static TextureRegion floorTextureRegion;


    // The texture containing all the elements
    private Texture mazeElementsTexture;
    private Texture obstaclesTexture;
    private Texture mobsTexture;

    //Maze
    private Maze maze;

    private TextureRegion fullHeartTexture;
    private TextureRegion emptyHeartTexture;
    private TextureRegion keyTexture;
    private TextureRegion noKeyTexture;


    private Music backgroundMusic;
    private Music gameOverMusic;
    private Music gameMusic;
    private Music victoryMusic;

    public Maze getMaze() {
        return maze;
    }

    // Character animation downwards
    private Animation<TextureRegion> characterDownAnimation;
    // Character animation upwards
    private Animation<TextureRegion> characterUpAnimation;
    // Character animation left
    private Animation<TextureRegion> characterLeftAnimation;
    // Character animation right
    private Animation<TextureRegion> characterRightAnimation;

    // Enemy animation downwards
    private Animation<TextureRegion> enemyDownAnimation;
    // Enemy animation upwards
    private Animation<TextureRegion> enemyUpAnimation;
    // Enemy animation left
    private Animation<TextureRegion> enemyLeftAnimation;
    // Enemy animation right
    private Animation<TextureRegion> enemyRightAnimation;




    private final NativeFileChooser fileChooser;
    public NativeFileChooser getFileChooser() {
        return fileChooser;
    }

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
        this.loadCharacterAnimations(); // Load character animations
        this.loadEnemyAnimations(); // Load enemy animations

        // Load the sprite sheet
        mazeElementsTexture = new Texture(Gdx.files.internal("basictiles.png"));
        obstaclesTexture = new Texture(Gdx.files.internal("objects.png"));
        mobsTexture = new Texture(Gdx.files.internal("mobs-sheet.salome.png"));//Enemy design by Salome Tsitskishvili

        // Initialize TextureRegions for each element
        // Adjust the coordinates (x, y) and dimensions (width, height) as per your sprite sheet layout
        wallTextureRegion = new TextureRegion(mazeElementsTexture, 0, 0, 16, 16);
        entryPointTextureRegion = new TextureRegion(mazeElementsTexture, 16, 0, 16, 16);
        exitTextureRegion = new TextureRegion(mazeElementsTexture, 0, 96, 16, 16);
        //trapTextureRegion = new TextureRegion(obstaclesTexture, 96, 42, 22, 22);
        enemyTextureRegion = new TextureRegion(mobsTexture, 144, 0, 16, 16);
        //keyTextureRegion = new TextureRegion(mazeElementsTexture, 64, 64, 16, 16);
        floorTextureRegion = new TextureRegion(mazeElementsTexture, 0, 16, 16, 16);





        // Play some background music
        // Background sound
       backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background.mp3"));
       backgroundMusic.setLooping(true);
       backgroundMusic.play();
        loadTextures();
        gameOverMusic = Gdx.audio.newMusic(Gdx.files.internal("No Hope.mp3"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("Bob&#039;s Adventures - back34.mp3"));
        victoryMusic = Gdx.audio.newMusic(Gdx.files.internal("Riverside Ride.mp3"));
        goToMenu(); // Navigate to the menu screen
    }
    public void showGameOverScreen() {
        if (gameMusic.isPlaying()) {
            gameMusic.stop();
        }
        else if (backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        }
        else if (gameOverMusic.isPlaying()) {
            gameOverMusic.stop();
        } else if (victoryMusic.isPlaying()) {
            gameOverMusic.stop();
        }
        gameOverMusic.play();
        gameOverMusic.setLooping(true);
        setScreen(new GameOverScreen(this));
    }
    public void showVictoryScreen() {
        if (gameMusic.isPlaying()) {
            gameMusic.stop();
        }
        else if (backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        }
        else if (gameOverMusic.isPlaying()) {
            gameOverMusic.stop();
        }
        victoryMusic.play();
        victoryMusic.setLooping(true);
        setScreen(new VictoryScreen(this));

    }

    /**
     * Switches to the menu screen.
     */
    public void goToMenu() {
        if (gameOverMusic.isPlaying()){ gameOverMusic.stop();}
        else if (gameMusic.isPlaying()) {
            gameMusic.stop();
        }else if (victoryMusic.isPlaying()) {
            victoryMusic.stop();
        }
        backgroundMusic.play();
        backgroundMusic.setLooping(true);
        this.setScreen(new MenuScreen(this));// Set the current screen to MenuScreen
        ;
        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the game screen if it exists
            gameScreen = null;
        }
    }

    /**
     * Switches to the game screen.
     */
    public void goToGame() {
        if (backgroundMusic.isPlaying()) {
            backgroundMusic.stop();}
        else if (gameOverMusic.isPlaying()) {
            gameOverMusic.stop();
        } else if (victoryMusic.isPlaying()) {
            gameOverMusic.stop();
        }gameMusic.play();
        gameMusic.setLooping(true);

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




    public Animation<TextureRegion> getCharacterUpAnimation() {
        return characterUpAnimation;
    }

    public Animation<TextureRegion> getCharacterLeftAnimation() {
        return characterLeftAnimation;
    }

    public Animation<TextureRegion> getCharacterRightAnimation() {
        return characterRightAnimation;
    }

    /**
     * Loads the character animations from the character.png file.
     */
    private void loadCharacterAnimations() {
        Texture walkSheet = new Texture(Gdx.files.internal("character.png"));

        int frameWidth = 16;
        int frameHeight = 32;
        int animationFrames = 4; // Number of frames per direction

        // Create animations for each direction
        characterDownAnimation = createAnimation(walkSheet, 0, frameWidth, frameHeight, animationFrames);
        characterLeftAnimation = createAnimation(walkSheet, 3, frameWidth, frameHeight, animationFrames);
        characterRightAnimation = createAnimation(walkSheet, 1, frameWidth, frameHeight, animationFrames);
        characterUpAnimation = createAnimation(walkSheet, 2, frameWidth, frameHeight, animationFrames);
    }
    /**
     * Loads the enemy animations from the mobs.png file.
     */
    private void loadEnemyAnimations() {
        Texture walkSheet = new Texture(Gdx.files.internal("mobs-sheet.salome.png"));//Enemy design by Salome Tsitskishvili

        int frameWidth = 16;
        int frameHeight = 16;
        int animationFrames = 3; // Number of frames per direction

        // Create animations for each direction
        enemyDownAnimation = createAnimation(walkSheet, 0, frameWidth, frameHeight, animationFrames);
        enemyLeftAnimation = createAnimation(walkSheet, 3, frameWidth, frameHeight, animationFrames);
        enemyRightAnimation = createAnimation(walkSheet, 1, frameWidth, frameHeight, animationFrames);
        enemyUpAnimation = createAnimation(walkSheet, 2, frameWidth, frameHeight, animationFrames);
    }
    private void loadTextures() {
        spriteBatch = new SpriteBatch(); // Create SpriteBatch
        Texture spriteSheet = new Texture(Gdx.files.internal("objects.png")); // Adjust path and coordinates
        Texture noKeytexture = new Texture(Gdx.files.internal("key-white.png"));
        Texture keytexture = new Texture(Gdx.files.internal("key-blue.png"));
        fullHeartTexture = new TextureRegion(spriteSheet, 64, 0,16, 16);
        emptyHeartTexture = new TextureRegion(spriteSheet, 128, 0,16,16);
        keyTexture = new TextureRegion(keytexture,0,0, 32, 32);
        noKeyTexture = new TextureRegion(noKeytexture,0,0, 32, 32);
    }
    /**
     * Creates an animation from a specific row of a sprite sheet.
     */
    private Animation<TextureRegion> createAnimation(Texture sheet, int row, int frameWidth, int frameHeight, int frameCount) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < frameCount; i++) {
            frames.add(new TextureRegion(sheet, i * frameWidth, row * frameHeight, frameWidth, frameHeight));
        }
        return new Animation<>(0.1f, frames);
    }
    protected Animation<TextureRegion> loadTrapAnimation() {
        Texture trapSheet = new Texture(Gdx.files.internal("objects.png")); // Adjust the file name as needed

        int frameWidth = 16; // Adjust the frame width as per your sprite sheet
        int frameHeight = 16; // Adjust the frame height as per your sprite sheet
        int animationFrames = 7;// Number of frames in the trap animation
        int startCol = 4; // Starting column for trap animation frames (adjust as needed)
        int startRow = 3; // Starting row for trap animation frames (adjust as needed)


        Array<TextureRegion> trapFrames = new Array<>(TextureRegion.class);

        // Extract frames for the trap animation
        for (int col = 0; col < animationFrames; col++) {
            // Calculate the x and y position for each frame in the sprite sheet
            int x = (startCol + col) * frameWidth;
            int y = startRow * frameHeight;

            trapFrames.add(new TextureRegion(trapSheet, x, y, frameWidth, frameHeight));
        }

        return new Animation<>(0.1f, trapFrames); // Adjust the frame duration as needed
    }
    protected Animation<TextureRegion> loadKeyAnimation() {
        Texture keySheet = new Texture(Gdx.files.internal("objects.png")); // Make sure this path is correct

        int frameWidth = 16; // Make sure these dimensions match your sprite sheet
        int frameHeight = 16;
        int animationFrames = 4; // Ensure you have 9 frames in the sprite sheet
        int startCol = 0; // Starting column for trap animation frames (adjust as needed)
        int startRow = 4; // Starting row for trap animation frames (adjust as needed)

        Array<TextureRegion> keyFrames = new Array<>(TextureRegion.class);

        for (int col = 0; col < animationFrames; col++) {
            // Calculate the x and y position for each frame in the sprite sheet
            int x = (startCol + col) * frameWidth;
            int y = startRow * frameHeight;

            keyFrames.add(new TextureRegion(keySheet, x, y, frameWidth, frameHeight));
        }


        return new Animation<>(0.1f, keyFrames); // Adjust the frame duration as needed
    }
    protected Animation<TextureRegion> loadLavaAnimation() {
        Texture lavaSheet = new Texture(Gdx.files.internal("lava.png")); // Adjust the file name as needed

        int frameWidth = 16; // Adjust the frame width as per your sprite sheet
        int frameHeight = 16; // Adjust the frame height as per your sprite sheet
        int animationFrames = 45; // Number of frames in the lava animation

        Array<TextureRegion> lavaFrames = new Array<>(TextureRegion.class);

        // Extract frames for the lava animation from all columns in a single row
        for (int col = 0; col < animationFrames; col++) {
            // Calculate the x and y position for each frame in the sprite sheet


            lavaFrames.add(new TextureRegion(lavaSheet, col*frameWidth, 0, frameWidth, frameHeight));
        }

        return new Animation<>(0.1f, lavaFrames); // Adjust the frame duration as needed
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
    //public static TextureRegion getTrapTextureRegion() { return trapTextureRegion; }
    public static TextureRegion getEnemyTextureRegion() { return enemyTextureRegion; }
    public static TextureRegion getKeyTextureRegion() { return keyTextureRegion; }
    public static TextureRegion getFloorTextureRegion() {
        return floorTextureRegion;
    }

    public TextureRegion getFullHeartTexture() {
        return fullHeartTexture;
    }

    public TextureRegion getEmptyHeartTexture() {
        return emptyHeartTexture;
    }

    public TextureRegion getKeyTexture() {
        return keyTexture;
    }

    public TextureRegion getNoKeyTexture() {
        return noKeyTexture;
    }

    public Animation<TextureRegion> getEnemyDownAnimation() {
        return enemyDownAnimation;
    }

    public Animation<TextureRegion> getEnemyUpAnimation() {
        return enemyUpAnimation;
    }

    public Animation<TextureRegion> getEnemyLeftAnimation() {
        return enemyLeftAnimation;
    }

    public Animation<TextureRegion> getEnemyRightAnimation() {
        return enemyRightAnimation;
    }
}
