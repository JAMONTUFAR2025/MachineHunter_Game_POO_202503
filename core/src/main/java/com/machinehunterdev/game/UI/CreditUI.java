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
            "Sin definir"
    };

    private String[] finalMessage = {
            "Gracias por jugar...",
            "ningún individuo, sea artista, guionista, o programador",
            "fue sobreexplotado en la elaboración de este producto",
            "*guiño guiño*"
    };

    private float scrollY;
    private float scrollSpeed = 100f; // pixels per second
    private boolean scrollingFinished = false;
    private float typingTimer = 0f;
    private float timePerChar = 0.05f;
    private int charIndex = 0;
    private int lineIndex = 0;
    private boolean typingFinished = false;

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

        if (!scrollingFinished) {
            scrollY += scrollSpeed * Gdx.graphics.getDeltaTime();
            for (int i = 0; i < credits.length; i++) {
                if (credits[i].equals("MACHINE HUNTER")) {
                    font.setColor(Color.RED);
                } else if (credits[i].equals("Clase") ||
                           credits[i].equals("Guión") ||
                           credits[i].equals("Arte") ||
                           credits[i].equals("Programación") ||
                           credits[i].equals("Música y sonidos")) {
                    font.setColor(Color.CYAN); // Sky blue
                } else {
                    font.setColor(Color.WHITE);
                }
                layout.setText(font, credits[i]);
                float x = (Gdx.graphics.getWidth() - layout.width) / 2;
                font.draw(batch, credits[i], x, y);
                y -= 80; // Line spacing
            }

            if (y > Gdx.graphics.getHeight()) {
                scrollingFinished = true;
            }
        } else {
            // Typing effect for the final message
            if (!typingFinished) {
                typingTimer += Gdx.graphics.getDeltaTime();
                if (lineIndex < finalMessage.length) {
                    String currentLine = finalMessage[lineIndex];
                    if (charIndex < currentLine.length()) {
                        if (typingTimer > timePerChar) {
                            charIndex++;
                            typingTimer = 0;
                        }
                    } else {
                        lineIndex++;
                        charIndex = 0;
                    }
                } else {
                    typingFinished = true;
                }
            }

            float startY = Gdx.graphics.getHeight() / 2 + 100;
            for (int i = 0; i < lineIndex; i++) {
                font.setColor(Color.WHITE); // Ensure white for the main part of the final message
                layout.setText(font, finalMessage[i]);
                float x = (Gdx.graphics.getWidth() - layout.width) / 2;
                font.draw(batch, finalMessage[i], x, startY - i * 80);
            }
            
            if (lineIndex < finalMessage.length) {
                font.setColor(Color.WHITE); // Ensure white for the main part of the final message
                String lineToDraw = finalMessage[lineIndex].substring(0, charIndex);
                layout.setText(font, lineToDraw);
                float x = (Gdx.graphics.getWidth() - layout.width) / 2;
                font.draw(batch, lineToDraw, x, startY - lineIndex * 80);
            }

            if (typingFinished) {
                font.setColor(Color.WHITE); // "Enter para continuar..." should be white
                layout.setText(font, "Enter para continuar...");
                float x = (Gdx.graphics.getWidth() - layout.width) / 2;
                font.draw(batch, "Enter para continuar...", x, 50);
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
        if (typingFinished && keycode == Input.Keys.ENTER) {
            gameController.stateMachine.changeState(MainMenuState.instance);
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