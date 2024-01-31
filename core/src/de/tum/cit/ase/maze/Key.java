package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Represents a key object in the game world. The key can be collected by the player to unlock doors or achieve objectives.
 * This class handles the animation and rendering of the key on the game screen.
 */
public class Key extends MazeElement {
    private Animation<TextureRegion> animation;

    private float stateTime;

    /**
     * Constructs a key element with a specified animation and position.
     *
     * @param animation The animation to be used for the key's appearance.
     * @param x The x-coordinate of the key's initial position.
     * @param y The y-coordinate of the key's initial position.
     */
    public Key(Animation<TextureRegion> animation, int x, int y) {
        super(animation.getKeyFrame(0), x, y, 16, 16);
        this.animation = animation;
        stateTime = 0f; // Initialize stateTime
    }
    /**
     * Updates the key's animation frame based on the time elapsed. This method should be called in each frame.
     *
     * @param delta The time in seconds since the last update.
     */
    public void update(float delta) {
        stateTime += delta; // Update the stateTime
        setTexture(animation.getKeyFrame(stateTime, true));
    }
}
