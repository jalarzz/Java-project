package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;

import static com.badlogic.gdx.math.MathUtils.degRad;
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
    private final float REACHED_NODE_TOLERANCE = 2.0f;
    private float speed = 40.0f;




    public Enemy(TextureRegion texture, int x, int y, Character player, Maze maze, Animation<TextureRegion>[] animations) {
        super(texture,x,y, TILE_SIZE, TILE_SIZE);
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
        float speed = TILE_SIZE * delta; // Adjust the speed if necessary
        float newX = x;
        float newY = y;


        // Calculate new position
        switch (direction) {
            case UP:
                newY += speed;
                break;
            case DOWN:
                newY -= speed;
                break;
            case LEFT:
                newX -= speed;
                break;
            case RIGHT:
                newX += speed;
                break;
            default:
                return; // Invalid direction

        }

        // Collision checking
        Rectangle tempBounds = new Rectangle(bounds);
        tempBounds.setPosition(newX, newY);
        int collisionType = maze.checkCollision(tempBounds, false);

// Check for collisions with walls and doors
        if (collisionType == 0) {
            handleWallCollision();
        } else {
            // Update the current position in the maze layout to floor
            maze.setElementAt((int) x / TILE_SIZE, (int) y / TILE_SIZE, -1);

            // Update position
            setPosition(newX, newY);

            // Update the new position in the maze layout to enemy
            maze.setElementAt((int) x / TILE_SIZE, (int) y / TILE_SIZE, 4);
        }
        if (!isCollisionWithWall(newX, newY, maze, currentDirection)) {
            setPosition(newX, newY);
        } else {
            handleWallCollision();
            chase(delta);
        }


}

    // New constructor without player parameter
    public Enemy(TextureRegion texture, int x, int y, Maze maze, Animation<TextureRegion>[] animations) {
        super(texture, x, y, TILE_SIZE, TILE_SIZE);
        this.maze = maze;
        this.animations = animations;
        this.stateTime = 0f;

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
                nodes[x][y] = new Node(x, y, layout[x][y] == -1);
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
        float speed = TILE_SIZE * delta;
        float projectedX = x, projectedY = y;

        switch (currentDirection) {
            case UP:
                projectedY += speed;
                break;
            case DOWN:
                projectedY -= speed;
                break;
            case LEFT:
                projectedX -= speed;
                break;
            case RIGHT:
                projectedX += speed;
                break;
        }

        // Check if the projected position collides with a wall or trap
        if (!isCollisionWithWall(projectedX, projectedY, maze,currentDirection)) {
            // If there is no collision, update the position
            setPosition(projectedX, projectedY);
        } else {
            // If there is a collision, choose a new direction
            chooseNewDirection(maze);
        }


    }  // Calculate the enemy's next position based on its current direction and speed


    private boolean isCollisionWithWall(float x, float y, Maze maze, Direction direction) {
        int gridX = (int) (x / TILE_SIZE);
        int gridY = (int) (y / TILE_SIZE);

        // Determine the next tile based on the direction of movement
        switch (direction) {
            case UP:
                gridY += 1;
                break;
            case DOWN:
                gridY -= 1;
                break;
            case LEFT:
                gridX -= 1;
                break;
            case RIGHT:
                gridX += 1;
                break;
        }

        // Check bounds
        if (gridX < 0 || gridY < 0 || gridX >= maze.getLayout().length || gridY >= maze.getLayout()[0].length) {
            return true; // Collision with a wall (out of bounds)
        }

        int tileType = maze.getElementAt(gridX, gridY);
        return tileType ==0; // Collision with a wall if the tile type is 0
    }

    private void chase(float delta) {
        float enemyGridX = x / TILE_SIZE;
        float enemyGridY = y / TILE_SIZE;
        float playerGridX = player.getX() / TILE_SIZE;
        float playerGridY = player.getY() / TILE_SIZE;

        // Check if the current path needs an update or if it's empty
        if (currentPath == null || currentPath.isEmpty() || pathIndex >= currentPath.size()) {
            // Calculate a new path
            currentPath = pathfinder.findPath(enemyGridX, enemyGridY, playerGridX, playerGridY);
            pathIndex = 0; // Reset pathIndex to start from the beginning
        }
followPath(delta );
    }



//    private void flee(float delta) {
//        // Check if the enemy has reached the player or the path needs an update
//        if (this.bounds.overlaps(player.getBounds())) {
//            // Determine the flee target coordinates
//            int fleeTargetX, fleeTargetY;
//
//            // Calculate differences in X and Y coordinates
//            int diffX = (int) player.getX() - (int) x;
//            int diffY = (int) player.getY() - (int) y;
//
//            // Determine flee direction (opposite to the player's direction)
//            if (diffX == 0) {
//                fleeTargetX = (int) x; // same X-coordinate if player is directly above or below
//            } else {
//                fleeTargetX = (int) ((int) x - 2 * Math.signum(diffX) * TILE_SIZE); // move in the opposite X direction
//            }
//
//            if (diffY == 0) {
//                fleeTargetY = (int) y; // same Y-coordinate if player is directly left or right
//            } else {
//                fleeTargetY = (int) ((int) y - 2 * Math.signum(diffY) * TILE_SIZE); // move in the opposite Y direction
//            }
//
//            // Adjust the target coordinates to ensure they are within the maze boundaries
//            fleeTargetX = Math.max(0, Math.min(fleeTargetX, maze.getLayout().length - 1));
//            fleeTargetY = Math.max(0, Math.min(fleeTargetY, maze.getLayout()[0].length - 1));
//
//            // Update the path
//            currentPath = pathfinder.findPath( x / TILE_SIZE, y / TILE_SIZE, fleeTargetX, fleeTargetY);
//            pathIndex = 0;
//        }
//
//        // Continue following the path
//        followPath(delta);
//    }

    /**
     * Chooses a new direction for the enemy that is valid within the specified grid bounds and avoids collisions.
     *

     * @param maze     The maze in which the enemy is moving.
     */
    private void chooseNewDirection(Maze maze) {
        Direction[] directions = Direction.values();
        Direction newDirection;
        boolean collision;

        do {
            newDirection = directions[random.nextInt(directions.length)];
            float projectedX = x, projectedY = y;
            float speed = TILE_SIZE;

            switch (newDirection) {
                case UP:    projectedY += speed; break;
                case DOWN:  projectedY -= speed; break;
                case LEFT:  projectedX -= speed; break;
                case RIGHT: projectedX += speed; break;
            }

            collision = isCollisionWithWall(projectedX, projectedY, maze,currentDirection);

            // Ensure that the new direction is not the same as the previous direction
            if (newDirection == currentDirection) {
                collision = true;
            }

        } while (collision);

        currentDirection = newDirection;
    }







    /**
     * Checks if the player character has entered a 3x3 grid area centered around the enemy.
     * The method calculates the bounds of this grid based on the enemy's current position
     * and checks if the player's bounding box overlaps with these bounds.
     *
     * @return true if the player character is within the grid, false otherwise.
     */
//    private boolean playerEntersGrid() {
//        // Grid dimensions for the rectangular area
//        int gridWidth = TILE_SIZE * 4;
//        int gridHeight = TILE_SIZE * 4;
//
//        // Calculate the top-left corner of the grid
//        float gridMinX = x - (float) gridWidth / 2 + (float) TILE_SIZE / 2;
//        float gridMinY = y - (float) gridHeight / 2 + (float) TILE_SIZE / 2;
//
//        // Create a rectangle representing the grid around the enemy
//        Rectangle gridBounds = new Rectangle(gridMinX, gridMinY, gridWidth, gridHeight);
//
//        // Check if the player's bounds overlap with the grid
//        boolean isInGrid = player.getBounds().overlaps(gridBounds);
//
//        // Define the range for close proximity within the grid
//        float proximityRadius = TILE_SIZE * 4; // for example, 2 tiles
//        boolean isClose = distanceSquared(x, y, player.getX(), player.getY()) <= proximityRadius * proximityRadius;
//
//        // Return true if the player is within the grid or very close to the enemy
//        return isInGrid || isClose;
//    }

    private boolean playerEntersGrid() {
        // Define the range within which the player is considered to be in the enemy's range
        float detectionRadius = TILE_SIZE * 4; // For example, 4 tiles

        // Calculate the squared distance between the enemy and the player
        float distanceSquared = distanceSquared(x, y, player.getX(), player.getY());

        // Check if the distance is less than or equal to the detection radius squared
        return distanceSquared <= detectionRadius * detectionRadius;
    }

    /**
     * Handles the behavior when the enemy collides with a wall.
     * The enemy will choose a new direction to move in.
     */
    private void handleWallCollision() {
        if (currentState == EnemyState.PATROLLING) {
            // When patrolling, randomly choose a new direction
            Direction newDirection;
            do {
                newDirection = Direction.values()[random.nextInt(Direction.values().length)];
            } while (newDirection == currentDirection); // Ensure it's a different direction
            currentDirection = newDirection;
        } else if (currentState == EnemyState.CHASING) {
            // When chasing, prioritize the direction that gets closer to the player
            Direction playerDirectionX = (player.getX() > x) ? Direction.RIGHT : Direction.LEFT;
            Direction playerDirectionY = (player.getY() > y) ? Direction.UP : Direction.DOWN;

            // Randomly choose between X and Y directions with bias towards player's direction
            if (random.nextFloat() < 0.7f) {
                currentDirection = playerDirectionX;
            } else {
                currentDirection = playerDirectionY;
            }
//        } else if (currentState == EnemyState.FLEEING) {
//            // When fleeing, move away from the player
//            Direction playerDirectionX = (player.getX() > x) ? Direction.LEFT : Direction.RIGHT;
//            Direction playerDirectionY = (player.getY() > y) ? Direction.DOWN : Direction.UP;

            // Randomly choose between X and Y directions with bias away from player's direction
            if (random.nextFloat() < 0.7f) {
                currentDirection = playerDirectionX;
            } else {
                currentDirection = playerDirectionY;
            }
        }
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
            // Log the current state of the enemy for debugging
            Gdx.app.log("Enemy State", "Current state: " + currentState.toString());


            // Log the state change for debugging
            if (playerEntersGrid() && currentState != EnemyState.CHASING) {
                Gdx.app.log("State Change", "Switching to CHASING state");
                currentState = EnemyState.CHASING;
                currentPath = null;
                pathIndex = 0;
            } else if (!playerEntersGrid() && currentState != EnemyState.PATROLLING) {
                Gdx.app.log("State Change", "Switching to PATROLLING state");
                currentState = EnemyState.PATROLLING;
            }
            switch (currentState) {
                case PATROLLING:
                    patrol(delta, maze);
                    break;
                case CHASING:
                    chase(delta);
                    break;
        }}





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
        // Check if there is no path or the enemy has reached the end of the path.
        if (currentPath == null || pathIndex >= currentPath.size()) {
            return true;
        }

        // Check if the player has moved a significant distance since the path was calculated.
        Node lastTargetNode = currentPath.get(currentPath.size() - 1);
        float playerGridX = player.getX() / TILE_SIZE;
        float playerGridY = player.getY() / TILE_SIZE;
        if (distanceSquared(playerGridX, playerGridY, lastTargetNode.x, lastTargetNode.y) > TILE_SIZE * TILE_SIZE) {
            return true;
        }

        // No need to update the path
        return false;
    }

    //    private boolean hasPlayerMovedSignificantly() {
//        Node lastTargetNode = currentPath.get(currentPath.size() - 1);
//        float playerGridX = player.getX() / TILE_SIZE;
//        float playerGridY = player.getY() / TILE_SIZE;
//        return distanceSquared(playerGridX, playerGridY, lastTargetNode.x, lastTargetNode.y) > TILE_SIZE * TILE_SIZE;
//    }

    private float distanceSquared(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return dx * dx + dy * dy;
    }

    private void followPath(float delta) {
        if (currentPath != null && pathIndex < currentPath.size()) {
            Node nextNode = currentPath.get(pathIndex);
            int targetX = nextNode.x * TILE_SIZE;
            int targetY = nextNode.y * TILE_SIZE;

            moveTowards(targetX, targetY, delta);
            if (reachedNode(targetX, targetY)) {
                pathIndex++;
            }
        }
    }



    private boolean reachedNode(float targetX, float targetY) {
        return distanceSquared(x, y, targetX, targetY) < REACHED_NODE_TOLERANCE;
    }
    // Modify the moveTowards method to accept 'delta' as a parameter
    private void moveTowards(float targetX, float targetY, float delta) {
        float diffX = targetX - x;
        float diffY = targetY - y;
        float magnitude = (float) Math.sqrt(diffX * diffX + diffY * diffY);

        if (magnitude > 0) {
            float moveX = speed * delta * (diffX / magnitude);
            float moveY = speed * delta * (diffY / magnitude);

            // Ensure the enemy does not overshoot the target
            if (Math.abs(moveX) > Math.abs(diffX)) moveX = diffX;
            if (Math.abs(moveY) > Math.abs(diffY)) moveY = diffY;

            x += moveX;
            y += moveY;

setPosition(x,y);
            // Calculate the direction based on the sign of diffX and diffY
            if (Math.abs(diffX) > Math.abs(diffY)) {
                currentDirection = (diffX > 0) ? Direction.RIGHT : Direction.LEFT;
            } else {
                currentDirection = (diffY > 0) ? Direction.UP : Direction.DOWN;
            }
        }
    }

}