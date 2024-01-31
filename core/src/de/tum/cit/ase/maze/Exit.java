package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
/**
 * Represents the exit point in the maze.
 * This class is used to create an exit element within the maze, marking the location
 * players aim to reach to complete the level. It extends the MazeElement class,
 * utilizing a specific texture (a golden gate lol) to visually differentiate the exit from other elements in the maze.
 */
public class Exit extends MazeElement {

    /**
     * Constructs a new Exit element with the specified texture and position.
     *
     * @param texture The texture for the exit element.
     * @param x       The x-coordinate of the exit's position.
     * @param y       The y-coordinate of the exit's position.
     */
    public Exit(TextureRegion texture, int x, int y) {
        super(texture, x, y,16,16);
    }
}
