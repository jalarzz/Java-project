package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Key extends MazeElement {
    private Animation<TextureRegion> animation;

    private float stateTime;

    public Key(Animation<TextureRegion> animation, int x, int y) {
        super(animation.getKeyFrame(0), x, y, 16, 16);
        this.animation = animation;
        stateTime = 0f; // Initialize stateTime
    }

    public void update(float delta) {
        stateTime += delta; // Update the stateTime
        setTexture(animation.getKeyFrame(stateTime, true));
    }
}
