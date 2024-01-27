package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;

public class HUD {
    private final Stage stage;
    private final Image[] hearts;
    private final Image keyImage;
//    private final TextureRegion keyTexture;
//    private final TextureRegion noKeyTexture;
    float heartScaling = 2.0f;
    float keyScaling = 5f;
    private Animation<TextureRegion> keyAnimation;
    private Animation<TextureRegion> noKeyAnimation;
    private float stateTime = 0;


    public HUD(TextureRegion fullHeart, TextureRegion emptyHeart, Animation<TextureRegion> keyAnimation, Animation<TextureRegion> noKeyAnimation, int initialLives) {
        // ... existing initialization code ...

        this.keyAnimation = keyAnimation;
        this.noKeyAnimation = noKeyAnimation;





        stage = new Stage(new ScreenViewport());
        Table leftTable = new Table();
        Table rightTable = new Table();
        // Set up the left table for hearts
        leftTable.top().left();
        hearts = new Image[initialLives];
        for (int i = 0; i < initialLives; i++) {
            hearts[i] = new Image(fullHeart);
            hearts[i].setScale(heartScaling);// Set the size of the heart images
            leftTable.add(hearts[i]).pad(25);
        }

        // Set up the right table for the key image
        rightTable.top().right();
        keyImage = new Image(noKeyAnimation.getKeyFrame(0)); // Initialize with the first frame of no key animation
        keyImage.setScale(keyScaling);
        rightTable.add(keyImage).pad(50, 0, 0, 50);

        // Add both tables to the stage
        stage.addActor(leftTable);
        stage.addActor(rightTable);
        leftTable.setFillParent(true);
        rightTable.setFillParent(true);


    }


    public void updateHearts(int currentLives, TextureRegion fullHeart, TextureRegion emptyHeart) {
        for (int i = 0; i < hearts.length; i++) {
            hearts[i].setDrawable(new TextureRegionDrawable(i < currentLives ? fullHeart : emptyHeart));
        }
    }

    public void updateKey(float delta, boolean hasKey) {
        stateTime += delta; // Update stateTime with the time elapsed since the last frame

        Animation<TextureRegion> currentAnimation = hasKey ? noKeyAnimation : keyAnimation;
        keyImage.setDrawable(new TextureRegionDrawable(currentAnimation.getKeyFrame(stateTime, true)));
    }

    public void updateExit(boolean reachedExit) {
        if(reachedExit){
            Label exitLabel = new Label("Exit reached", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
            exitLabel.setFontScale(2.0f);
            stage.addActor(exitLabel);
            exitLabel.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        }
    }

    public void draw() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }



    public void dispose() {
        stage.dispose();
    }
}
