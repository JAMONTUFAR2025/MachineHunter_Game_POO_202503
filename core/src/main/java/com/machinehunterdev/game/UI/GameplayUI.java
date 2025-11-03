package com.machinehunterdev.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.machinehunterdev.game.DamageTriggers.WeaponType;
import com.machinehunterdev.game.Gameplay.GlobalSettings;
import com.machinehunterdev.game.Character.Character;

/**
 * Interfaz de usuario que se muestra durante el gameplay.
 * Muestra la barra de salud del jugador y su nombre.
 * 
 * @author MachineHunterDev
 */
import com.machinehunterdev.game.Character.EnemyType;

public class GameplayUI {

    /** Renderizador de formas para la barra de salud */
    private ShapeRenderer shapeRenderer;
    
    /** Cámara dedicada para la interfaz de usuario */
    private OrthographicCamera uiCamera;
    
    /** Fuente para el texto de la interfaz */
    private BitmapFont font;
    
    /** SpriteBatch compartido para renderizado */
    private SpriteBatch batch;

    private Texture laserIcon;
    private Texture ionIcon;
    private Texture railgunIcon;
    private Texture heartTexture;
    private Texture noHeartTexture;
    private Texture pauseIcon;

    /**
     * Constructor de la interfaz de gameplay.
     * @param batch SpriteBatch compartido del juego
     */
    public GameplayUI(SpriteBatch batch) {
        this.batch = batch;
        this.shapeRenderer = new ShapeRenderer();
        this.uiCamera = new OrthographicCamera();
        this.uiCamera.setToOrtho(false, 1280, 720);
        loadCustomBitmapFont();

        laserIcon = new Texture(Gdx.files.internal("UI/LaserIcon.png"));
        ionIcon = new Texture(Gdx.files.internal("UI/IonIcon.png"));
        railgunIcon = new Texture(Gdx.files.internal("UI/RailgunIcon.png"));
        heartTexture = new Texture(Gdx.files.internal("UI/Heart.png"));
        noHeartTexture = new Texture(Gdx.files.internal("UI/Heartbroken.png"));
        pauseIcon = new Texture(Gdx.files.internal("UI/PauseIcon.png"));
    }

    /**
     * Carga la fuente personalizada para la interfaz.
     */
    private void loadCustomBitmapFont() {
        try {
            this.font = new BitmapFont(Gdx.files.internal("fonts/OrangeKid64.fnt"));
            this.font.setColor(Color.WHITE);
        } catch (Exception e) {
            System.err.println("Error al cargar la fuente personalizada. Usando fuente por defecto.");
            this.font = new BitmapFont();
        }
    }

    private BossHealthBar bossHealthBar;

    public void setBoss(Character boss, String bossName, EnemyType enemyType) {
        this.bossHealthBar = new BossHealthBar(boss, bossName, enemyType);
    }

    /**
     * Renderiza la interfaz de gameplay.
     * @param playerHealth Salud actual del jugador
     * @param currentWeapon Arma actual del jugador
     */
    public void draw(int playerHealth, WeaponType currentWeapon, boolean isPlayerInvulnerable) {
        // === Shape Drawing ===
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw boss health bar shapes
        if (bossHealthBar != null) {
            bossHealthBar.drawShapes(shapeRenderer, uiCamera.viewportWidth, uiCamera.viewportHeight);
        }

        shapeRenderer.end();

        // === Sprite & Text Drawing ===
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        // Draw player health hearts
        int totalHealth = 3;
        int heartSize = 64;
        int padding = 10;
        for (int i = 0; i < totalHealth; i++) {
            float x = padding + (i * (heartSize + padding));
            float y = padding;
            if (i < playerHealth) {
                batch.draw(heartTexture, x, y, heartSize, heartSize);
            } else {
                batch.draw(noHeartTexture, x, y, heartSize, heartSize);
            }
        }

        // Draw pause icon
        int pauseIconSize = 64;
        batch.draw(pauseIcon, uiCamera.viewportWidth - pauseIconSize - padding, padding, pauseIconSize, pauseIconSize);

        // Draw boss name text
        if (bossHealthBar != null) {
            bossHealthBar.drawText(batch, font, uiCamera.viewportWidth, uiCamera.viewportHeight);
        }

        // Draw weapon selection icons
        float totalWidth = (3 * 64) + (2 * padding);
        float startX = (uiCamera.viewportWidth - totalWidth) / 2;
        float iconSize = 64;

        if (isPlayerInvulnerable) {
            batch.setColor(1, 1, 1, 0.3f);
            batch.draw(laserIcon, startX, (float)padding, iconSize, iconSize);
            batch.draw(ionIcon, startX + iconSize + padding, (float)padding, iconSize, iconSize);
            batch.draw(railgunIcon, startX + (2 * (iconSize + padding)), (float)padding, iconSize, iconSize);
        } else {
            if (currentWeapon == WeaponType.LASER) {
                batch.setColor(Color.WHITE);
            } else {
                batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
            }
            batch.draw(laserIcon, startX, (float)padding, iconSize, iconSize);

            if (currentWeapon == WeaponType.ION) {
                batch.setColor(Color.WHITE);
            } else {
                batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
            }
            batch.draw(ionIcon, startX + iconSize + padding, (float)padding, iconSize, iconSize);

            if (currentWeapon == WeaponType.RAILGUN) {
                batch.setColor(Color.WHITE);
            } else {
                batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
            }
            batch.draw(railgunIcon, startX + (2 * (iconSize + padding)), (float)padding, iconSize, iconSize);
        }

        batch.setColor(Color.WHITE); // Reset color
        batch.end();
    }

    /**
     * Maneja el redimensionamiento de la ventana.
     * @param width Nuevo ancho de la ventana
     * @param height Nuevo alto de la ventana
     */
    public void resize(int width, int height) {
        uiCamera.viewportWidth = width;
        uiCamera.viewportHeight = height;
        uiCamera.update();
    }

    /**
     * Libera los recursos utilizados por la interfaz.
     */
    public void dispose() {
        shapeRenderer.dispose();
        font.dispose();
        heartTexture.dispose();
        noHeartTexture.dispose();
        pauseIcon.dispose();
    }
}