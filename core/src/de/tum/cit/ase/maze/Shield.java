package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Shield extends Collectible {
    Character player;
    private HUD hud;
    public Shield(TextureRegion texture, float x, float y, HUD hud, Character player) {
        super(texture, x, y, 16, 16);
        this.hud = hud;
        this.player = player;
    }

    @Override
    protected void applyEffect() {

        player.extendInvulnerability(20.0f);
        hud.showMessage("You are invincible!");
        System.out.println("Shield collected: Player is temporarily invulnerable.");
    }
}