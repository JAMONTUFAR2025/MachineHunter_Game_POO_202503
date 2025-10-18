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

public class NextLevelUI implements InputProcessor {

    private GameplayState gameplayState;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture backgroundTexture;

    private String[] options = {"Siguiente Nivel"};
    private int selectedOption = 0;

    public NextLevelUI(GameplayState gameplayState, SpriteBatch batch) {
        this.gameplayState = gameplayState;
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

        drawMenu(options, Gdx.graphics.getHeight() / 2f + 80);
        
        drawControls();
        batch.end();
    }

    private void drawControls() {
        String controlsText = "E-Seleccionar";
        GlyphLayout layout = new GlyphLayout(font, controlsText);

        font.setColor(Color.WHITE);
        float textX = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float textY = 10 + layout.height + 20;
        font.draw(batch, controlsText, textX, textY);
    }

    private void drawMenu(String[] options, float startY) {
        drawText("¡Nivel completado!", Gdx.graphics.getHeight() / 2f + 160, false);
        for (int i = 0; i < options.length; i++) {
            drawText(options[i], startY - (i * 80), i == selectedOption);
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
        if (keycode == Input.Keys.ENTER || keycode == Input.Keys.E) {
            if (selectedOption == 0) { // Siguiente Nivel
                gameplayState.restartLevel();
            }
        }
        return true;
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
