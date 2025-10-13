package com.machinehunterdev.game.Dialog;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class DialogManager {
    private SpriteBatch batch;
    private BitmapFont font;
    private Dialog currentDialog;
    private int currentLineIndex;
    private boolean dialogActive;

    private float dialogBoxWidth = 440;
    private float dialogBoxHeight = 100;
    private float dialogBoxX;
    private float dialogBoxY;

    private ScreenViewport uiViewport;

    private String currentVisibleText = "";
    private float textTimer = 0f;
    private float textSpeed = 0.05f;
    private boolean textFullyVisible = false;
    private float autoAdvanceTimer = 0f;
    private float autoAdvanceDelay = 0.5f;

    private List<String> pages;
    private int currentPage;

    private GlyphLayout glyphLayout;

    private Texture backgroundTexture;
    private Texture borderTexture;

    public DialogManager(SpriteBatch batch) {
        this.batch = batch;
        font = new BitmapFont(Gdx.files.internal("fonts/OrangeKid64.fnt"));
        font.getData().setScale(0.6f);
        font.setColor(Color.WHITE);
        glyphLayout = new GlyphLayout();
        pages = new ArrayList<>();

        // Create background texture
        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(0, 0, 0, 0.8f);
        bgPixmap.fill();
        backgroundTexture = new Texture(bgPixmap);
        bgPixmap.dispose();

        // Create border texture
        Pixmap borderPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        borderPixmap.setColor(Color.WHITE);
        borderPixmap.fill();
        borderTexture = new Texture(borderPixmap);
        borderPixmap.dispose();

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
        autoAdvanceTimer = 0f;
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
            autoAdvanceTimer = 0f;
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
        batch.setProjectionMatrix(uiViewport.getCamera().combined);

        batch.begin();

        // Draw dialog box background
        batch.draw(backgroundTexture, dialogBoxX, dialogBoxY, dialogBoxWidth, dialogBoxHeight);

        // Draw dialog box border
        batch.draw(borderTexture, dialogBoxX, dialogBoxY, dialogBoxWidth, 1); // Top
        batch.draw(borderTexture, dialogBoxX, dialogBoxY + dialogBoxHeight - 1, dialogBoxWidth, 1); // Bottom
        batch.draw(borderTexture, dialogBoxX, dialogBoxY, 1, dialogBoxHeight); // Left
        batch.draw(borderTexture, dialogBoxX + dialogBoxWidth - 1, dialogBoxY, 1, dialogBoxHeight); // Right

        glyphLayout.setText(font, currentVisibleText, Color.WHITE, dialogBoxWidth - 20, Align.left, true);

        // Draw text
        font.draw(batch, glyphLayout, dialogBoxX + 10, dialogBoxY + dialogBoxHeight - 10);
        
        batch.end();
    }

    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
        updateDialogPosition();
    }

    public void dispose() {
        font.dispose();
        backgroundTexture.dispose();
        borderTexture.dispose();
    }
}