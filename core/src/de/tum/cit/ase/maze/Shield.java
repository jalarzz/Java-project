package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Represents a shield collectible in the game. When collected, it grants the player temporary invincibility.
 */
public class Shield extends Collectible {
    Character player;
    private HUD hud;

    /**
     * Constructs a Shield collectible with a specified texture, position, and effects.
     *
     * @param texture The texture for the shield collectible.
     * @param x The x-coordinate of the shield's position.
     * @param y The y-coordinate of the shield's position.
     * @param hud The HUD instance to display messages.
     * @param player The player character who can collect the shield.
     */
    public Shield(TextureRegion texture, float x, float y, HUD hud, Character player) {
        super(texture, x, y, 16, 16);
        this.hud = hud;
        this.player = player;
    }
    /**
     * Applies the shield's effect by granting temporary invincibility to the player.
     * This method is called automatically when the player collects the shield.
     */
    @Override
    protected void applyEffect() {

        player.extendInvulnerability(20.0f);
        hud.showMessage("You are invincible!");
    }
}