package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
/**
 * Represents a floor element in the maze.
 * Floor elements serve as the navigable area of the maze where characters can move. This class
 * extends MazeElement and is used to visually represent the floor on the maze map.
 */

public class Floor extends MazeElement {
    /**
     * Constructs a new Floor element with the specified texture and position.
     *
     * @param texture The texture for the floor element.
     * @param x       The x-coordinate of the floor's position.
     * @param y       The y-coordinate of the floor's position.
     */
    public Floor(TextureRegion texture, int x, int y) {
        super(texture, x, y,16,16);
    }
}
