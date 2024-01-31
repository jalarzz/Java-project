package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Represents a life collectible in the game. When collected by the player, it increases the player's lives.
 * This class is responsible for the life collectible's behavior and effects when collected.
 */
public class Life extends Collectible {
    private Character playerCharacter;
    private HUD hud;
    /**
     * Constructs a life collectible with specified texture, position, player character, and HUD.
     *
     * @param texture Texture for the life collectible's appearance.
     * @param x The x-coordinate of the life's initial position.
     * @param y The y-coordinate of the life's initial position.
     * @param playerCharacter The player character that can collect this life.
     * @param hud The game's HUD to display messages when the life is collected.
     */
    public Life(TextureRegion texture, float x, float y, Character playerCharacter, HUD hud) {
        super(texture, x, y, 16, 16);
        this.playerCharacter = playerCharacter;
        this.hud = hud;
    }
    /**
     * Applies the effect of collecting a life. Increases the player's lives by 1 if the player has less than 5 lives.
     * Displays a message in the HUD based on whether the life was successfully added or not.
     */
    @Override
    protected void applyEffect() {
        //increase player's lives
        if(playerCharacter.getLives()<5) {
            playerCharacter.setLives(playerCharacter.getLives() + 1);
            hud.showMessage("+1 Life. Nice!");
        }
        else {
            hud.showMessage("No more than 5 lives!");
        }
        System.out.println("Life collected: Player's lives increased.");
    }
}