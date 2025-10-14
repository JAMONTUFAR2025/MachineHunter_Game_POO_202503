package com.machinehunterdev.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.machinehunterdev.game.Gameplay.GlobalSettings;

public class GameplayUI {

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera uiCamera;
    private BitmapFont font;
    private SpriteBatch batch;

    public GameplayUI(SpriteBatch batch) {
        this.batch = batch;
        this.shapeRenderer = new ShapeRenderer();
        this.uiCamera = new OrthographicCamera();
        this.uiCamera.setToOrtho(false, 1280, 720); // Assuming a fixed resolution for the UI
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

    public void draw(int playerHealth) {
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        int totalHealth = 3;
        int squareSize = 30;
        int padding = 10;

        for (int i = 0; i < totalHealth; i++) {
            if (i < playerHealth) {
                shapeRenderer.setColor(Color.RED);
            } else {
                shapeRenderer.setColor(Color.WHITE);
            }

            // Draw from left to right
            float x = padding + (i * (squareSize + padding));
            float y = padding;
            shapeRenderer.rect(x, y, squareSize, squareSize);
        }

        shapeRenderer.end();

        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();
        GlyphLayout layout = new GlyphLayout();
        String text = "Player: " + GlobalSettings.playerName;
        layout.setText(font, text);
        float x = uiCamera.viewportWidth - layout.width - 10;
        float y = layout.height + 10;
        font.draw(batch, text, x, y);
        batch.end();
    }

    public void resize(int width, int height) {
        uiCamera.viewportWidth = width;
        uiCamera.viewportHeight = height;
        uiCamera.update();
    }

    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
    }
}
