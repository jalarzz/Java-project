package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;

import static com.badlogic.gdx.math.MathUtils.random;

public class Enemy extends MazeElement implements Movable {
    private EnemyState currentState; // Current state of the enemy in the FSM.
    private static final int TILE_SIZE = 16; // each tile is 16x16
    private Direction currentDirection; // current direction
    private Character player;
    private Maze maze;
    private Animation<TextureRegion>[] animations; // Animations for different directions
    private float stateTime; // Time since the animation started
    private AStar pathfinder;
    private List<Node> currentPath;
    private int pathIndex;
    private final float UPDATE_PATH_THRESHOLD = 64.0f;
    private final float REACHED_NODE_TOLERANCE = 4.0f;
    private float speed = 100.0f;
    private float someThreshold = 100.0f;


    public Enemy(TextureRegion texture, int x, int y, Character player, Maze maze, Animation<TextureRegion>[] animations) {
        super(texture, x, y, TILE_SIZE, TILE_SIZE);
        this.currentState = EnemyState.PATROLLING;
        this.currentDirection = Direction.values()[random.nextInt(Direction.values().length)]; // Random initial direction
        this.player = player; // Reference to the player character
        this.maze = maze; // Reference to the maze
        this.animations = animations;
        this.stateTime = 0f;
        this.pathfinder = new AStar(convertToNodes(maze.getLayout()));
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
        int collisionType = maze.checkCollision(tempBounds, false);

        // Check for collisions with walls and doors
        if (collisionType == 0 || collisionType == 1 || collisionType == 2 || collisionType == 3) {
            handleWallCollision(); // Handle wall collisions (e.g., change direction)
        } else {
            // Update the current position in the maze layout to floor
            maze.setElementAt((int) x / TILE_SIZE, (int) y / TILE_SIZE, -1);

            // Update position
            setPosition(newX, newY);

            // Update the new position in the maze layout to enemy
            maze.setElementAt((int) x / TILE_SIZE, (int) y / TILE_SIZE, 4);
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
    private Node[][] convertToNodes(int[][] layout) {
        Node[][] nodes = new Node[layout.length][layout[0].length];

        for (int x = 0; x < layout.length; x++) {
            for (int y = 0; y < layout[x].length; y++) {
                // Assuming that 0 represents a walkable path in your maze
                nodes[x][y] = new Node(x, y, layout[x][y] == 0);
            }
        }

        return nodes;
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
        move(currentDirection, maze, delta);
    }
    private void chase(float delta) {
        if (pathNeedsUpdate()) {
            // Recalculate the path towards the player
            currentPath = pathfinder.findPath((int)x, (int)y, (int)player.getX(), (int)player.getY());
            pathIndex = 0;
        }
        followPath();
    }

    private void flee(float delta) {
        if (pathNeedsUpdate()) {
            // Determine the flee target coordinates
            int fleeTargetX, fleeTargetY;

            // Calculate differences in X and Y coordinates
            int diffX = (int)player.getX() - (int)x;
            int diffY = (int)player.getY() - (int)y;

            // Determine flee direction (opposite to the player's direction)
            if (diffX == 0) {
                fleeTargetX = (int)x; // same X-coordinate if player is directly above or below
            } else {
                fleeTargetX = (int) ((int)x - 2 * Math.signum(diffX)); // move in the opposite X direction
            }

            if (diffY == 0) {
                fleeTargetY = (int)y; // same Y-coordinate if player is directly left or right
            } else {
                fleeTargetY = (int) ((int)y - 2 * Math.signum(diffY)); // move in the opposite Y direction
            }

            // Adjust the target coordinates to ensure they are within the maze boundaries
            fleeTargetX = Math.max(0, Math.min(fleeTargetX, maze.getLayout().length - 1));
            fleeTargetY = Math.max(0, Math.min(fleeTargetY, maze.getLayout()[0].length - 1));

            currentPath = pathfinder.findPath((int)x, (int)y, fleeTargetX, fleeTargetY);
            pathIndex = 0;
        }
        followPath();
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

        }

        if (newDirection != null) {
            currentDirection = newDirection; // Update the direction
            stateTime = 0f; // Reset animation state time
        }
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
        currentDirection = Direction.values()[random.nextInt(Direction.values().length)];
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
        stateTime += delta;

        switch (currentState) {
            case PATROLLING:
                patrol(delta, maze);
                break;
            case CHASING:
                chase(delta);
                break;
            case FLEEING:
                flee(delta);
                break;
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
    private boolean pathNeedsUpdate() {
        // Recalculate path if it's null or completed
        return currentPath == null || pathIndex >= currentPath.size() ||
                distanceSquared(x, y, player.getX(), player.getY()) > someThreshold; // Recalculate if player moved significantly
    }
    private void followPath() {
        if (currentPath != null && pathIndex < currentPath.size()) {
            Node nextNode = currentPath.get(pathIndex);
            moveTowards(nextNode.x, nextNode.y);
            if (reachedNode(nextNode)) {
                pathIndex++;
            }
        }
    }
    private void moveTowards(int targetX, int targetY) {
        float diffX = targetX - x;
        float diffY = targetY - y;
        float magnitude = (float) Math.sqrt(diffX * diffX + diffY * diffY);

        // Calculate movement without multiplying by delta since it's already in your chase and flee methods
        float moveX = speed * (diffX / magnitude);
        float moveY = speed * (diffY / magnitude);

        x += moveX;
        y += moveY;

        if (Math.abs(moveX) > Math.abs(moveY)) {
            currentDirection = moveX > 0 ? Direction.RIGHT : Direction.LEFT;
        } else {
            currentDirection = moveY > 0 ? Direction.UP : Direction.DOWN;
        }
    }
    private boolean reachedNode(Node node) {
        return distanceSquared(x, y, node.x * TILE_SIZE, node.y * TILE_SIZE) < REACHED_NODE_TOLERANCE;
    }

    private float distanceSquared(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return dx * dx + dy * dy;
    }

}
