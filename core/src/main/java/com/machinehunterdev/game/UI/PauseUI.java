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
import com.machinehunterdev.game.Audio.AudioId;
import com.machinehunterdev.game.Audio.AudioManager;
import com.machinehunterdev.game.GameStates.PauseState;
import com.machinehunterdev.game.Gameplay.GlobalSettings;

public class PauseUI implements InputProcessor {

    private PauseState pauseState;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture backgroundTexture;

    private enum MenuState { MAIN, CONFIRM_EXIT, CONFIRM_RETRY }
    private MenuState currentState = MenuState.MAIN;

    private String[] mainOptions = {"Reanudar","Reintentar", "Salir"};
    private String[] confirmOptions = {"Si", "No"};
    
    private int selectedOption = 0;

    public PauseUI(PauseState pauseState, SpriteBatch batch) { // Changed constructor parameter
        this.pauseState = pauseState;
        this.batch = batch;

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
        
        batch.setColor(0, 0, 0, 0.7f);
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(Color.WHITE);

        font.getData().setScale(1.0f);

        if (currentState == MenuState.MAIN) {
            drawMenu(mainOptions, Gdx.graphics.getHeight() / 2f + 80);
        } else if (currentState == MenuState.CONFIRM_EXIT) {
            drawConfirmationMenu();
        } else if (currentState == MenuState.CONFIRM_RETRY) {
            drawRetryConfirmationMenu();
        }
        drawControls();
        batch.end();
    }

    private void drawControls() {
        String controlsText = Input.Keys.toString(GlobalSettings.CONTROL_CANCEL) + "-Retroceder | " + Input.Keys.toString(GlobalSettings.CONTROL_INTERACT) + "-Seleccionar | " + Input.Keys.toString(GlobalSettings.CONTROL_JUMP) + "/" + Input.Keys.toString(GlobalSettings.CONTROL_CROUCH) + "-Moverse";
        GlyphLayout layout = new GlyphLayout(font, controlsText);

        font.setColor(Color.WHITE);
        float textX = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float textY = 10 + layout.height + 20;
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

    private void drawRetryConfirmationMenu() {
        float startY = Gdx.graphics.getHeight() / 2f + 80;
        drawText("¿Desea reintentar?", startY, false);
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
        } else if (currentState == MenuState.CONFIRM_RETRY) {
            handleConfirmRetryInput(keycode);
        }
        return true;
    }

    private void handleMainMenuInput(int keycode) {
        if (keycode == GlobalSettings.CONTROL_JUMP) {
            AudioManager.getInstance().playSfx(AudioId.UIChange, null);
            selectedOption = (selectedOption - 1 + mainOptions.length) % mainOptions.length;
        } else if (keycode == GlobalSettings.CONTROL_CROUCH) {
            AudioManager.getInstance().playSfx(AudioId.UIChange, null);
            selectedOption = (selectedOption + 1) % mainOptions.length;
        } else if (keycode == GlobalSettings.CONTROL_INTERACT) {
            if (selectedOption == 0) { // Reanudar
                AudioManager.getInstance().playSfx(AudioId.UICancel, null);
                pauseState.resumeGame();
            } else if (selectedOption == 1) { // Reintentar
                AudioManager.getInstance().playSfx(AudioId.UIAccept, null);
                currentState = MenuState.CONFIRM_RETRY;
                selectedOption = 0;
            } else if (selectedOption == 2) { // Salir
                AudioManager.getInstance().playSfx(AudioId.UIAccept, null);
                currentState = MenuState.CONFIRM_EXIT;
                selectedOption = 0;
            }
        } else if (keycode == GlobalSettings.CONTROL_CANCEL) {
            AudioManager.getInstance().playSfx(AudioId.UICancel, null);
            pauseState.resumeGame();
        }
    }

    private void handleConfirmExitInput(int keycode) {
        if (keycode == GlobalSettings.CONTROL_JUMP) {
            AudioManager.getInstance().playSfx(AudioId.UIChange, null);
            selectedOption = (selectedOption - 1 + confirmOptions.length) % confirmOptions.length;
        } else if (keycode == GlobalSettings.CONTROL_CROUCH) {
            AudioManager.getInstance().playSfx(AudioId.UIChange, null);
            selectedOption = (selectedOption + 1) % confirmOptions.length;
        } else if (keycode == GlobalSettings.CONTROL_INTERACT) {
            if (selectedOption == 0) { // Si
                AudioManager.getInstance().playSfx(AudioId.UIAccept, null);
                pauseState.exitToMainMenu();
            } else if (selectedOption == 1) { // No
                AudioManager.getInstance().playSfx(AudioId.UICancel, null);
                currentState = MenuState.MAIN;
                selectedOption = 0;
            }
        } else if (keycode == GlobalSettings.CONTROL_CANCEL) {
            AudioManager.getInstance().playSfx(AudioId.UICancel, null);
            currentState = MenuState.MAIN;
            selectedOption = 0;
        }
    }

    private void handleConfirmRetryInput(int keycode) {
        if (keycode == GlobalSettings.CONTROL_JUMP) {
            AudioManager.getInstance().playSfx(AudioId.UIChange, null);
            selectedOption = (selectedOption - 1 + confirmOptions.length) % confirmOptions.length;
        } else if (keycode == GlobalSettings.CONTROL_CROUCH) {
            AudioManager.getInstance().playSfx(AudioId.UIChange, null);
            selectedOption = (selectedOption + 1) % confirmOptions.length;
        } else if (keycode == GlobalSettings.CONTROL_INTERACT) {
            if (selectedOption == 0) { // Si
                AudioManager.getInstance().playSfx(AudioId.UIAccept, null);
                pauseState.restartLevel();
            } else if (selectedOption == 1) { // No
                AudioManager.getInstance().playSfx(AudioId.UICancel, null);
                currentState = MenuState.MAIN;
                selectedOption = 0;
            }
        } else if (keycode == GlobalSettings.CONTROL_CANCEL) {
            AudioManager.getInstance().playSfx(AudioId.UICancel, null);
            currentState = MenuState.MAIN;
            selectedOption = 0;
        }
    }

    public void dispose() {
        font.dispose();
        backgroundTexture.dispose();
    }

    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
}
