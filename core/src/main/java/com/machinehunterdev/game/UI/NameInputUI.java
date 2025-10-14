package com.machinehunterdev.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.machinehunterdev.game.Character.Character;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.GameStates.GameplayState;
import com.machinehunterdev.game.Gameplay.GlobalSettings;

public class NameInputUI implements InputProcessor {

    private BitmapFont font;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private GameController gameController;
    private StringBuilder playerName;
    private float elapsedTime = 0;

    public NameInputUI(SpriteBatch batch, GameController gameController) {
        this.batch = batch;
        this.gameController = gameController;
        this.playerName = new StringBuilder();
        this.shapeRenderer = new ShapeRenderer();
        loadCustomBitmapFont();
    }

    private void loadCustomBitmapFont() {
        try {
            this.font = new BitmapFont(Gdx.files.internal("fonts/OrangeKid64.fnt"));
            this.font.setColor(Color.WHITE);
        } catch (Exception e) {
            System.err.println("Error al cargar la fuente personalizada. Usando fuente por defecto.");
            this.font = new BitmapFont();
        }
    }

    public void draw(Character playerCharacter) {
        batch.begin();

        // Draw the character
        playerCharacter.draw(batch);

        GlyphLayout layout = new GlyphLayout();
        String prompt = "Ingresa tu nombre";
        font.getData().setScale(2);
        layout.setText(font, prompt);
        float promptX = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float promptY = Gdx.graphics.getHeight() - 50;
        font.draw(batch, prompt, promptX, promptY);

        font.getData().setScale(1.5f);
        String nameText = playerName.toString();
        layout.setText(font, nameText);
        float nameX = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float nameY = Gdx.graphics.getHeight() / 4f;
        font.draw(batch, nameText, nameX, nameY);

        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        elapsedTime += Gdx.graphics.getDeltaTime();
        if ((int)(elapsedTime * 2) % 2 == 0) {
            float cursorX = nameX + layout.width;
            shapeRenderer.rect(cursorX, nameY - font.getCapHeight(), 20, font.getCapHeight() + 10);
        }
        shapeRenderer.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ENTER) {
            if (playerName.length() > 0) {
                GlobalSettings.playerName = playerName.toString();
                gameController.stateMachine.changeState(GameplayState.instance);
            }
        } else if (keycode == Input.Keys.BACKSPACE) {
            if (playerName.length() > 0) {
                playerName.setLength(playerName.length() - 1);
            }
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        if (playerName.length() < 15 && (java.lang.Character.isLetterOrDigit(character) || character == ' ')) {
            playerName.append(character);
        }
        return true;
    }

    public void dispose() {
        if (font != null) {
            font.dispose();
        }
        shapeRenderer.dispose();
    }

    // Métodos de InputProcessor no usados
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
}