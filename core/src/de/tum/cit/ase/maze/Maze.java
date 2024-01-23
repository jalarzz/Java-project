package de.tum.cit.ase.maze;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import com.badlogic.gdx.math.Rectangle;


/**
 * The Maze class is responsible for loading, parsing, and storing the layout of the maze.
 * It reads a maze configuration from a .properties file and represents it as a 2D grid.
 */
public class Maze {
    private static final float TILE_SIZE = 16;
    private int[][] layout;
    private boolean isValidMaze = false;

    /**
     * Constructor for the Maze class.
     * Loads and parses the maze configuration from the given file.
     *
     * @param fileHandle The handle to the .properties file containing the maze layout.
     */
    public Maze(FileHandle fileHandle) {
        if (fileHandle == null || !fileHandle.exists()) {
            System.err.println("Maze file is null or does not exist.");
            return;
        }

        Properties properties = new Properties();
        try {
            properties.load(fileHandle.read());
            int[] size = determineMazeSize(properties);
            if (size[0] > 0 && size[1] > 0) {
                layout = new int[size[0]][size[1]];
                // Initialize the layout with floor value (-1)
                for (int i = 0; i < layout.length; i++) {
                    Arrays.fill(layout[i], -1); // Fill row with floor value
                }
                parseProperties(properties);
                isValidMaze = true;
            } else {
                System.err.println("Invalid maze size determined from the file.");
            }
        } catch (IOException e) {
            System.err.println("Error reading maze file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in maze file: " + e.getMessage());
        }
    }

    /**
     * Determines the size of the maze based on the properties file.
     *
     * @param properties The properties object containing the maze data.
     * @return An array containing the width and height of the maze.
     */
    private int[] determineMazeSize(Properties properties) {
        int maxWidth = 0;
        int maxHeight = 0;
        for (String key : properties.stringPropertyNames()) {
            try {
                String[] coords = key.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                if (x > maxWidth) maxWidth = x;
                if (y > maxHeight) maxHeight = y;
            } catch (NumberFormatException e) {
                System.err.println("Invalid coordinate format: " + key);
            }
        }
        return new int[]{maxWidth + 1, maxHeight + 1};
    }

    /**
     * Parses the properties file and fills the maze layout.
     *
     * @param properties The properties object containing the maze data.
     */
    private void parseProperties(Properties properties) {
        properties.forEach((key, value) -> {
            try {
                String[] coords = ((String) key).split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                int type = Integer.parseInt((String) value);
                layout[x][y] = type;
            } catch (NumberFormatException e) {
                System.err.println("Invalid format in maze file for key: " + key);
            }
        });
    }

    /**
     * Gets the maze layout as a 2D integer array.
     *
     * @return The 2D array representing the maze.
     */
    public int[][] getLayout() {
        return layout;
    }

    /**
     * Checks if the maze is valid and properly loaded.
     *
     * @return True if the maze is valid, false otherwise.
     */
    public boolean isValidMaze() {
        return isValidMaze;
    }

    /**
     * Checks if a move to the specified coordinates is valid.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return True if the move is valid, false otherwise.
     */
    /*public boolean isValidMove(int x, int y) {
        // Check bounds
        if (x < 0 || y < 0 || x >= layout.length * 16 || y >= layout[0].length * 16) {
            return false;
        }
        // Check if the location is not a wall (wall is typically represented by 0)
        return layout[(int)x/16][(int)y/16] != 0;
    }*/

    /**
     * Checks if a move to the specified float coordinates is valid.
     * Now also checks for interaction with other elements using float values.
     *
     * @param x The x-coordinate (float).
     * @param y The y-coordinate (float).
     * @return A code indicating the type of interaction, or -1 for a valid move.
     */
    /*public int checkMove(float x, float y) {
        // Convert float to grid coordinates
        int gridX = (int)(x / 16);
        int gridY = (int)(y / 16);

        // Check bounds
        if (gridX < 0 || gridY < 0 || gridX >= layout.length || gridY >= layout[0].length) {
            return 0; // Indicate a wall collision
        }

        return layout[gridX][gridY]; // Return the tile type
    }*/

    /**
     * Checks for collisions between the character and specific tiles in the maze based on the character's intended movement.
     * It uses the character's bounding box and checks if it overlaps with tiles that represent walls, doors, traps, enemies, etc.
     *
     * @param characterBounds The bounding box of the character after movement.
     * @param newX The x-coordinate the character is trying to move to.
     * @param newY The y-coordinate the character is trying to move to.
     * @param hasKey A boolean indicating if the character has a key (for locked doors).
     * @return The type of element the character collides with, or -1 if there is no collision.
     */
    public int checkCollision(Rectangle characterBounds, float newX, float newY, boolean hasKey) {
        int gridX = (int)(newX / TILE_SIZE);
        int gridY = (int)(newY / TILE_SIZE);

        if (gridX < 0 || gridY < 0 || gridX >= layout.length || gridY >= layout[0].length) {
            return 0; // Out of bounds
        }

        int tileType = layout[gridX][gridY];
        switch (tileType) {
            case 0: // Wall
                return 0;
            case 2: // Exit
                return hasKey ? -1 : 2; // If character has a key, no collision; otherwise, collision with locked door
            case 3: // Trap
                return 3;
            case 4: // Enemy
                return 4;
            case 5: // Key
                return 5;
            // Add additional cases as needed for other tile types
        }

        return -1; // No collision with any special element
    }


    /**
     * Gets the type of element at the specified coordinates.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return The type of element at the coordinates, or -1 if out of bounds.
     */
    public int getElementAt(int x, int y) {
        // Check bounds
        if (x < 0 || y < 0 || x >= layout.length || y >= layout[0].length) {
            return -1; // Indicating out of bounds or no element
        }
        return layout[x][y];
    }

}

