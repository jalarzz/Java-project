package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class MazeElement {
    protected TextureRegion texture;
    protected int x;
    protected int y;

    public MazeElement(TextureRegion texture, int x, int y) {
        this.texture = texture;
        this.x = x;
        this.y = y;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }
}