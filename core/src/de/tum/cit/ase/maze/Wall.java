package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
/**
 * Represents a wall element in the maze.
 * Walls are static obstacles that characters cannot pass through. This class
 * extends MazeElement and is used to visually represent walls on the maze map.
 */

public class Wall extends MazeElement {
    /**
     * Constructs a new Wall element with the specified texture and position.
     *
     * @param texture The texture for the wall element.
     * @param x       The x-coordinate of the wall's position.
     * @param y       The y-coordinate of the wall's position.
     */
    public Wall(TextureRegion texture, int x, int y) {
        super(texture, x, y,16,16);
    }
}
