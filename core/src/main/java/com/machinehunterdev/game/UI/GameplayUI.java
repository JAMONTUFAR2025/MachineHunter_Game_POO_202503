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

/**
 * Interfaz de usuario que se muestra durante el gameplay.
 * Muestra la barra de salud del jugador y su nombre.
 * 
 * @author MachineHunterDev
 */
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

    /**
     * Renderiza la interfaz de gameplay.
     * @param playerHealth Salud actual del jugador
     * @param currentWeapon Arma actual del jugador
     */
    public void draw(int playerHealth, WeaponType currentWeapon) {
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        int totalHealth = 3;
        int squareSize = 50;
        int padding = 10;

        // Dibujar cuadrados de salud (rojos = vida, blancos = perdida)
        for (int i = 0; i < totalHealth; i++) {
            if (i < playerHealth) {
                shapeRenderer.setColor(Color.RED);
            } else {
                shapeRenderer.setColor(Color.WHITE);
            }

            float x = padding + (i * (squareSize + padding));
            float y = padding;
            shapeRenderer.rect(x, y, squareSize, squareSize);
        }

        shapeRenderer.end();

        // Dibujar nombre del jugador y arma actual
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();
        GlyphLayout layout = new GlyphLayout();
        
        String text = "Player: " + GlobalSettings.playerName;
        layout.setText(font, text);
        float x = uiCamera.viewportWidth - layout.width - 10;
        float y = layout.height + 10;
        font.draw(batch, text, x, y);
        batch.end();

        // Dibujar cuadrados de seleccion de arma
        batch.begin();
        float totalWidth = (3 * squareSize) + (2 * padding);
        float startX = (uiCamera.viewportWidth - totalWidth) / 2;

        // Cuadrado para LASER (J)
        if (currentWeapon == WeaponType.LASER) {
            batch.setColor(Color.WHITE);
        } else {
            batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
        }
        batch.draw(laserIcon, startX, padding, squareSize, squareSize);

        // Cuadrado para ION (K)
        if (currentWeapon == WeaponType.ION) {
            batch.setColor(Color.WHITE);
        } else {
            batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
        }
        batch.draw(ionIcon, startX + squareSize + padding, padding, squareSize, squareSize);

        // Cuadrado para RAILGUN (L)
        if (currentWeapon == WeaponType.RAILGUN) {
            batch.setColor(Color.WHITE);
        } else {
            batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
        }
        batch.draw(railgunIcon, startX + (2 * (squareSize + padding)), padding, squareSize, squareSize);
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
    }
}