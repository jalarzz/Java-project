package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Character class represents the player character in the game.
 * It includes movement and animation handling based on direction.
 */
public class Character extends MazeElement implements Movable {
    private int lives;
    private boolean hasKey;
    private boolean reachedExit;
    private Animation<TextureRegion>[] animations;
    private float stateTime;
    private Direction currentDirection;
    private OrthographicCamera camera;
    private static final int TILE_SIZE = 16; // each tile is 16x16
    private static final int CHAR_WIDTH = 16; // Character width
    private static final int CHAR_HEIGHT = 32; // Character height
    private static final float INVULNERABILITY_TIME = 3.0f; // 2 seconds of invulnerability
    private float invulnerabilityTimer = 0;
    private Sound loseLife;
    private Sound keyObtained;
    private Maze maze;
    private boolean isArmed;
    private boolean keySoundPlayed = false;





    /**
     * Constructor for the Character class.
     *
     * @param animations Array of animations for each direction.
     * @param x Initial x-coordinate of the character.
     * @param y Initial y-coordinate of the character.
     * @param lives Number of lives the character starts with.
     */
    public Character(Animation<TextureRegion>[] animations, float x, float y, int lives, OrthographicCamera camera) {
        super(null, x, y,CHAR_WIDTH,CHAR_HEIGHT); // texture is set to null initially
        this.animations = animations;
        this.lives = lives;
        this.hasKey = false;
        this.stateTime = 0f;
        this.currentDirection = null; // Default direction
        this.camera = camera;
        this.bounds = new Rectangle(x+2,y+2,8,4);
        this.isArmed = false;
        this.loseLife = Gdx.audio.newSound(Gdx.files.internal("Realistic_Punch-Mark_DiAngelo-1609462330.mp3"));
        this.keyObtained = Gdx.audio.newSound(Gdx.files.internal("mario-coin-sfx.mp3"));

    }

    /**
     * Moves the character in the given direction if the move is valid.
     * Checks for collision with maze elements and performs actions based on the type of collision.
     *
     * @param direction The direction to move the character.
     * @param maze      The maze to check for valid moves.
     * @param delta     The time passed since the last frame.
     */
    @Override
    public void move(Direction direction, Maze maze, float delta) {
        float newX = x, newY = y;
        float speed = TILE_SIZE * delta * 3; // Adjust the speed if necessary

        // Determine new position based on direction
        switch (direction) {
            case UP:    newY += speed; break;
            case DOWN:  newY -= speed; break;
            case LEFT:  newX -= speed; break;
            case RIGHT: newX += speed; break;
            default:    return; // Invalid direction
        }
        // Update the current direction
        this.currentDirection = direction;

        // Create a temporary bounding box for the intended position
        Rectangle tempBounds = new Rectangle(bounds);
        tempBounds.setPosition(newX, newY);

        // Check collision
        int collisionType = maze.checkCollision(tempBounds, hasKey);
        handleCollision(collisionType, newX, newY);
    }

    /**
     * Handles collision based on the type of element collided with.
     *
     * @param collisionType The type of element collided with.
     * @param newX          The new X position.
     * @param newY          The new Y position.
     */
    private void handleCollision(int collisionType, float newX, float newY) {
        switch (collisionType) {
            case 0: // Wall
            case 2: // Exit without key
                // Movement is blocked
                break;
            case 3: // Trap

                loseLife();
                setPosition(newX, newY);
                camera.position.set(bounds.x, bounds.y, camera.position.z);
                break;
            case 5: // Key
                setHasKey(true);
                if (!keySoundPlayed) {
                    keyObtained.play();
                    keySoundPlayed = true;
                }





                // Fall through to default case to allow movement
            default:
                // Move the character and update camera
                setPosition(newX, newY);
                camera.position.set(bounds.x, bounds.y, camera.position.z);
                break;
        }
    }

    /**
     * Updates the character's position and bounding box.
     *
     * @param newX The new X-coordinate of the character.
     * @param newY The new Y-coordinate of the character.
     */
    public void setPosition(float newX, float newY) {
        this.x = newX;
        this.y = newY;
        this.bounds.setPosition(newX, newY);
    }







    /**
     * Updates the state of the character.
     *
     * @param delta Time since last frame.
     */
    public void update(float delta) {
        stateTime += delta;
        if (invulnerabilityTimer > 0) {
            invulnerabilityTimer -= delta;
        }
    }

    /**
     * Draws the character at its current position.
     *
     * @param batch The SpriteBatch used for drawing.
     *
     */
    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion currentFrame;

        if (currentDirection != null) {
            // Use the animation corresponding to the current direction
            currentFrame = animations[currentDirection.ordinal()].getKeyFrame(stateTime/3, true);
        } else {
            // When not moving, show the first frame of the down animation
            currentFrame = animations[Direction.RIGHT.ordinal()].getKeyFrame(0, false);
        }

        batch.draw(currentFrame, x-4, y-4, CHAR_WIDTH, CHAR_HEIGHT);
    }



    /**
     * Checks and updates the character's status based on the maze element it encounters.
     *
     * @param maze The maze containing the elements.
     */
    public void updateStatus(Maze maze, Array<MazeElement> mazeElements) {
        try {
            int elementType = maze.getElementAt((int) x, (int) y);
            //Gdx.app.log("updateStatus", "Element Type: " + elementType);

            switch (elementType) {
                case 2: // Exit
                    break;
                case 3: // Trap
                    loseLife();
                    break;
                case 5: // Key
                    hasKey = true;
                    break;
            }

            for (MazeElement element : mazeElements) {
                if (element instanceof Enemy) {
                    Enemy enemy = (Enemy) element;
                    if (this.bounds.overlaps(enemy.getBounds())) {
                       // Gdx.app.log("updateStatus", "Collision with Enemy");
                        loseLife();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Gdx.app.error("updateStatus", "Error in updateStatus: " + e.getMessage(), e);
        }
    }
    /**
     * Decreases the character's lives by one.
     */
    private void loseLife() {
        if (invulnerabilityTimer <= 0) {
            lives--;
          loseLife.play();
            invulnerabilityTimer = INVULNERABILITY_TIME;
            if (lives <= 0) {

            }
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

    public boolean hasReachedExit() {
        return reachedExit;
    }

    public void setReachedExit(boolean reachedExit) {
        this.reachedExit = reachedExit;
    }

    public boolean isHasKey() {
        return hasKey;
    }

    public boolean isReachedExit() {
        return reachedExit;
    }

    public Animation<TextureRegion>[] getAnimations() {
        return animations;
    }

    public float getStateTime() {
        return stateTime;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public float getInvulnerabilityTimer() {
        return invulnerabilityTimer;
    }

    public Sound getLoseLife() {
        return loseLife;
    }

    public Maze getMaze() {
        return maze;
    }
    public boolean isArmed() {
        return isArmed;
    }

    public void setArmed(boolean armed) {
        isArmed = armed;
    }
}