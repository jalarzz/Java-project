package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Abstract base class for all collectible items in the game.
 */
public abstract class Collectible extends MazeElement {
    protected boolean collected = false;

    public Collectible(TextureRegion texture, float x, float y, float width, float height) {
        super(texture, x, y, width, height);
    }

    /**
     * Marks the collectible as collected and performs any necessary action
     * upon collection, such as applying effects.
     */
    public void collect() {
        this.collected = true;
        applyEffect();
    }

    /**
     * Applies the specific effect of the collectible.
     * Subclasses must override this method to implement their specific effects.
     */
    protected abstract void applyEffect();

    /**
     * Checks if the collectible has been collected.
     *
     * @return true if collected, false otherwise.
     */
    public boolean isCollected() {
        return collected;
    }
}
