package com.machinehunterdev.game.Dialog;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;
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
    private float autoAdvanceTimer = 0f;
    private float autoAdvanceDelay = 0.5f; // Delay in seconds before auto-advancing

    private List<String> pages;
    private int currentPage;

    private GlyphLayout glyphLayout;

    public DialogManager() {
        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("fonts/OrangeKid64.fnt"));
        font.getData().setScale(0.6f);
        font.setColor(Color.WHITE);
        shapeRenderer = new ShapeRenderer();
        glyphLayout = new GlyphLayout();
        pages = new ArrayList<>();

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
        pages.clear();
        currentPage = 0;

        // Split text into pages
        GlyphLayout layout = new GlyphLayout();
        float targetWidth = dialogBoxWidth - 20;
        float targetHeight = dialogBoxHeight - 20;

        int start = 0;
        while (start < fullText.length()) {
            int end = start;
            while (end < fullText.length()) {
                end++;
                layout.setText(font, fullText.substring(start, end), Color.WHITE, targetWidth, Align.left, true);
                if (layout.height > targetHeight) {
                    end = fullText.lastIndexOf(' ', end - 1);
                    if (end == -1 || end <= start) {
                        end = fullText.indexOf(' ', start);
                        if (end == -1) {
                            end = fullText.length();
                        }
                    }
                    break;
                }
            }
            pages.add(fullText.substring(start, end));
            start = end;
            if (start < fullText.length() && fullText.charAt(start) == ' ') {
                start++;
            }
        }

        startPage();
    }

    private void startPage() {
        currentVisibleText = "";
        textTimer = 0f;
        textFullyVisible = false;
        autoAdvanceTimer = 0f; // Reset auto-advance timer
    }

    public void nextLine() {
        if (textFullyVisible) {
            if (currentPage < pages.size() - 1) {
                currentPage++;
                startPage();
            } else {
                if (currentLineIndex < currentDialog.getLines().size() - 1) {
                    currentLineIndex++;
                    startNewLine();
                } else {
                    dialogActive = false;
                }
            }
        } else {
            currentVisibleText = pages.get(currentPage);
            textFullyVisible = true;
            autoAdvanceTimer = 0f; // Reset timer when skipping
        }
    }

    public void update(float dt) {
        if (!dialogActive) return;

        if (textFullyVisible) {
            autoAdvanceTimer += dt;
            if (autoAdvanceTimer >= autoAdvanceDelay) {
                nextLine();
            }
            return;
        }

        textTimer += dt;
        String fullText = pages.get(currentPage);

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

        glyphLayout.setText(font, currentVisibleText, Color.WHITE, dialogBoxWidth - 20, Align.left, true);

        // ✅ Dibujar texto
        batch.begin();
        font.draw(batch, glyphLayout, dialogBoxX + 10, dialogBoxY + dialogBoxHeight - 10);
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
