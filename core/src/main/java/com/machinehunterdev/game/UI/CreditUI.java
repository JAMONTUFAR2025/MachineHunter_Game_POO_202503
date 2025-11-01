package com.machinehunterdev.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.machinehunterdev.game.GameController;
import com.machinehunterdev.game.GameStates.MainMenuState;

public class CreditUI implements InputProcessor {

    private SpriteBatch batch;
    private GameController gameController;
    private Texture backgroundTexture;
    private BitmapFont font;
    private String[] credits = {
            "MACHINE HUNTER",
            "Créditos",
            "",
            "Clase",
            "Programación Orientada a Objetos",
            "",
            "Período 2025-3",
            "",
            "Guión",
            "Raúl Fernando Ramos Lara",
            "",
            "Arte",
            "Mariely Nicol Hiraeta Henriquez",
            "Ken Kato Castellanos",
            "",
            "Programación",
            "Josué Alejandro Montúfar Zúniga",
            "Anner Alessandro Teruel Pineda",
            "Ken Kato Castellanos",
            "",
            "Música y sonidos",
            "Sin definir",
            "",
            "",
            "¡Gracias por Jugar!",
            "Ningún individuo sea guionista, artista o programador",
            "fue sobreexplotado en la elaboración de este producto",
            "*guiño* *guiño*"
    };

    private float scrollY;
    private float scrollSpeed = 100f;
    private boolean creditsFinished = false;
    private float finishedTimer = 0f;
    private boolean skipUsed = false;

    public CreditUI(SpriteBatch batch, GameController gameController) {
        this.batch = batch;
        this.gameController = gameController;
        this.backgroundTexture = new Texture(Gdx.files.internal("Fondos/NameInputBackgroundShadowless.png"));
        this.font = new BitmapFont(Gdx.files.internal("fonts/OrangeKid64.fnt"));
        this.scrollY = -200; // Start credits off-screen
    }

    public void draw() {
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        GlyphLayout layout = new GlyphLayout();
        float y = scrollY;

        if (!creditsFinished) {
            scrollY += scrollSpeed * Gdx.graphics.getDeltaTime();
        }

        float totalHeight = 0;
        for (int i = 0; i < credits.length; i++) {
            String line = credits[i];
            if (line.equals("MACHINE HUNTER")) {
                font.setColor(Color.RED);
            } else if (line.equals("Clase") ||
                       line.equals("Guión") ||
                       line.equals("Arte") ||
                       line.equals("Programación") ||
                       line.equals("Música y sonidos") ||
                       line.equals("¡Gracias por Jugar!")) {
                font.setColor(Color.CYAN);
            } else {
                font.setColor(Color.WHITE);
            }
            layout.setText(font, line);
            float x = (Gdx.graphics.getWidth() - layout.width) / 2;
            font.draw(batch, line, x, y - totalHeight);
            totalHeight += 80;
        }

        if (y - totalHeight > Gdx.graphics.getHeight()) {
            creditsFinished = true;
        }

        if (creditsFinished) {
            finishedTimer += Gdx.graphics.getDeltaTime();
            if (finishedTimer > 1) {
                gameController.stateMachine.changeState(MainMenuState.instance);
            }
        }

        batch.end();
    }

    public void dispose() {
        if (font != null) {
            font.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            gameController.stateMachine.changeState(MainMenuState.instance);
        }
        if (keycode == Input.Keys.ENTER) {
            if (!creditsFinished && !skipUsed) {
                float totalHeight = credits.length * 80;
                scrollY = totalHeight - 400; // Adjust to show the last few phrases
                skipUsed = true;
            } else if (creditsFinished) {
                gameController.stateMachine.changeState(MainMenuState.instance);
            }
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
     @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }
}
