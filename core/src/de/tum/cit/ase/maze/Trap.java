package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
/**
 * Represents a trap element within the maze. Traps are obstacles that can affect the player negatively.
 * This class uses an animation to visually represent the trap in the game.
 */
public class Trap extends MazeElement {
    private Animation<TextureRegion> animation;
    private float stateTime;
    /**
     * Constructs a Trap object with the specified animation and position.
     *
     * @param animation The animation to be used for the trap.
     * @param x The x-coordinate of the trap's position.
     * @param y The y-coordinate of the trap's position.
     */
    public Trap(Animation<TextureRegion> animation, int x, int y) {
        super(animation.getKeyFrame(0), x, y,16,16);
        this.animation = animation;
        stateTime = 0f; // Initialize stateTime
    }

    /**
     * Updates the trap's animation based on the elapsed time.
     *
     * @param delta The time in seconds since the last frame.
     */

    public void update(float delta) {
        stateTime += delta; // Update the stateTime
        setTexture(animation.getKeyFrame(stateTime, true));
    }
}