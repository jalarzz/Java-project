package de.tum.cit.ase.maze;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;

public class HUD {
    private Stage stage;
    private Image[] hearts;
    private Image keyImage;
    private TextureRegion keyTexture;
    private TextureRegion noKeyTexture;

    public HUD(TextureRegion fullHeart, TextureRegion emptyHeart, TextureRegion keyTexture, TextureRegion noKeyTexture, int initialLives) {
        this.keyTexture = keyTexture;
        this.noKeyTexture = noKeyTexture;

        stage = new Stage(new ScreenViewport());
        Table table = new Table();
        table.top();
        table.setFillParent(true);

        // Add hearts
        hearts = new Image[initialLives];
        for (int i = 0; i < initialLives; i++) {
            hearts[i] = new Image(fullHeart);
            table.add(hearts[i]).pad(10);
        }

        // Add key image
        keyImage = new Image(noKeyTexture);
        table.add(keyImage).pad(10).top().right();

        stage.addActor(table);
    }

    public void updateHearts(int currentLives, TextureRegion fullHeart, TextureRegion emptyHeart) {
        for (int i = 0; i < hearts.length; i++) {
            hearts[i].setDrawable(new TextureRegionDrawable(i < currentLives ? fullHeart : emptyHeart));
        }
    }

    public void updateKey(boolean hasKey) {
        keyImage.setDrawable(new TextureRegionDrawable(hasKey ? keyTexture : noKeyTexture));
    }

    public void draw() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
    }
}
