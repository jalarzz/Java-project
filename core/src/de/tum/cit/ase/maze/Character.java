package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Character class represents the player character in the game.
 * It includes movement and animation handling based on direction.
 */
public class Character extends MazeElement implements Movable {
    private int lives;
    private boolean hasKey;
    private Animation<TextureRegion>[] animations;
    private float stateTime;
    private Direction currentDirection;


    /**
     * Constructor for the Character class.
     *
     * @param animations Array of animations for each direction.
     * @param x Initial x-coordinate of the character.
     * @param y Initial y-coordinate of the character.
     * @param lives Number of lives the character starts with.
     */
    public Character(Animation<TextureRegion>[] animations, int x, int y, int lives) {
        super(null, x, y); // texture is set to null initially
        this.animations = animations;
        this.lives = lives;
        this.hasKey = false;
        this.stateTime = 0f;
        this.currentDirection = null; // Default direction
    }

    /**
     * Moves the character in the given direction if the move is valid.
     *
     * @param direction The direction to move the character.
     * @param maze      The maze to check for valid moves.
     */
    @Override
    public void move(Direction direction, Maze maze) {
        int newX = x, newY = y;
        switch (direction) {
            case UP:    newY++; break;
            case DOWN:  newY--; break;
            case LEFT:  newX--; break;
            case RIGHT: newX++; break;
            default:    return; // Do nothing for other keys
        }
        if (maze.isValidMove(newX, newY)) {
            x = newX;
            y = newY;
            Gdx.app.log("Character", "Moved to (" + newX + ", " + newY + ")");
        } else {
            Gdx.app.log("Character", "Invalid move to (" + newX + ", " + newY + ")");
        }
    }
    /**
     * Updates the state of the character.
     *
     * @param delta Time since last frame.
     */
    public void update(float delta) {
        stateTime += delta;
    }

    /**
     * Draws the character at its current position.
     *
     * @param batch The SpriteBatch used for drawing.
     * @param cam The camera for coordinate adjustment.
     */
    @Override
    public void draw(SpriteBatch batch, OrthographicCamera cam) {
        TextureRegion currentFrame;
        if (currentDirection != null) {
            currentFrame = animations[currentDirection.ordinal()].getKeyFrame(stateTime, true);
        } else {
            // Draw the first frame of the down animation when the game starts
            currentFrame = animations[Direction.DOWN.ordinal()].getKeyFrame(0, true);
        }
        batch.draw(currentFrame, x + cam.position.x, y + cam.position.y);
    }


    /**
     * Checks and updates the character's status based on the maze element it encounters.
     *
     * @param maze The maze containing the elements.
     */
    public void updateStatus(Maze maze) {
        int elementType = maze.getElementAt(x, y);
        switch (elementType) {
            case 3: // Trap
                loseLife();
                break;
            case 4: // Enemy
                loseLife();
                break;
            case 5: // Key
                hasKey = true;
                break;
            // Add more cases as needed
        }
    }

    /**
     * Decreases the character's lives by one.
     */
    private void loseLife() {
        lives--;
        if (lives <= 0) {
            // Handle character's death, like restarting the level or ending the game
        }
    }

    // Getters and setters

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public boolean hasKey() {
        return hasKey;
    }

    public void setHasKey(boolean hasKey) {
        this.hasKey = hasKey;
    }
}
