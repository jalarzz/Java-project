package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import static com.badlogic.gdx.math.MathUtils.random;

public class Enemy extends MazeElement implements Movable {
    private EnemyState currentState; // Current state of the enemy in the FSM.
    private static final int TILE_SIZE = 16; // each tile is 16x16
    private Direction currentDirection; // current direction
    private Character player;
    private Maze maze;
    private Animation<TextureRegion>[] animations; // Animations for different directions
    private float stateTime; // Time since the animation started

    public Enemy(TextureRegion texture, int x, int y, Character player, Maze maze, Animation<TextureRegion>[] animations) {
        super(texture, x, y, TILE_SIZE, TILE_SIZE);
        this.currentState = EnemyState.PATROLLING;
        this.currentDirection = Direction.values()[random.nextInt(Direction.values().length)]; // Random initial direction
        this.player = player; // Reference to the player character
        this.maze = maze; // Reference to the maze
        this.animations = animations;
        this.stateTime = 0f;
    }

    @Override
    public void move(Direction direction, Maze maze, float delta) {
        float newX = x, newY = y;
        float speed = TILE_SIZE * delta; // Adjust the speed if necessary

        // Calculate new position
        switch (direction) {
            case UP:    newY += speed; break;
            case DOWN:  newY -= speed; break;
            case LEFT:  newX -= speed; break;
            case RIGHT: newX += speed; break;
            default:    return; // Invalid direction
        }

        // Collision checking
        Rectangle tempBounds = new Rectangle(bounds);
        tempBounds.setPosition(newX, newY);
        int collisionType = maze.checkCollision(tempBounds,false);
        if (collisionType == 0) { // Wall collision
            handleWallCollision();
        } else {
            // Update the current position in the maze layout to floor
            maze.setElementAt((int) x/TILE_SIZE, (int) y/TILE_SIZE, -1);

            // Update position
            setPosition(newX, newY);

            // Update the new position in the maze layout to enemy
            maze.setElementAt((int)x / TILE_SIZE, (int)y / TILE_SIZE, 4);
        }
        Gdx.app.debug("Enemy", "Moved to x=" + x + ", y=" + y);
    }
    // New constructor without player parameter
    public Enemy(TextureRegion texture, int x, int y, Maze maze, Animation<TextureRegion>[] animations) {
        super(texture, x, y, TILE_SIZE, TILE_SIZE);
        this.maze = maze;
        this.animations = animations;
        this.stateTime = 0f;
        // player is not set yet, will be set later
    }

    // Method to set the player character
    public void setPlayer(Character player) {
        if (player == null) {
            throw new IllegalArgumentException("Player character cannot be null");
        }
        this.player = player;
    }

    /**
     * Handles the patrolling behavior of the enemy within a 3x3 grid.
     * The enemy moves randomly within this grid, avoiding walls and traps.
     * The enemy changes state if the player enters its patrolling grid.
     *
     * @param delta The time passed since the last frame.
     * @param maze  The maze in which the enemy is moving.
     */
    private void patrol(float delta, Maze maze) {
        // Define the 3x3 grid bounds around the enemy
        int gridMinX = (int)bounds.x - TILE_SIZE;
        int gridMaxX = (int)bounds.x + TILE_SIZE;
        int gridMinY = (int)bounds.y - TILE_SIZE;
        int gridMaxY = (int)bounds.y + TILE_SIZE;

        // Check if the player enters the grid
        if (playerEntersGrid(gridMinX, gridMaxX, gridMinY, gridMaxY)) {
            if (player.isArmed()) {
                currentState = EnemyState.FLEEING;
            } else {
                currentState = EnemyState.CHASING;
            }
            return;
        }

        // Decide whether to change direction based on the current position and direction
        if (shouldChangeDirection(gridMinX, gridMaxX, gridMinY, gridMaxY, maze)) {
            chooseNewDirection(gridMinX, gridMaxX, gridMinY, gridMaxY, maze);
        }

        // Move in the current direction
        move(currentDirection, maze, delta);
        Gdx.app.debug("Enemy", "Patrolling. Current direction: " + currentDirection);
    }
    /**
     * Chooses a new direction for the enemy that is valid within the specified grid bounds and avoids collisions.
     *
     * @param gridMinX The minimum X-coordinate of the grid.
     * @param gridMaxX The maximum X-coordinate of the grid.
     * @param gridMinY The minimum Y-coordinate of the grid.
     * @param gridMaxY The maximum Y-coordinate of the grid.
     * @param maze     The maze in which the enemy is moving.
     */
    private void chooseNewDirection(int gridMinX, int gridMaxX, int gridMinY, int gridMaxY, Maze maze) {
        Direction[] directions = Direction.values();
        Direction newDirection = null;
        Rectangle projectedBounds = new Rectangle(bounds);

        for (int attempt = 0; attempt < directions.length; attempt++) {
            newDirection = directions[random.nextInt(directions.length)];
            float speed = TILE_SIZE;

            projectedBounds.setPosition(bounds.x, bounds.y); // Reset to current position
            switch (newDirection) {
                case UP:    projectedBounds.y += speed; break;
                case DOWN:  projectedBounds.y -= speed; break;
                case LEFT:  projectedBounds.x -= speed; break;
                case RIGHT: projectedBounds.x += speed; break;
            }

            // Check if new direction is valid
            if (projectedBounds.x >= gridMinX && projectedBounds.x + projectedBounds.width <= gridMaxX &&
                    projectedBounds.y >= gridMinY && projectedBounds.y + projectedBounds.height <= gridMaxY &&
                    maze.checkCollision(projectedBounds, false) != 0) {
                break; // Found a valid new direction
            }
        }

        currentDirection = (newDirection != null) ? newDirection : currentDirection;
        // If no valid direction found, keep the current direction
    }

    /**
     * Checks if the player character has entered the enemy's patrolling grid.
     *
     * @param gridMinX The minimum X-coordinate of the grid.
     * @param gridMaxX The maximum X-coordinate of the grid.
     * @param gridMinY The minimum Y-coordinate of the grid.
     * @param gridMaxY The maximum Y-coordinate of the grid.
     * @return true if the player character is within the grid, false otherwise.
     */
    private boolean playerEntersGrid(int gridMinX, int gridMaxX, int gridMinY, int gridMaxY) {
        Rectangle playerBounds = player.getBounds(); // Method to get player's bounding box

        // Check if any part of the player's bounds is within the grid
        return playerBounds.overlaps(new Rectangle(gridMinX, gridMinY, gridMaxX - gridMinX, gridMaxY - gridMinY));
    }

    /**
     * Determines whether the enemy should change its direction based on its position relative
     * to the grid bounds and potential collisions.
     *
     * @param gridMinX Minimum X-coordinate of the grid.
     * @param gridMaxX Maximum X-coordinate of the grid.
     * @param gridMinY Minimum Y-coordinate of the grid.
     * @param gridMaxY Maximum Y-coordinate of the grid.
     * @param maze The maze in which the enemy is moving.
     * @return true if the enemy should change direction, false otherwise.
     */
    private boolean shouldChangeDirection(int gridMinX, int gridMaxX, int gridMinY, int gridMaxY, Maze maze) {
        // Calculate the enemy's next position based on its current direction and speed
        float speed = TILE_SIZE;
        Rectangle projectedBounds = new Rectangle(bounds);

        switch (currentDirection) {
            case UP:    projectedBounds.y += speed; break;
            case DOWN:  projectedBounds.y -= speed; break;
            case LEFT:  projectedBounds.x -= speed; break;
            case RIGHT: projectedBounds.x += speed; break;
        }

        // Check if the projected position is outside the grid bounds
        if (projectedBounds.x < gridMinX || projectedBounds.x + projectedBounds.width > gridMaxX ||
                projectedBounds.y < gridMinY || projectedBounds.y + projectedBounds.height > gridMaxY) {
            return true;
        }

        // Check for collision with walls
        return maze.checkCollision(projectedBounds, false) == 0;
    }

    /**
     * Handles the behavior when the enemy collides with a wall.
     * The enemy will choose a new direction to move in.
     */
    private void handleWallCollision() {
        // Randomly choose a new direction
        // You can make this more sophisticated based on the current state
        Direction[] directions = Direction.values();
        Direction newDirection = directions[random.nextInt(directions.length)];
        this.currentDirection = newDirection; // Update the direction
    }
    //TODO add setPosition to Movable interface
    /**
     * Updates the enemy's position and its bounding box.
     *
     * @param newX The new X-coordinate of the enemy.
     * @param newY The new Y-coordinate of the enemy.
     */
    public void setPosition(float newX, float newY) {
        this.x = newX;
        this.y = newY;
        this.bounds.setPosition(newX, newY);
    }
    /**
     * Updates the state of the enemy.
     *
     * @param delta Time since last frame.
     */

    public void update(float delta) {
        try {
            stateTime += delta;

            Gdx.app.debug("Enemy", "Updating enemy. Current state: " + currentState);

            switch (currentState) {
                case PATROLLING:
                    patrol(delta, maze);
                    Gdx.app.debug("Enemy", "Patrolling state. X: " + x + ", Y: " + y + ", Direction: " + currentDirection);
                    break;
                case CHASING:
                    // Implement chasing behavior
                    Gdx.app.debug("Enemy", "Chasing state. Player X: " + player.getX() + ", Player Y: " + player.getY());
                    break;
                case FLEEING:
                    // Implement fleeing behavior
                    Gdx.app.debug("Enemy", "Fleeing state. Player X: " + player.getX() + ", Player Y: " + player.getY());
                    break;
            }
        } catch (Exception e) {
            Gdx.app.error("Enemy", "Error in update method: " + e.getMessage(), e);
        }
    }


    /**
     * Draws the enemy at its current position using the appropriate animation frame.
     *
     * @param batch The SpriteBatch used for drawing.
     */
    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame = animations[currentDirection.ordinal()].getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x, y, TILE_SIZE, TILE_SIZE);
    }



}
