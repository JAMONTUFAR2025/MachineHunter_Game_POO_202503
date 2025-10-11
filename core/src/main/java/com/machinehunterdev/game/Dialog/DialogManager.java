package com.machinehunterdev.game.Dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class DialogManager {
        private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private Dialog currentDialog; // ✅ Aseguramos que usamos Dialog
    private int currentLineIndex;
    private boolean dialogActive;

    // ✅ Tamaño fijo del cuadro de diálogo
    private float dialogBoxWidth = 440;
    private float dialogBoxHeight = 100;
    private float dialogBoxX;
    private float dialogBoxY;

    private ScreenViewport uiViewport;

    // ✅ Variables para efecto de escritura
    private String currentVisibleText = "";
    private float textTimer = 0f;
    private float textSpeed = 0.05f;
    private boolean textFullyVisible = false;

    public DialogManager() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        shapeRenderer = new ShapeRenderer();

        uiViewport = new ScreenViewport();
        uiViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        updateDialogPosition();

        dialogActive = false;
    }

    private void updateDialogPosition() {
        dialogBoxX = (Gdx.graphics.getWidth() - dialogBoxWidth) / 2f;
        dialogBoxY = Gdx.graphics.getHeight() - dialogBoxHeight - 20;
    }

    public void showDialog(Dialog dialog) {
        currentDialog = dialog;
        currentLineIndex = 0;
        dialogActive = true;
        startNewLine();
    }

    private void startNewLine() {
        String fullText = currentDialog.getLines().get(currentLineIndex);
        currentVisibleText = "";
        textTimer = 0f;
        textFullyVisible = false;
    }

    public void nextLine() {
        if (textFullyVisible) {
            if (currentLineIndex < currentDialog.getLines().size() - 1) {
                currentLineIndex++;
                startNewLine();
            } else {
                dialogActive = false;
            }
        } else {
            currentVisibleText = currentDialog.getLines().get(currentLineIndex);
            textFullyVisible = true;
        }
    }

    public void update(float dt) {
        if (!dialogActive || textFullyVisible) return;

        textTimer += dt;
        String fullText = currentDialog.getLines().get(currentLineIndex);

        int charIndex = (int)(textTimer / textSpeed);
        if (charIndex < fullText.length()) {
            currentVisibleText = fullText.substring(0, charIndex + 1);
        } else {
            currentVisibleText = fullText;
            textFullyVisible = true;
        }
    }

    public boolean isDialogActive() {
        return dialogActive;
    }

    public void render() {
        if (!dialogActive) return;
        
        uiViewport.apply(true);

        shapeRenderer.setProjectionMatrix(uiViewport.getCamera().combined);
        batch.setProjectionMatrix(uiViewport.getCamera().combined);

        // ✅ Dibujar fondo del cuadro de diálogo (negro semi-transparente)
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.8f));
        shapeRenderer.rect(dialogBoxX, dialogBoxY, dialogBoxWidth, dialogBoxHeight);
        shapeRenderer.end();

        // ✅ Dibujar borde del cuadro de diálogo
        shapeRenderer.begin(ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(dialogBoxX, dialogBoxY, dialogBoxWidth, dialogBoxHeight);
        shapeRenderer.end();

        // ✅ Dibujar texto
        batch.begin();
        font.draw(batch, currentVisibleText, dialogBoxX + 10, dialogBoxY + dialogBoxHeight - 10);
        batch.end();
    }

    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
        updateDialogPosition();
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}
