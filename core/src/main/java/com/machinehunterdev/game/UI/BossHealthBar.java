package com.machinehunterdev.game.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.machinehunterdev.game.Character.Character;

public class BossHealthBar {

    private Character boss;
    private String bossName;

    public BossHealthBar(Character boss, String bossName) {
        this.boss = boss;
        this.bossName = "Gemini.exe";
    }

    public void drawShapes(ShapeRenderer shapeRenderer, float viewportWidth, float viewportHeight) {
        float barWidth = 400;
        float barHeight = 20;
        float x = (viewportWidth - barWidth) / 2;
        float y = viewportHeight - barHeight - 80;

        // Draw black border (slightly larger)
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(x - 2, y - 2, barWidth + 4, barHeight + 4);

        // Draw the full bar in red (representing max health)
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(x, y, barWidth, barHeight);

        // Draw a semi-transparent black rectangle over the empty portion
        float healthPercentage = (float) boss.getHealth() / 100;
        if (healthPercentage < 1.0f) {
            shapeRenderer.setColor(0, 0, 0, 0.5f); // Semi-transparent black
            shapeRenderer.rect(x + (barWidth * healthPercentage), y, barWidth * (1 - healthPercentage), barHeight);
        }
    }

    public void drawText(SpriteBatch batch, BitmapFont font, float viewportWidth, float viewportHeight) {
        float barWidth = 400;
        float barHeight = 20;
        float x = (viewportWidth - barWidth) / 2;
        float y = viewportHeight - barHeight - 20;

        font.setColor(Color.WHITE);
        GlyphLayout layout = new GlyphLayout(font, bossName);
        float fontX = x + (barWidth - layout.width) / 2;
        float fontY = y + barHeight + layout.height - 30;
        font.draw(batch, bossName, fontX, fontY);
    }

    public void dispose() {
        // No resources to dispose
    }
}