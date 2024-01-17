package de.tum.cit.ase.maze;
import com.badlogic.gdx.files.FileHandle;
import java.io.IOException;
import java.util.Properties;

/**
 * The Maze class is responsible for loading, parsing, and storing the layout of the maze.
 * It reads a maze configuration from a .properties file and represents it as a 2D grid.
 */
public class Maze {
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
                layout[x][layout[0].length - y - 1] = type;
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

}

