package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Floor extends MazeElement {
    public Floor(TextureRegion texture, int x, int y) {
        super(texture, x, y,16,16);
    }
}
