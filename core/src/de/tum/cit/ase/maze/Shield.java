package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Shield extends Collectible {
    private HUD hud;
    public Shield(TextureRegion texture, float x, float y, HUD hud) {
        super(texture, x, y, 16, 16);
        this.hud = hud;
    }

    @Override
    protected void applyEffect() {
        // Implement effect, e.g., temporary invulnerability
        hud.showMessage("You are invincible!");
        System.out.println("Shield collected: Player is temporarily invulnerable.");
    }
}