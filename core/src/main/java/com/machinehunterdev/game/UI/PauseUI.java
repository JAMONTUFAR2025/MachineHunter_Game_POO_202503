package com.machinehunterdev.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.GameStates.GameplayState;

public class PauseUI implements InputProcessor {

    private GameplayState gameplayState;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture backgroundTexture;

    private enum MenuState { MAIN, CONFIRM_EXIT }
    private MenuState currentState = MenuState.MAIN;

    private String[] mainOptions = {"Reanudar", "Salir"};
    private String[] confirmOptions = {"Si", "No"};
    private int selectedOption = 0;

    public PauseUI(GameplayState gameplayState, SpriteBatch batch) {
        this.gameplayState = gameplayState;
        this.batch = batch;

        // Create a 1x1 white texture programmatically
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        this.backgroundTexture = new Texture(pixmap);
        pixmap.dispose();

        loadCustomBitmapFont();
    }

    private void loadCustomBitmapFont() {
        try {
            this.font = new BitmapFont(Gdx.files.internal("fonts/OrangeKid64.fnt"));
        } catch (Exception e) {
            this.font = new BitmapFont();
        }
    }

    public void draw() {
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        batch.begin();
        
        // Draw a semi-transparent background over the whole screen
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(Color.WHITE);

        // Draw menu text
        font.getData().setScale(1.0f);

        if (currentState == MenuState.MAIN) {
            drawMenu(mainOptions, Gdx.graphics.getHeight() / 2f + 80);
        } else if (currentState == MenuState.CONFIRM_EXIT) {
            drawConfirmationMenu();
        }
        drawControls();
        batch.end();
    }
    private void drawControls() {
        String controlsText = "Q-Retroceder | E-Seleccionar | W/S-Moverse";
        GlyphLayout layout = new GlyphLayout(font, controlsText);

        // Set the font scale for the controls text
        font.setColor(Color.WHITE);

        // Calculate the position of the text
        float textX = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float textY = 10 + layout.height + 20;

        // Draw the text
        font.draw(batch, controlsText, textX, textY);
    }


    private void drawMenu(String[] options, float startY) {
        for (int i = 0; i < options.length; i++) {
            drawText(options[i], startY - (i * 80), i == selectedOption);
        }
    }

    private void drawConfirmationMenu() {
        float startY = Gdx.graphics.getHeight() / 2f + 80;

        drawText("¿Esta seguro?", startY, false);
        
        for (int i = 0; i < confirmOptions.length; i++) {
            float optionY = startY - ((i + 1) * 80);
            drawText(confirmOptions[i], optionY, i == selectedOption);
        }
    }

    private void drawText(String text, float y, boolean isSelected) {
        GlyphLayout layout = new GlyphLayout(font, text);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2f;
        font.setColor(isSelected ? new Color(0.7f, 0, 0, 1) : Color.WHITE);
        font.draw(batch, text, x, y);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (currentState == MenuState.MAIN) {
            handleMainMenuInput(keycode);
        } else if (currentState == MenuState.CONFIRM_EXIT) {
            handleConfirmExitInput(keycode);
        }
        return true;
    }

    private void handleMainMenuInput(int keycode) {
        if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            selectedOption = (selectedOption - 1 + mainOptions.length) % mainOptions.length;
        } else if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            selectedOption = (selectedOption + 1) % mainOptions.length;
        } else if (keycode == Input.Keys.ENTER || keycode == Input.Keys.E) {
            if (selectedOption == 0) { // Reanudar
                gameplayState.resumeGame();
            } else if (selectedOption == 1) { // Salir
                currentState = MenuState.CONFIRM_EXIT;
                selectedOption = 0;
            }
        }else if (keycode == Input.Keys.Q){
            gameplayState.resumeGame();

        }
    }

    private void handleConfirmExitInput(int keycode) {
        if (keycode == Input.Keys.UP || keycode == Input.Keys.W) {
            selectedOption = (selectedOption - 1 + confirmOptions.length) % confirmOptions.length;
        } else if (keycode == Input.Keys.DOWN || keycode == Input.Keys.S) {
            selectedOption = (selectedOption + 1) % confirmOptions.length;
        } else if (keycode == Input.Keys.ENTER || keycode == Input.Keys.E) {
            if (selectedOption == 0) { // Si
                gameplayState.exitToMainMenu();
            } else if (selectedOption == 1) { // No
                currentState = MenuState.MAIN;
                selectedOption = 0;
            }
        }else if (keycode == Input.Keys.Q){
            currentState = MenuState.MAIN;
            selectedOption = 0;
        }
    }

    public void dispose() {
        font.dispose();
        backgroundTexture.dispose();
    }

    // Unused methods
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
}
