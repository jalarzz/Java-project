package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
/**
 * Represents a lava obstacle in the game world.
 * This class manages the animation and rendering of the lava on the game screen.
 */
public class Lava extends MazeElement {
    private Animation<TextureRegion> animation;
    private float stateTime;

    /**
     * Constructs a lava element with a specified animation and position.
     *
     * @param animation The animation to be used for the lava's appearance.
     * @param x The x-coordinate of the lava's initial position.
     * @param y The y-coordinate of the lava's initial position.
     */
    public Lava(Animation<TextureRegion> animation, int x, int y) {
        super(animation.getKeyFrame(0), x, y,16,16);// Call to the parent class constructor with the first frame of the animation
        this.animation = animation;
        stateTime = 0f; // Initialize stateTime
    }

    /**
     * Updates the lava's animation frame based on the time elapsed. This method should be called in each frame
     * to ensure the animation is smooth and continuous.
     *
     * @param delta The time in seconds since the last update.
     */
    public void update(float delta) {
        stateTime += delta; // Update the stateTime
        setTexture(animation.getKeyFrame(stateTime, true));
    }
}
