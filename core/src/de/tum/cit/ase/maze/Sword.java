package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
/**
 * Represents a sword collectible in the game. When collected, it grants the player the ability to slay enemies.
 */
public class Sword extends Collectible {
    private Character character;
    private HUD hud;

    /**
     * Constructs a Sword collectible with a specified texture, position, and effects on the player.
     *
     * @param texture The texture for the sword collectible.
     * @param x The x-coordinate of the sword's position.
     * @param y The y-coordinate of the sword's position.
     * @param character The player character who can collect and use the sword.
     * @param hud The HUD instance to display messages regarding the sword collection.
     */
    public Sword(TextureRegion texture, float x, float y, Character character, HUD hud) {
        super(texture, x, y, 16, 16);
        this.character = character;
        this.hud = hud;
    }
    /**
     * Applies the sword's effect by enabling the player character to slay enemies automatically.
     * This method is called automatically when the player collects the sword.
     */
    @Override
    protected void applyEffect() {
        character.setArmed(true);
        hud.showMessage("You can slay now!");
        System.out.println("Sword collected: Player can now slay enemies automatically.");
    }
}