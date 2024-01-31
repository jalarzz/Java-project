package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Represents the entry point in the maze.
 * This class is responsible for marking the starting location of the player within the maze.
 * It extends MazeElement, making use of a specific texture to visually indicate the entry point
 * among other elements in the maze.
 */
public class EntryPoint extends MazeElement {
    /**
     * Constructs a new EntryPoint element with the specified texture and position.
     *
     * @param texture The texture for the entry point element.
     * @param x       The x-coordinate of the entry point's position.
     * @param y       The y-coordinate of the entry point's position.
     */
    public EntryPoint(TextureRegion texture, int x, int y) {
        super(texture, x, y,16,16);
    }
}
