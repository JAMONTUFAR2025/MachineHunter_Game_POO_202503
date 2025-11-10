package com.machinehunterdev.game.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.machinehunterdev.game.Character.Character;
import com.machinehunterdev.game.Character.EnemyType;

public class BossHealthBar {

    // Referencia al personaje jefe cuya vida se muestra
    private Character boss;
    // Nombre del jefe a mostrar en la barra de vida
    private String bossName;
    // Salud maxima del jefe al inicio
    private float maxHealth;
    // Tipo de enemigo jefe para determinar el color de la barra
    private EnemyType enemyType;

    /**
     * Constructor para la barra de vida del jefe.
     * @param boss El personaje jefe.
     * @param bossName El nombre del jefe.
     * @param enemyType El tipo de enemigo jefe.
     */
    public BossHealthBar(Character boss, String bossName, EnemyType enemyType) {
        this.boss = boss;
        this.bossName = bossName;
        this.maxHealth = boss.getHealth();
        this.enemyType = enemyType;
    }

    /**
     * Dibuja la forma de la barra de vida del jefe.
     * Incluye el fondo, el borde y la barra de vida que representa la salud actual.
     * @param shapeRenderer El ShapeRenderer para dibujar formas.
     * @param viewportWidth Ancho del viewport.
     * @param viewportHeight Alto del viewport.
     */
    public void drawShapes(ShapeRenderer shapeRenderer, float viewportWidth, float viewportHeight) {
        float barWidth = 400;
        float barHeight = 20;
        float x = (viewportWidth - barWidth) / 2;
        float y = viewportHeight - barHeight - 80;

        // Draw black border (slightly larger)
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(x - 2, y - 2, barWidth + 4, barHeight + 4);

        // Draw the background of the health bar
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(x, y, barWidth, barHeight);

        // Draw the health bar with color based on boss type and health
        float healthPercentage = (float) boss.getHealth() / maxHealth;
        Color healthColor;

        if (healthPercentage <= 0.5f) {
            healthColor = Color.RED;
        } else {
            switch (enemyType) {
                case BOSS_GEMINI:
                    healthColor = Color.BLUE;
                    break;
                case BOSS_CHATGPT:
                    healthColor = Color.LIME;
                    break;
                default:
                    healthColor = Color.RED; // Default color
                    break;
            }
        }
        
        shapeRenderer.setColor(healthColor);
        shapeRenderer.rect(x, y, barWidth * healthPercentage, barHeight);

        // Draw a black line in the middle of the bar
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(x + barWidth / 2 - 1, y, 2, barHeight); // 2 pixels wide line
    }

    /**
     * Dibuja el texto (nombre del jefe) sobre la barra de vida.
     * @param batch El SpriteBatch para dibujar texto.
     * @param font La fuente a utilizar.
     * @param viewportWidth Ancho del viewport.
     * @param viewportHeight Alto del viewport.
     */
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

    /**
     * Libera los recursos utilizados por la barra de vida.
     * En este caso, no hay recursos especificos que liberar.
     */
    public void dispose() {
        // No resources to dispose
    }
}