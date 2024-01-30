package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.Random;

/**
 * Represents a chest in the game that can be opened to reveal a random collectible.
 */
public class Chest extends MazeElement {
    private boolean opened = false;
    private MazeRunnerGame game; // Reference to the MazeRunnerGame instance
    private GameScreen gameScreen;
    private TextureRegion openTexture; // Texture to use when the chest is open

    /**
     * Constructs a Chest object.
     *
     * @param texture     The texture for the closed chest.
     * @param openTexture The texture for the opened chest.
     * @param x           The x-coordinate of the chest.
     * @param y           The y-coordinate of the chest.
     * @param game        The MazeRunnerGame instance for accessing global resources.
     * @param gameScreen  The GameScreen instance for accessing game elements and logic.
     */
    public Chest(TextureRegion texture, TextureRegion openTexture, float x, float y, MazeRunnerGame game, GameScreen gameScreen) {
        super(texture, x, y, 16, 16); // Assuming 16x16 is the size of the chest
        this.game = game;
        this.gameScreen = gameScreen;
        this.openTexture = openTexture;
    }

    /**
     * Opens the chest if it hasn't been opened yet and randomly selects a collectible to drop.
     */
    public void open() {
        if (!opened) {
            opened = true;
            dropCollectible();
        }
    }

    /**
     * Randomly generates a collectible and adds it to the game screen.
     */
    private void dropCollectible() {
        Random rand = new Random();
        Collectible collectible;

        switch (rand.nextInt(3)) {
            case 0:
                collectible = new Sword(game.getSwordTexture(),this.x,this.y-16,gameScreen.getPlayerCharacter(),gameScreen.getHud());
                break;
            case 1:
                collectible = new Sword(game.getSwordTexture(),this.x,this.y-16,gameScreen.getPlayerCharacter(),gameScreen.getHud());
                //collectible = new Life(game.getLifeTexture(), this.x, this.y-16, gameScreen.getPlayerCharacter(),gameScreen.getHud());
                break;
            case 2:
                collectible = new Sword(game.getSwordTexture(),this.x,this.y-16,gameScreen.getPlayerCharacter(),gameScreen.getHud());
                //collectible = new Shield(game.getShieldTexture(), this.x, this.y-16, gameScreen.getHud(), gameScreen.getPlayerCharacter());
                break;
            default:
                return; // In case of an unexpected value
        }

        gameScreen.addCollectible(collectible);
    }

    /**
     * Determines if the chest has been opened.
     *
     * @return True if the chest is opened, false otherwise.
     */
    public boolean isOpened() {
        return opened;
    }

    /**
     * Draws the chest on the game screen, using the open texture if the chest has been opened.
     *
     * @param batch The SpriteBatch used for drawing.
     */
    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion currentTexture = opened ? openTexture : texture;
        batch.draw(currentTexture, x, y, width, height);
    }
}
