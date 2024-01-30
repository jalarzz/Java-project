package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Sword extends Collectible {
    private Character character;
    private HUD hud;
    public Sword(TextureRegion texture, float x, float y, Character character, HUD hud) {
        super(texture, x, y, 16, 16);
        this.character = character;
        this.hud = hud;
    }

    @Override
    protected void applyEffect() {
        character.setArmed(true);
        hud.showMessage("You can slay now!");
        System.out.println("Sword collected: Player can now slay enemies automatically.");
    }
}