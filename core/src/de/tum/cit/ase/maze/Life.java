package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Life extends Collectible {
    private Character playerCharacter;
    private HUD hud;
    public Life(TextureRegion texture, float x, float y, Character playerCharacter, HUD hud) {
        super(texture, x, y, 16, 16);
        this.playerCharacter = playerCharacter;
        this.hud = hud;
    }

    @Override
    protected void applyEffect() {
        //increase player's lives
        playerCharacter.setLives(playerCharacter.getLives()+1);
        hud.showMessage("One more life. Good for you!");

        System.out.println("Life collected: Player's lives increased.");
    }
}