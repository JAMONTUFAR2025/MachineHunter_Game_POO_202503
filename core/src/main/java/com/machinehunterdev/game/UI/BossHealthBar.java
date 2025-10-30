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
        float y = viewportHeight - barHeight - 100;
        shapeRenderer.rect(x, y, barWidth, barHeight);

        // Draw the health
        float healthPercentage = (float) boss.getHealth() / 100;
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(x, y, barWidth * healthPercentage, barHeight);
    }

    public void drawText(SpriteBatch batch, BitmapFont font, float viewportWidth, float viewportHeight) {
        float barWidth = 400;
        float barHeight = 20;
        float x = (viewportWidth - barWidth) / 2;
        float y = viewportHeight - barHeight - 20;

        font.setColor(Color.WHITE);
        GlyphLayout layout = new GlyphLayout(font, bossName);
        float fontX = x + (barWidth - layout.width) / 2;
        float fontY = y + barHeight + layout.height + 5;
        font.draw(batch, bossName, fontX, fontY);
    }

    public void dispose() {
        // No resources to dispose
    }
}