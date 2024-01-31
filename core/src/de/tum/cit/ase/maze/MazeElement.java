package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
/**
 * Represents a generic element within the maze, serving as a base class for all interactable and drawable entities.
 * This abstract class provides common attributes such as position, texture, and bounding box for collision detection.
 */

public abstract class MazeElement {
    protected TextureRegion texture; // The graphical texture of the maze element
    protected float x; // The x-coordinate of the element's position
    protected float y; // The y-coordinate of the element's position
    protected float width; // The width of the element
    protected float height; // The height of the element
    protected Rectangle bounds; // The bounding box for collision detection


    /**
     * Constructs a MazeElement with specified texture, position, and size.
     *
     * @param texture The texture for visual representation of the element.
     * @param x The initial x-coordinate of the element.
     * @param y The initial y-coordinate of the element.
     * @param width The width of the element.
     * @param height The height of the element.
     */
    public MazeElement(TextureRegion texture, float x, float y, float width, float height) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle(x, y, width, height); // Initialize the bounding box
    }
    /**
     * Draws the maze element using the provided SpriteBatch.
     *
     * @param batch The SpriteBatch used to draw the texture.
     */
    public void draw(SpriteBatch batch) {
        batch.draw(texture, x , y );
    }

    public TextureRegion getTexture() {
        return texture;
    }

    public void setTexture(TextureRegion texture) {
        this.texture = texture;
    }

    public float getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        this.bounds.setX(x);
    }

    public float getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        this.bounds.setY(y);
    }

    public Rectangle getBounds() {
        return bounds;
    }
}