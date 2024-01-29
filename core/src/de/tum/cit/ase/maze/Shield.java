package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Shield extends Collectible {
    public Shield(TextureRegion texture, float x, float y) {
        super(texture, x, y, 16, 16);
    }

    @Override
    protected void applyEffect() {
        // Implement effect, e.g., temporary invulnerability
        System.out.println("Shield collected: Player is temporarily invulnerable.");
    }
}