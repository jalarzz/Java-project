package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Enemy extends MazeElement {
    public Enemy(TextureRegion texture, int x, int y) {
        super(texture, x, y,16,16);
    }

    // Additional methods for enemy behavior can be added here
}
