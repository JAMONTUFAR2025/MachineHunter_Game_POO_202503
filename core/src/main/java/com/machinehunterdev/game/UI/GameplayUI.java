package com.machinehunterdev.game.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class GameplayUI {

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera uiCamera;

    public GameplayUI() {
        this.shapeRenderer = new ShapeRenderer();
        this.uiCamera = new OrthographicCamera();
        this.uiCamera.setToOrtho(false, 1280, 720); // Assuming a fixed resolution for the UI
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

            // Draw from right to left
            float x = uiCamera.viewportWidth - padding - squareSize - (i * (squareSize + padding));
            float y = uiCamera.viewportHeight - squareSize - padding;
            shapeRenderer.rect(x, y, squareSize, squareSize);
        }

        shapeRenderer.end();
    }

    public void resize(int width, int height) {
        uiCamera.viewportWidth = width;
        uiCamera.viewportHeight = height;
        uiCamera.update();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
