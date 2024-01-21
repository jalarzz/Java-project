package de.tum.cit.ase.maze;

/**
 * Interface for elements in the game that can move.
 */
public interface Movable {
    /**
     * Moves the element in a specified direction.
     * 
     * @param direction The direction to move.
     * @param maze The maze to consider for movement constraints.
     */
    void move(Direction direction, Maze maze);

    // Any other common methods for movable elements
}
