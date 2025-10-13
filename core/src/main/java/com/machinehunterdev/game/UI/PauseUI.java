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
        
        // Create a 1x1 semi-transparent black texture programmatically
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.5f); // Black with 50% alpha
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
        
        // Draw semi-transparent background
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw menu text
        font.getData().setScale(1.5f);

        if (currentState == MenuState.MAIN) {
            drawMenu(mainOptions, Gdx.graphics.getHeight() / 2f + 100);
        } else if (currentState == MenuState.CONFIRM_EXIT) {
            drawConfirmationMenu();
        }

        batch.end();
    }

    private void drawMenu(String[] options, float startY) {
        for (int i = 0; i < options.length; i++) {
            drawText(options[i], startY - (i * 100), i == selectedOption);
        }
    }

    private void drawConfirmationMenu() {
        drawText("¿Está seguro?", Gdx.graphics.getHeight() / 2f + 100, false);
        drawMenu(confirmOptions, Gdx.graphics.getHeight() / 2f);
    }

    private void drawText(String text, float y, boolean isSelected) {
        GlyphLayout layout = new GlyphLayout(font, text);
        float x = (Gdx.graphics.getWidth() - layout.width) / 2f;
        font.setColor(isSelected ? Color.RED : Color.WHITE);
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
