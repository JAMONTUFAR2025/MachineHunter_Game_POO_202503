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
import com.machinehunterdev.game.GameController;
import com.badlogic.gdx.utils.Align;
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
            "Catedrático",
            "Luis Fernando Truel Umanzor",
            "",
            "Guión",
            "Raúl Fernando Ramos Lara",
            "",
            "Arte",
            "Mariely Nicol Hiraeta Henriquez",
            "Ken Kato Castellanos",
            "",
            "Texturas",
            "Explosion",
            "A quien corresponda",
            "Rayo",
            "Pokémon Ruby & Sapphire",
            "",
            "Programación",
            "Josué Alejandro Montúfar Zúniga",
            "Anner Alessandro Teruel Pineda",
            "Ken Kato Castellanos",
            "",
            "Música y sonidos",
            "Mario & Luigi: Superstar Saga",
            "Pokémon Ruby & Sapphire",
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

    private float skipTimer = 0f;
    private boolean isSkipping = false;
    private static final float TIME_TO_SKIP = 5f;
    private Texture skipIndicatorTexture;
    private boolean fastForward = false;

    public CreditUI(SpriteBatch batch, GameController gameController) {
        this.batch = batch;
        this.gameController = gameController;
        this.backgroundTexture = new Texture(Gdx.files.internal("Fondos/NameInputBackgroundShadowless.png"));
        this.font = new BitmapFont(Gdx.files.internal("fonts/OrangeKid64.fnt"));
        this.scrollY = -200; // Start credits off-screen

        Pixmap skipPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        skipPixmap.setColor(Color.WHITE);
        skipPixmap.fill();
        skipIndicatorTexture = new Texture(skipPixmap);
        skipPixmap.dispose();
    }

    public void update(float dt) {
        if (isSkipping) {
            skipTimer += dt;
            if (skipTimer >= TIME_TO_SKIP) {
                gameController.stateMachine.changeState(MainMenuState.instance);
            }
        }
    }

    public void draw() {
        update(Gdx.graphics.getDeltaTime());

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        GlyphLayout layout = new GlyphLayout();
        float y = scrollY;

        float currentScrollSpeed = fastForward ? scrollSpeed * 2 : scrollSpeed;

        if (!creditsFinished) {
            scrollY += currentScrollSpeed * Gdx.graphics.getDeltaTime();
        }

        float totalHeight = 0;
        for (int i = 0; i < credits.length; i++) {
            String line = credits[i];
            if (line.equals("MACHINE HUNTER")) {
                font.setColor(Color.RED);
            } else if (line.equals("Clase") ||
                        line.equals("Catedrático") ||
                        line.equals("Guión") ||
                        line.equals("Arte") ||
                        line.equals("Texturas") ||
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

        float barWidth = 200;
        float barHeight = 30; // Adjusted position
        float barX = Gdx.graphics.getWidth() - barWidth - 50; // Adjusted position
        float barY = 80; // Adjusted position

        batch.setColor(0.2f, 0.2f, 0.2f, 1);
        batch.draw(skipIndicatorTexture, barX - 4, barY - 4, barWidth + 8, barHeight + 8);

        batch.setColor(0.5f, 0.5f, 0.5f, 1);
        batch.draw(skipIndicatorTexture, barX, barY, barWidth, barHeight);
        batch.setColor(1, 1, 1, 1); // Reset color

        if (isSkipping) {
            float progress = skipTimer / TIME_TO_SKIP;
            batch.setColor(1, 1, 1, 1);
            batch.draw(skipIndicatorTexture, barX + 4, barY + 4, (barWidth - 8) * progress, barHeight - 8);
        }

        font.getData().setScale(0.5f);
        font.draw(batch, "Mantén ENTER para omitir", barX, barY + barHeight + 30, barWidth, Align.center, false);
        font.getData().setScale(1.0f);

        batch.end();
    }

    public void dispose() {
        if (font != null) {
            font.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        if (skipIndicatorTexture != null) {
            skipIndicatorTexture.dispose();
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            gameController.stateMachine.changeState(MainMenuState.instance);
        }
        if (keycode == Input.Keys.ENTER) {
            isSkipping = true;
        }
        if (keycode == Input.Keys.E) {
            fastForward = true;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.ENTER) {
            isSkipping = false;
            skipTimer = 0;
        }
        if (keycode == Input.Keys.E) {
            fastForward = false;
        }
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
