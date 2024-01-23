package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public abstract class MazeElement {
    protected TextureRegion texture;
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected Rectangle bounds;

    public MazeElement(TextureRegion texture, float x, float y, float width, float height) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle(x, y, width, height); // Initialize the bounding box
    }

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