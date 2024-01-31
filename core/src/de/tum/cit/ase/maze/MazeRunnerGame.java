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
    private static TextureRegion enemyTextureRegion;
    private static TextureRegion keyTextureRegion;
    private static TextureRegion floorTextureRegion;


    // The texture containing all the elements
    private Texture mazeElementsTexture;
    private Texture obstaclesTexture;
    private Texture mobsTexture;

    //Maze
    private Maze maze;

    private static TextureRegion fullHeartTexture;
    private static TextureRegion emptyHeartTexture;
    //    private TextureRegion keyTexture;
//    private TextureRegion noKeyTexture;
    private Animation<TextureRegion> noKeyAnimation;
    private Animation<TextureRegion> keyAnimation;


    private Music backgroundMusic;
    private Music gameOverMusic;
    private Music gameMusic;
    private Music victoryMusic;
    //Collectibles texture regions
    private static TextureRegion swordTextureRegion;
    private static TextureRegion lifeTextureRegion;
    private static TextureRegion shieldTextureRegion;
    private static TextureRegion closedChestTextureRegion;
    private static TextureRegion openChestTextureRegion;
    private Texture chestTexture;
    private Texture collectiblesTexture;
    protected boolean isPaused = false;

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

    // Armed animation downwards
    private Animation<TextureRegion> characterDownAnimationArmed;
    // Armed animation upwards
    private Animation<TextureRegion> characterUpAnimationArmed;
    // Armed animation left
    private Animation<TextureRegion> characterLeftAnimationArmed;
    // Armed animation right
    private Animation<TextureRegion> characterRightAnimationArmed;

    // Enemy animation downwards
    private Animation<TextureRegion> enemyDownAnimation;
    // Enemy animation upwards
    private Animation<TextureRegion> enemyUpAnimation;
    // Enemy animation left
    private Animation<TextureRegion> enemyLeftAnimation;
    // Enemy animation right
    private Animation<TextureRegion> enemyRightAnimation;

    // Enemy death animation
    private Animation<TextureRegion> enemyDeathAnimation;





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
        this.loadCharacterArmedAnimations(); // load armed character animations
        this.loadEnemyAnimations(); // Load enemy animations


        // Load the sprite sheet
        mazeElementsTexture = new Texture(Gdx.files.internal("basictiles-shee.salomet.png"));
        obstaclesTexture = new Texture(Gdx.files.internal("objects.OwOt.png"));
        mobsTexture = new Texture(Gdx.files.internal("mobs-sheet.salome.png"));//Enemy design by Salome Tsitskishvili
        chestTexture = new Texture(Gdx.files.internal("things.png"));
        collectiblesTexture = new Texture(Gdx.files.internal("objects.OwOt.png"));


        // Initialize TextureRegions for each element
        wallTextureRegion = new TextureRegion(mazeElementsTexture, 0, 32, 16, 16);
        entryPointTextureRegion = new TextureRegion(mazeElementsTexture, 64, 16, 16, 16);
        exitTextureRegion = new TextureRegion(mazeElementsTexture, 0, 96, 16, 16);
        floorTextureRegion = new TextureRegion(mazeElementsTexture, 0, 16, 16, 16);

        keyAnimation = loadKeyAnimation();
        noKeyAnimation = loadNoKeyAnimation();

        //Initialize collectibles textures:
        swordTextureRegion = new TextureRegion(collectiblesTexture, 32, 0, 16, 16); // Example path
        lifeTextureRegion = new TextureRegion(collectiblesTexture, 0, 0, 16, 16); // Example path
        shieldTextureRegion = new TextureRegion(collectiblesTexture, 16, 0, 16, 16); // Example path

        //Initialize chest textures:
        closedChestTextureRegion = new TextureRegion(chestTexture, 96, 0, 16, 16);
        openChestTextureRegion = new TextureRegion(chestTexture, 128, 48, 16, 16);


        // Play some background music
        // Background sound
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.1f);
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
        } else if (backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        } else if (gameOverMusic.isPlaying()) {
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
        } else if (backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        } else if (gameOverMusic.isPlaying()) {
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
        if (gameOverMusic.isPlaying()) {
            gameOverMusic.stop();
        } else if (gameMusic.isPlaying()) {
            gameMusic.stop();
        } else if (victoryMusic.isPlaying()) {
            victoryMusic.stop();
        }
        backgroundMusic.play();
        backgroundMusic.setLooping(true);
        this.menuScreen = new MenuScreen(this);
        this.setScreen(menuScreen);// Set the current screen to MenuScreen
    }

    /**
     * Switches to the game screen.
     */
    public void goToGame() {
        if (backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        } else if (gameOverMusic.isPlaying()) {
            gameOverMusic.stop();
        } else if (victoryMusic.isPlaying()) {
            victoryMusic.stop();
        }
        gameMusic.play();
        gameMusic.setLooping(true);
        gameScreen = new GameScreen(this);
        this.setScreen(gameScreen); // Set the current screen to GameScreen
        Gdx.app.log("Debug", "gameScreen instantiated");
        Gdx.app.log("Debug", menuScreen.toString());
        if (menuScreen != null) {
            menuScreen.dispose(); // Dispose the menu screen if it exists
            menuScreen = null;
        }
    }
    /**
     * Switches back to the game screen. Resumes game where you left off.
     */
    public void resumeGame() {
        if (backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        } else if (gameOverMusic.isPlaying()) {
            gameOverMusic.stop();
        } else if (victoryMusic.isPlaying()) {
            gameOverMusic.stop();
        }
        gameMusic.play();
        gameMusic.setLooping(true);

        this.setScreen(gameScreen); // Set the current screen to GameScreen
        if (menuScreen != null) {
            menuScreen.dispose(); // Dispose the menu screen if it exists
            menuScreen = null;
        }
    }

    /**
     * Loads a maze configuration from a given file and initiates the transition to the game screen.
     * This method creates a new Maze instance based on the file content, sets the current game state
     * to reflect the newly loaded maze, and navigates to the game screen where the maze will be displayed.
     * Logging is used to confirm the successful transition and the existence of the game screen.
     *
     * @param fileHandle The file handle representing the source file for the maze configuration.
     *                   This file should contain the maze layout and possibly other settings
     *                   related to the maze that will be interpreted by the Maze class.
     */
    public void loadMaze(FileHandle fileHandle) {
        this.maze = new Maze(fileHandle);
        goToGame(); // Go to the game screen after loading the maze
        if(this.gameScreen == null) {
            Gdx.app.log("loadMaze", "game.gameScreen is null");
        } else {
            Gdx.app.log("loadMaze", "game.gameScreen is not null");
        }
    }


    // Method to set the GameScreen
    public void setGameScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
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
        Texture walkSheet = new Texture(Gdx.files.internal("character.salome-sheet.png"));

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
     * Loads the armed character animations from the character.png file.
     */
    private void loadCharacterArmedAnimations() {
        Texture walkSheet = new Texture(Gdx.files.internal("characters.salome-sheet.png"));
        TextureRegion armedRegion = new TextureRegion(walkSheet,0,4,84,84);


        int frameWidth = 16;
        int frameHeight = 32;
        int animationFrames = 4; // Number of frames per direction

        // Create animations for each direction
        characterDownAnimationArmed = createAnimation(walkSheet, 0, frameWidth, frameHeight, animationFrames);
        characterLeftAnimationArmed = createAnimation(walkSheet, 3, frameWidth, frameHeight, animationFrames);
        characterRightAnimationArmed = createAnimation(walkSheet, 1, frameWidth, frameHeight, animationFrames);
        characterUpAnimationArmed = createAnimation(walkSheet, 2, frameWidth, frameHeight, animationFrames);
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
        enemyLeftAnimation = createAnimation(walkSheet, 1, frameWidth, frameHeight, animationFrames);
        enemyRightAnimation = createAnimation(walkSheet, 2, frameWidth, frameHeight, animationFrames);
        enemyUpAnimation = createAnimation(walkSheet, 3, frameWidth, frameHeight, animationFrames);
    }
    /**
     * Loads and initializes textures for various game elements. This includes setting up texture regions for hearts and loading other sprites from asset files.
     */
    private void loadTextures() {
        spriteBatch = new SpriteBatch(); // Create SpriteBatch
        Texture spriteSheet = new Texture(Gdx.files.internal("objects.png")); // Adjust path and coordinates
        Texture noKeytexture = new Texture(Gdx.files.internal("objects.png"));
        Texture keytexture = new Texture(Gdx.files.internal("objects.png"));
        fullHeartTexture = new TextureRegion(spriteSheet, 64, 0, 16, 16);
        emptyHeartTexture = new TextureRegion(spriteSheet, 128, 0, 16, 16);

    }

    /**
     * Creates an animation sequence from a texture sheet based on specified parameters.
     *
     * @param sheet The texture sheet containing animation frames.
     * @param row The row in the texture sheet where the animation frames are located.
     * @param frameWidth The width of each animation frame.
     * @param frameHeight The height of each animation frame.
     * @param frameCount The total number of frames in the animation sequence.
     * @return An Animation object containing the sequence of texture regions for the animation.
     */
    private Animation<TextureRegion> createAnimation(Texture sheet, int row, int frameWidth, int frameHeight, int frameCount) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < frameCount; i++) {
            frames.add(new TextureRegion(sheet, i * frameWidth, row * frameHeight, frameWidth, frameHeight));
        }
        return new Animation<>(0.1f, frames);
    }
    /**
     * Loads the animation for the key not being possessed by the player.
     *
     * @return An Animation object for the no-key state.
     */
    protected Animation<TextureRegion> loadNoKeyAnimation() {
        Texture noKeySheet = new Texture(Gdx.files.internal("objects.OwOt.png")); // Make sure this path is correct

        int frameWidth = 16; // Make sure these dimensions match your sprite sheet
        int frameHeight = 16;
        int animationFrames = 4; // Ensure you have 9 frames in the sprite sheet
        int startCol = 0; // Starting column for trap animation frames (adjust as needed)
        int startRow = 1; // Starting row for trap animation frames (adjust as needed)

        Array<TextureRegion> noKeyFrames = new Array<>(TextureRegion.class);

        for (int col = 0; col < animationFrames; col++) {
            // Calculate the x and y position for each frame in the sprite sheet
            int x = (startCol + col) * frameWidth;
            int y = startRow * frameHeight;

            noKeyFrames.add(new TextureRegion(noKeySheet, x, y, frameWidth, frameHeight));
        }
        return new Animation<>(0.1f, noKeyFrames);
    }
    /**
     * Loads the animation for traps within the game.
     *
     * @return An Animation object for traps.
     */
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
    /**
     * Loads the animation that plays when the player possesses the key.
     *
     * @return An Animation object for the key possession state.
     */
    protected Animation<TextureRegion> loadKeyAnimation() {
        Texture keySheet = new Texture(Gdx.files.internal("objects.OwOt.png"));

        int frameWidth = 16;
        int frameHeight = 16;
        int animationFrames = 4;
        int startCol = 0;
        int startRow = 4;

        Array<TextureRegion> keyFrames = new Array<>(TextureRegion.class);

        for (int col = 0; col < animationFrames; col++) {
            // Calculate the x and y position for each frame in the sprite sheet
            int x = (startCol + col) * frameWidth;
            int y = startRow * frameHeight;

            keyFrames.add(new TextureRegion(keySheet, x, y, frameWidth, frameHeight));
        }


        return new Animation<>(0.1f, keyFrames); // Adjust the frame duration as needed
    }

    /**
     * Loads the death animation for enemies.
     *
     * @return An Animation object for enemy death.
     */

    protected Animation<TextureRegion> loadEnemyDeathAnimation() {
        Texture keySheet = new Texture(Gdx.files.internal("objects.OwOt.png"));

        int frameWidth = 24;
        int frameHeight = 24;
        int animationFrames = 4;
        int startCol = 4;
        int startRow = 4;

        Array<TextureRegion> keyFrames = new Array<>(TextureRegion.class);

        for (int col = 0; col < animationFrames; col++) {
            // Calculate the x and y position for each frame in the sprite sheet
            int x = (startCol + col) * frameWidth;
            int y = startRow * frameHeight;

            keyFrames.add(new TextureRegion(keySheet, x, y, frameWidth, frameHeight));
        }


        return new Animation<>(0.1f, keyFrames);
    }
    /**
     * Loads the animation for lava in the game.
     *
     * @return An Animation object for lava.
     */

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
        // Dispose screens
        if (menuScreen != null) {
            menuScreen.dispose();
        }
        if (gameScreen != null) {
            gameScreen.dispose();
        }

        // Dispose textures
        if (mazeElementsTexture != null) {
            mazeElementsTexture.dispose();
        }
        if (obstaclesTexture != null) {
            obstaclesTexture.dispose();
        }
        if (mobsTexture != null) {
            mobsTexture.dispose();
        }
        if (chestTexture != null) {
            chestTexture.dispose();
        }
        if (collectiblesTexture != null) {
            collectiblesTexture.dispose();
        }

        // Dispose music
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }
        if (gameOverMusic != null) {
            gameOverMusic.dispose();
        }
        if (gameMusic != null) {
            gameMusic.dispose();
        }
        if (victoryMusic != null) {
            victoryMusic.dispose();
        }

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
    public static TextureRegion getWallTextureRegion() {
        return wallTextureRegion;
    }

    public static TextureRegion getEntryPointTextureRegion() {
        return entryPointTextureRegion;
    }

    public static TextureRegion getExitTextureRegion() {
        return exitTextureRegion;
    }

    //public static TextureRegion getTrapTextureRegion() { return trapTextureRegion; }
    public static TextureRegion getEnemyTextureRegion() {
        return enemyTextureRegion;
    }

    public static TextureRegion getKeyTextureRegion() {
        return keyTextureRegion;
    }

    public static TextureRegion getFloorTextureRegion() {
        return floorTextureRegion;
    }

    public TextureRegion getFullHeartTexture() {
        return fullHeartTexture;
    }

    public TextureRegion getEmptyHeartTexture() {
        return emptyHeartTexture;
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

    public Animation<TextureRegion> getEnemyDeathAnimation() {
        return enemyDeathAnimation;
    }

    // Getter methods for the collectible textures
    public TextureRegion getSwordTexture() {
        return swordTextureRegion;
    }

    public TextureRegion getLifeTexture() {
        return lifeTextureRegion;
    }

    public TextureRegion getShieldTexture() {
        return shieldTextureRegion;
    }

    public MenuScreen getMenuScreen() {
        return menuScreen;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public Texture getMazeElementsTexture() {
        return mazeElementsTexture;
    }

    public Texture getObstaclesTexture() {
        return obstaclesTexture;
    }

    public Texture getMobsTexture() {
        return mobsTexture;
    }

    public Animation<TextureRegion> getNoKeyAnimation() {
        return noKeyAnimation;
    }

    public Animation<TextureRegion> getKeyAnimation() {
        return keyAnimation;
    }

    public Music getBackgroundMusic() {
        return backgroundMusic;
    }

    public Music getGameOverMusic() {
        return gameOverMusic;
    }

    public Music getGameMusic() {
        return gameMusic;
    }

    public Music getVictoryMusic() {
        return victoryMusic;
    }

    public static TextureRegion getSwordTextureRegion() {
        return swordTextureRegion;
    }

    public static TextureRegion getLifeTextureRegion() {
        return lifeTextureRegion;
    }

    public static TextureRegion getShieldTextureRegion() {
        return shieldTextureRegion;
    }

    public static TextureRegion getClosedChestTextureRegion() {
        return closedChestTextureRegion;
    }

    public static TextureRegion getOpenChestTextureRegion() {
        return openChestTextureRegion;
    }

    public Texture getChestTexture() {
        return chestTexture;
    }

    public Texture getCollectiblesTexture() {
        return collectiblesTexture;
    }
    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public Animation<TextureRegion> getCharacterDownAnimationArmed() {
        return characterDownAnimationArmed;
    }

    public Animation<TextureRegion> getCharacterUpAnimationArmed() {
        return characterUpAnimationArmed;
    }

    public Animation<TextureRegion> getCharacterLeftAnimationArmed() {
        return characterLeftAnimationArmed;
    }

    public Animation<TextureRegion> getCharacterRightAnimationArmed() {
        return characterRightAnimationArmed;
    }
}
