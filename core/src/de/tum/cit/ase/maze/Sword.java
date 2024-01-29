package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Sword extends Collectible {
    private Character character;
    public Sword(TextureRegion texture, float x, float y, Character character) {
        super(texture, x, y, 16, 16);
        this.character = character;
    }

    @Override
    protected void applyEffect() {
        character.setArmed(true);
        System.out.println("Sword collected: Player can now slay enemies automatically.");
    }
}