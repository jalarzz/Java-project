package de.tum.cit.ase.maze;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;

import java.awt.*;

public class HUD {
    private final Stage stage;
    private final Image[] hearts;
    private final Image keyImage;
    float heartScaling = 2.0f;
    float keyScaling = 5f;
    private Animation<TextureRegion> keyAnimation;
    private Animation<TextureRegion> noKeyAnimation;
    private float stateTime = 0;
    private Label messageLabel; // Add a Label for displaying messages
    private Label invincibilityLabel; // Label for displaying invincibility countdown


    /**
     * Constructs a HUD display with heart icons for lives, a key animation for key possession status,
     * and an optional message display for in-game notifications.
     *
     * @param fullHeart TextureRegion for a full heart icon.
     * @param emptyHeart TextureRegion for an empty heart icon.
     * @param keyAnimation Animation for the key when the player possesses it.
     * @param noKeyAnimation Animation for the key when the player does not possess it.
     * @param initialLives The initial number of lives to display.
     */

    public HUD(TextureRegion fullHeart, TextureRegion emptyHeart, Animation<TextureRegion> keyAnimation, Animation<TextureRegion> noKeyAnimation, int initialLives) {

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
        rightTable.add(keyImage).pad(60, 0, 0, 60);

        // Add both tables to the stage
        stage.addActor(leftTable);
        stage.addActor(rightTable);
        leftTable.setFillParent(true);
        rightTable.setFillParent(true);

        //Stuff for message
        // Initialize the message Label
        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        messageLabel = new Label("", labelStyle);
        messageLabel.setFontScale(2.0f); // Adjust the scale as needed
        messageLabel.setPosition(Gdx.graphics.getWidth() / 8f, Gdx.graphics.getHeight() * 0.9f); // Position the label on the screen
        messageLabel.setAlignment(Align.center); // Center the text
        stage.addActor(messageLabel); // Add the label to the stage


        // Initialize the invincibility countdown label
        invincibilityLabel = new Label("", labelStyle);
        invincibilityLabel.setFontScale(1.5f);
        invincibilityLabel.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() * 0.85f);
        invincibilityLabel.setAlignment(Align.center);
        stage.addActor(invincibilityLabel);



    }
    /**
     * Displays a temporary message on the HUD.
     *
     * @param messageText The text of the message to display.
     */

    public void showMessage(String messageText) {
        messageLabel.setText(messageText); // Set the text of the message
        messageLabel.setVisible(true); // Make the label visible

        // Schedule hiding the message after a few seconds
        float delay = 3; // delay in seconds before the message disappears
        messageLabel.addAction(Actions.sequence(Actions.delay(delay), Actions.fadeOut(0.5f), Actions.run(() -> messageLabel.setVisible(false))));
    }
    /**
     * Updates the HUD with the current invincibility timer.
     * @param invincibilityTime Remaining time of invincibility in seconds.
     */
    public void updateInvincibilityTimer(float invincibilityTime) {
        if (invincibilityTime > 0) {
            invincibilityLabel.setText(String.format("Invincible: %.1f", invincibilityTime));
            invincibilityLabel.setVisible(true);
        } else {
            invincibilityLabel.setVisible(false);
        }
    }


    /**
     * Updates the display of player lives in the HUD based on the current number of lives.
     *
     * @param currentLives The current number of lives the player has.
     * @param fullHeart The texture to use for a full heart (life available).
     * @param emptyHeart The texture to use for an empty heart (life lost).
     */
    public void updateHearts(int currentLives, TextureRegion fullHeart, TextureRegion emptyHeart) {
        for (int i = 0; i < hearts.length; i++) {
            hearts[i].setDrawable(new TextureRegionDrawable(i < currentLives ? fullHeart : emptyHeart));
        }
    }
    /**
     * Updates the key icon in the HUD based on whether the player currently has a key.
     *
     * @param delta The time in seconds since the last frame.
     * @param hasKey A boolean indicating whether the player currently possesses a key.
     */
    public void updateKey(float delta, boolean hasKey) {
        stateTime += delta; // Update stateTime with the time elapsed since the last frame

        Animation<TextureRegion> currentAnimation = hasKey ? noKeyAnimation : keyAnimation;
        keyImage.setDrawable(new TextureRegionDrawable(currentAnimation.getKeyFrame(stateTime, true)));
    }
    /**
     * Displays a notification when the exit is reached by the player.
     *
     * @param reachedExit A boolean indicating whether the player has reached the exit.
     */
    public void updateExit(boolean reachedExit) {
        if(reachedExit){
            Label exitLabel = new Label("Exit reached", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
            exitLabel.setFontScale(2.0f);
            stage.addActor(exitLabel);
            exitLabel.setPosition(Gdx.graphics.getWidth()/2f, Gdx.graphics.getHeight()/2f);
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
